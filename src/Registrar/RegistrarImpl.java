package Registrar;

import CateringFacility.CateringFacility;
import User.User;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.util.*;
import java.security.*;

public class RegistrarImpl extends UnicastRemoteObject implements Registrar{
    private byte[] s;
    //2 salt waarden
    private byte[] salt;
    private byte[] iv;
    private LocalDate date;

    private List<CateringFacility> cateringFacilities;
    private ArrayList<User> users;
    //Key=phone
    private Map<String, ArrayList<byte[]>> userTokensMap;
    private Map<Long, ArrayList<byte[]>> pseudonyms;
    private Map<String, ArrayList<byte[]>> signedTokensMap;
    private PrivateKey privateKey;
    private PublicKey publicKey;


    public RegistrarImpl() throws RemoteException, NoSuchAlgorithmException {
        this.date = LocalDate.now();
        users = new ArrayList<>();
        userTokensMap = new HashMap<>();
        signedTokensMap = new HashMap<>();
        cateringFacilities = new ArrayList<>();
        generateSecretKey();
        generateSalt();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey= keyPair.getPublic();
    }

    public ArrayList<byte[]> generateUserTokens(String phone) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        ArrayList<byte[]> tokens = new ArrayList<>();
        // Digitale Handtekeningen
        Signature signature = Signature.getInstance("SHA256WithDSA");
        for (int i = 0; i < 48; i++) {
            SecureRandom secureRandom = new SecureRandom();
            signature.initSign(privateKey, secureRandom);
            signature.update(Byte.parseByte(phone));
            tokens.add(i, signature.sign());
        }

        //Handtekening

        return tokens;
        //Handtekening controleren
        //signatureVerify.initVerify(keyPair.getPublic());
        //signatureVerify.update(Byte.parseByte(phone));
    }
    public void generateSecretKey() throws RemoteException, NoSuchAlgorithmException {
        //https://www.baeldung.com/java-secret-key-to-string#:~:text=There%20are%20two%20ways%20for,Generator%20like%20the%20SecureRandom%20class.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(192);
        this.s = keyGenerator.generateKey().getEncoded();
        System.out.println("Secret Key has been generated for Registrar");
    }

    public static String convertSecretKeyToString(SecretKey secretKey) {
        byte[] rawData = secretKey.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        return encodedKey;
    }

    public static SecretKey convertStringToSecretKey(String encodedKey) throws UnsupportedEncodingException {
        byte[] decodedKey = encodedKey.getBytes("UTF-8");
        //byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }

    public void generateSalt(){
        SecureRandom random = new SecureRandom();
        this.salt = new byte[16];
        this.iv = new byte[16];
        random.nextBytes(salt);
        random.nextBytes(iv);
    }
    @Override
    public LocalDate getDate() throws RemoteException {
        return this.date;
    }

    @Override
    public void nextDay() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException, InvalidAlgorithmParameterException {
        this.date = date.plusDays(1);
        //bij nieuwe dag moeten users nieuwe tokens krijgen
        for(User u : users) {
            userTokensMap.get(u.getPhone()).clear();
            for(int i=0;i<48;i++) {
                ArrayList<byte[]> generatedTokens = generateUserTokens(u.getPhone());
                userTokensMap.get(u.getPhone()).add(generatedTokens.get(i));
                u.retrieveTokens(generatedTokens);
            }
        }

        for (CateringFacility cf :
                cateringFacilities) {
            //elke dag moet de registrar een nieuwe sCF genereren voor elke catering
            //en daaruit een pseudoniem afleiden voor elke catering
            cf.requestDailyPseudonym();
            //er wordt ook een nieuw random number R_i gegenereerd
            cf.generateDailyRandomNumber();
            //daarna moeten er nieuwe qr-codes gegenereerd worden
            cf.generateQRcode();
        }

    }
    //user methodes
    @Override
    public void enrollUser(User user) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        users.add(user);
        System.out.println(user.getName()+ " has been enrolled.");
        // 48 Tokens toekennen aan user en in map steken
        ArrayList<byte[]> generatedTokens = generateUserTokens(user.getPhone());
        for(int i=0; i<48; i++){
            userTokensMap.put(user.getPhone(),generatedTokens);
        }
        user.retrieveTokens(generatedTokens);
    }
    @Override
    public PublicKey getPublicKey() throws RemoteException {
        return publicKey;
    }

    @Override
    public boolean checkToken(PublicKey publicKey, User user, byte[] signedToken) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        //1. kijken of token bestaat in de usertokensmap
        //System.out.println("size" + userTokensMap.get(user.getPhone()).size() + "tokens: " + userTokensMap.get(user.getPhone()));
        boolean tokenExists = userTokensMap.get(user.getPhone()).contains(signedToken);
        //System.out.println("token exists: " + tokenExists);
        if (tokenExists){
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(Byte.parseByte(user.getPhone()));
            System.out.println("token is autentiek" + signature.verify(signedToken));
            return signature.verify(signedToken);

        } else return false;
    }
    /*
    Catering methodes
     */
    @Override
    public String[] checkAuthenticityCatering(CateringFacility newCatering) throws RemoteException {
        String[] unique  = new String[4];
        for(CateringFacility cf : cateringFacilities){
            if(cf.getBusinessNumber() == newCatering.getBusinessNumber()){
                unique[0] = Long.toString(newCatering.getBusinessNumber());
            }
            if(cf.getFacilityName().equals(newCatering.getFacilityName())){
                unique[1] = newCatering.getFacilityName();
            }
            if(cf.getLocation().equals(newCatering.getLocation())){
                unique[2] = newCatering.getLocation();
            }
            if(cf.getPhoneNumber() == newCatering.getPhoneNumber()){
                unique[3] = Long.toString(newCatering.getPhoneNumber());
            }
        }
        return unique;
    }

    @Override
    public void enrollCatering(CateringFacility cf) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        cateringFacilities.add(cf);
        System.out.println(cf.getFacilityName()+ " has been enrolled.");
        cf.requestDailyPseudonym();
        cf.generateDailyRandomNumber();
        cf.generateQRcode();
    }

    @Override
    public void disconnectCatering(CateringFacility cf) throws RemoteException {
        cateringFacilities.remove(cf);
        System.out.println(cf.getFacilityName()+ " has been deleted.");
    }

    public byte[] deriveDailySecretKey(long CF) throws RemoteException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        /*
        s_(CF, day_i) = KDF(s, CF, day_i)
         */
        // 1) Constructie van een byte[] die CF en day_i bevat
        byte[] arguments = (String.valueOf(CF) + "|" + String.valueOf(date)).getBytes();

        // 2) KDF berekenen
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        KeySpec spec = new PBEKeySpec("password".toCharArray(), salt, 65536, 256); // AES-256
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = f.generateSecret(spec).getEncoded();
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, ivspec);
        return c.doFinal(arguments);

        /*
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, convertStringToSecretKey(s.toString()));
        return cipher.doFinal(arguments);

         */
    }
    @Override
    public byte[] calculateDailyPseudonym(long CF, String location) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] sCF = deriveDailySecretKey(CF);
        byte[] arguments = (location + ";" + date).getBytes();
        sha.update(sCF);
        System.out.println("Daily pseudonym has been calculated at registrar.");
        return sha.digest(arguments);
    }
}
