package MixingProxy;

import User.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;

public interface MixingProxy extends Remote {
    void connectToServer() throws RemoteException;
    boolean retrieveCapsule(User user, LocalDate ldt, String qr, byte[] userToken) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException;
    void retrieveExitCapsule(User user, LocalDate ldt, String qr, byte[] usertoken) throws RemoteException;
    void flush() throws RemoteException;
}
