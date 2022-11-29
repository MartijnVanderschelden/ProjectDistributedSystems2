package Registrar;

import CateringFacility.CateringFacilityImpl;
import User.UserImpl;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.Base64;

public class RegistrarImpl implements Registrar{
    SecretKey s;
    LocalDate date;
    UserImpl user;
    CateringFacilityImpl cateringFacility;

    public RegistrarImpl() throws RemoteException, NoSuchAlgorithmException {
        UnicastRemoteObject.exportObject(this, 0);
        generateSecretKey();
        this.date = LocalDate.now();
    }

    public void generateSecretKey() throws NoSuchAlgorithmException {
        //https://www.baeldung.com/java-secret-key-to-string#:~:text=There%20are%20two%20ways%20for,Generator%20like%20the%20SecureRandom%20class.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(192);
        this.s = keyGenerator.generateKey();
    }

    public static String convertSecretKeyToString(SecretKey secretKey) {
        byte[] rawData = secretKey.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        return encodedKey;
    }

    public static SecretKey convertStringToSecretKeyto(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }
    @Override
    public LocalDate getDate() throws RemoteException {
        return this.date;
    }

    @Override
    public void nextDay() throws RemoteException {
        this.date = date.plusDays(1);
    }

    @Override
    public PublicKey enrollUser(int telefoonnummer) throws RemoteException {
        return null;
    }

    @Override
    public SecretKey enrollFacility(String cf) throws RemoteException {
        return null;
    }
}
