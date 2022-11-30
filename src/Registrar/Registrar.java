package Registrar;

import javax.crypto.SecretKey;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.time.LocalDate;

public interface Registrar extends Remote {

    void generateSecretKey() throws RemoteException, NoSuchAlgorithmException;
    LocalDate getDate() throws RemoteException;
    void nextDay() throws RemoteException;
}
