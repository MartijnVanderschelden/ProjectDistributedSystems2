package Registrar;

import javax.crypto.SecretKey;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.time.LocalDate;

public interface Registrar extends Remote {
    LocalDate getDate() throws RemoteException;

    void nextDay() throws RemoteException;

    PublicKey enrollUser(int telefoonnummer) throws RemoteException;

    SecretKey enrollFacility(String cf) throws RemoteException;
}
