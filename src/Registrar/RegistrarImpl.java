package Registrar;

import User.User;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;
import java.security.*;

public class RegistrarImpl extends UnicastRemoteObject implements Registrar{
    private SecretKey s;
    private LocalDate date;
    private SecretKey dailySecretKey;
    private ArrayList<User> users = new ArrayList<User>();
    //Key=phone
    private Map<String, ArrayList<byte[]>> userTokensMap;
    private Map<String, ArrayList<byte[]>> signedTokens;


    public RegistrarImpl() throws RemoteException, NoSuchAlgorithmException {
        //UnicastRemoteObject.exportObject(this, 0);
        this.date = LocalDate.now();
        generateSecretKey();
        users = new ArrayList<>();
        userTokensMap = new HashMap<>();
        signedTokens = new HashMap<>();
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
        this.s = keyGenerator.generateKey();
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
}
