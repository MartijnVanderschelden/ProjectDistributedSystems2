package Registrar;

import CateringFacility.CateringFacility;
import User.User;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.*;
import java.security.*;

public class RegistrarImpl extends UnicastRemoteObject implements Registrar{
    private byte[] s;
    private byte[] salt;
    private LocalDate date;

    private ArrayList<CateringFacility> cateringFacilities;
    private ArrayList<User> users;
    //Key=phone
    private Map<String, ArrayList<byte[]>> userTokensMap;
    private Map<String, ArrayList<byte[]>> signedTokensMap;


    public RegistrarImpl() throws RemoteException, NoSuchAlgorithmException {
        this.date = LocalDate.now();
        generateSecretKey();
        generateSalt();
        users = new ArrayList<>();
        userTokensMap = new HashMap<>();
        signedTokensMap = new HashMap<>();
        cateringFacilities = new ArrayList<>();
    }

    public static ArrayList<byte[]> generateUserTokens(String phone) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        ArrayList<byte[]> tokens = new ArrayList<>();
        // Digitale Handtekeningen
        Signature signature = Signature.getInstance("SHA256WithDSA");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        for (int i = 0; i < 48; i++) {
            SecureRandom secureRandom = new SecureRandom();
            signature.initSign(keyPair.getPrivate(), secureRandom);
            signature.update(Byte.parseByte(phone));
            tokens.add(i, signature.sign());
        }

        //Handtekening

        return tokens;
        //Handtekening controleren
        //signatureVerify.initVerify(keyPair.getPublic());
        //signatureVerify.update(Byte.parseByte(phone));
    }


    @Override
    public void generateSecretKey() throws RemoteException, NoSuchAlgorithmException {
        //https://www.baeldung.com/java-secret-key-to-string#:~:text=There%20are%20two%20ways%20for,Generator%20like%20the%20SecureRandom%20class.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(192);
        this.s = keyGenerator.generateKey().getEncoded();
    }

    @Override
    public void generateSalt() throws RemoteException{
        SecureRandom random = new SecureRandom();
        salt = new byte[124];
        random.nextBytes(salt);
    }

    public static String convertSecretKeyToString(SecretKey secretKey) {
        byte[] rawData = secretKey.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        return encodedKey;
    }

    public static SecretKey convertStringToSecretKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }

    @Override
    public LocalDate getDate() throws RemoteException {
        return this.date;
    }

    @Override
    public void nextDay() throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
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
    }

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

    /*
    Catering methodes
     */
    @Override
    public void enrollCatering(CateringFacility cf) throws RemoteException {
        cateringFacilities.add(cf);
        System.out.println(cf.getFacilityName()+ " has been enrolled.");
        System.out.println(cateringFacilities);
    }

    @Override
    public byte[] deriveDailySecretKey(long CF, LocalDate date) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
        //CF is het business number
        /*
        Constructie van key derivation function s_(CF,day_i) = KDF(s, CF, day_i)
         */
        /*
        Gebruikmakend van het PBKDF2WithHmacSHA256 algoritme wordt een key derivation function geconstrueerd
        Argumenten:
        - char [] password: concatenatie van s, CF and day_i; onderscheiden van elkaar met ","
        - byte[] salt: wordt gegenereerd bij het opzetten van de registrar
        - int iterationCount
        - int keyLength
         */

        /*
        // 1) Argumenten omzetten naar String
        String uniqueIdentifier = String.valueOf(CF);
        String day = String.valueOf(date);
        char[] arguments = (uniqueIdentifier + "," + day).toCharArray();

        // aantal iteraties
        int count = 1000;

        // 2) KDF construeren met bijhorende parameters
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(arguments);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        return pbeKey.getEncoded();

         */
        return null;
    }

    @Override
    public byte[] calculateDailyPseudonym(long CF, LocalDate date, String location) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] sCF = deriveDailySecretKey(CF, date);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.write(location.getBytes());
        dos.write(sCF);
        dos.write(date.toString().getBytes());
        dos.flush();
        dos.close();
        baos.close();

        return sha.digest(baos.toByteArray());
    }
}
