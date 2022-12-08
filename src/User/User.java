package User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;

public interface User extends Remote{
    String getPhone() throws RemoteException;
    String getName() throws RemoteException;
    void retrieveTokens(List<byte[]> tokens) throws RemoteException;
    String scanQR(UserImpl user, String qr) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException;
}

