package Registrar;

import User.UserImpl;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface Registrar extends Remote {
    void generateSecretKey() throws RemoteException, NoSuchAlgorithmException;
    LocalDate getDate() throws RemoteException;
    void nextDay() throws RemoteException;
    //methodes voor user
    void connectUser(UserImpl user) throws RemoteException;
    ArrayList<byte[]> enrollUser(String phone) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException;
}
