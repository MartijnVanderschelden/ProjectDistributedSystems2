package MixingProxy;

import User.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.*;

public interface MixingProxy extends Remote {
    void connectToServer() throws RemoteException;
    String retrieveCapsule(User user, String capsule) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException;
}
