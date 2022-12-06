package Registrar;

import CateringFacility.CateringFacility;
import User.User;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;

public interface Registrar extends Remote {
    void generateSecretKey() throws RemoteException, NoSuchAlgorithmException;

    void generateSalt() throws RemoteException, NoSuchAlgorithmException;
    LocalDate getDate() throws RemoteException;
    void nextDay() throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException;
    //methodes voor user
    void enrollUser(User user) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException;
    //methodes voor catering
    void enrollCatering(CateringFacility cf) throws RemoteException;

    byte[] deriveDailySecretKey(long CF, LocalDate date) throws RemoteException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException;

    byte[] calculateDailyPseudonym(long CF, LocalDate date, String location) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException;
}
