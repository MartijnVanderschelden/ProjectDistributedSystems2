package Registrar;

import CateringFacility.CateringFacility;
import MatchingService.MatchingService;
import User.User;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.security.*;

public class RegistrarImpl extends UnicastRemoteObject implements Registrar{
    MatchingService matchingService;
    private byte[] s;
    //2 salt waarden
    private byte[] salt;
    private byte[] iv;
    private LocalDate date;
    private int count;

    private List<CateringFacility> cateringFacilities;
    private ArrayList<User> users;
    //Key=phone
    private Map<String, List<byte[]>> userTokensMap;
    private Map<LocalDate, ArrayList<byte[]>> pseudonymsPerDay;
    private Map<String, List<byte[]>> signedTokensMap;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private ArrayList<String> criticalTokens;
    private Map<String, String> allTokensToPhoneNumber;


    public RegistrarImpl() throws RemoteException, NoSuchAlgorithmException {
        this.date = LocalDate.now();
        this.count = 0;
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
        this.pseudonymsPerDay = new HashMap<>();
        pseudonymsPerDay.put(this.date, new ArrayList<>());
        this.criticalTokens = new ArrayList<>();
        this.allTokensToPhoneNumber = new HashMap<>();
    }

    public ArrayList<byte[]> generateUserTokens(String phone) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        ArrayList<byte[]> tokens = new ArrayList<>();
        // Digitale Handtekeningen

        for (int i = 0; i < 48; i++) {
            Signature signature = Signature.getInstance("SHA256WithDSA");
            SecureRandom secureRandom = new SecureRandom();
            signature.initSign(privateKey, secureRandom);
            signature.update(phone.getBytes(StandardCharsets.UTF_8));
            byte[] digitalSignature = signature.sign();
            tokens.add(i, digitalSignature);
        }

        //signatureVerify.update(Byte.parseByte(phone));
        return tokens;
    }
    public void generateSecretKey() throws RemoteException, NoSuchAlgorithmException {
        //https://www.baeldung.com/java-secret-key-to-string#:~:text=There%20are%20two%20ways%20for,Generator%20like%20the%20SecureRandom%20class.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(192);
        this.s = keyGenerator.generateKey().getEncoded();
        System.out.println("Secret Key s has been generated for Registrar");
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
        //dag verder -> nieuwe arraylist toevoegen aan pseudonyms per day
        pseudonymsPerDay.put(this.date, new ArrayList<>());

        //bij nieuwe dag moeten users nieuwe tokens krijgen
        // EN User informeren van blootstelling aan geinfecteerde persoon
        for(User u : users) {
              userTokensMap.get(u.getPhone()).clear();
            ArrayList<byte[]> newGeneratedTokens = generateUserTokens(u.getPhone());
            for(int i=0;i<48;i++) {
                userTokensMap.get(u.getPhone()).add(newGeneratedTokens.get(i));
                allTokensToPhoneNumber.put(DatatypeConverter.printHexBinary(newGeneratedTokens.get(i)), u.getPhone());
            }
            u.newDay(newGeneratedTokens, matchingService.getUninformedTokens(), date);
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
        // Bij nieuwe dag moeten de capsules van mixing naar matching verstuurd worden

        // Elke dag worden de pseudonyms van de dag voordien door matching service gedownload
        ArrayList<byte[]> pseudonyms = pseudonymsPerDay.get(date.minusDays(1));
        matchingService.downloadPseudonymsOfYesterday(pseudonyms);

        //Verkrijg uninformedTokens van een dag oud en print uit
        ArrayList<String[]> uninformedTokens = matchingService.getUninformedTokens();
        ArrayList<String> usersToInform = new ArrayList<>();
        ArrayList<String> informedTokens = new ArrayList<>();
        String dateToken;
        for(String[] u : uninformedTokens){
            String token = u[0];
            dateToken = u[1];

            if(LocalDateTime.parse(dateToken).toLocalDate().isBefore(date.minusDays(2))){
                usersToInform.add(allTokensToPhoneNumber.get(token));
            }
            informedTokens.add(token);
        }

        if (!usersToInform.isEmpty()) {
            System.out.println("Volgende gebruikers moeten nog gecontacteerd worden omdat ze in contact zijn gekomen met een positief getest persoon: ");
        }
        for (String userPhone : usersToInform) {
            System.out.println("* " + userPhone);
        }
        matchingService.informedTokens(informedTokens);
    }

    //user methodes
    @Override
    public void enrollUser(User user) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        users.add(user);
        System.out.println(user.getName()+ " has been enrolled.");
        // 48 Tokens toekennen aan user en in map steken
        ArrayList<byte[]> generatedTokens = generateUserTokens(user.getPhone());
        userTokensMap.put(user.getPhone(),generatedTokens);

        for(int i=0; i<48; i++){
            allTokensToPhoneNumber.put(DatatypeConverter.printHexBinary(generatedTokens.get(i)), user.getPhone());
        }

        user.newDay(generatedTokens, matchingService.getUninformedTokens(), date);
    }
    @Override
    public PublicKey getPublicKey() throws RemoteException {
        return publicKey;
    }

    @Override
    public void setMatchingService(MatchingService ms) throws RemoteException {
        this.matchingService = ms;
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
        Gebaseerd op: https://stackoverflow.com/questions/29354133/how-to-fix-invalid-aes-key-length
         */
        /*
        s_(CF, day_i) = KDF(s, CF, day_i)
         */
        // 1) Constructie van een byte[] die CF en day_i bevat
        byte[] arguments = (String.valueOf(s) + "|" + String.valueOf(CF) + "|" + String.valueOf(date)).getBytes();

        // 2) KDF berekenen
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        KeySpec spec = new PBEKeySpec("password".toCharArray(), salt, 65536, 256); // AES-256
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = f.generateSecret(spec).getEncoded();
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, ivspec);
        return c.doFinal(arguments);
    }
    @Override
    public byte[] calculateDailyPseudonym(long CF, String location) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] sCF = deriveDailySecretKey(CF);
        byte[] arguments = (location + ";" + date).getBytes();
        sha.update(sCF);

        CateringFacility catering = null;
        for (CateringFacility cf :
                cateringFacilities) {
            if (cf.getBusinessNumber() == CF) catering = cf;
        }
        System.out.println("Daily pseudonym has been calculated at registrar for " + catering.getFacilityName() + ".");
        byte[] pseudonym = sha.digest(arguments);
        pseudonymsPerDay.get(this.date).add(pseudonym);
        return pseudonym;
    }

    @Override
    public void tokenInformed(String userToken) throws RemoteException{
        criticalTokens.remove(userToken);
    }

    @Override
    public void warnCatering(String cateringBN, String timeFrom, String timeUntil) throws RemoteException{
        for(CateringFacility c : cateringFacilities){
            if(c.getBusinessNumber() == Integer.valueOf(cateringBN)){
                c.receiveWarning("A person that recently tested positive visited your business from " + timeFrom + " till " + timeUntil);
            }
        }
    }
}
