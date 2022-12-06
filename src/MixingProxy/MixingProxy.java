package MixingProxy;

import User.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.*;

public interface MixingProxy extends Remote {
    void connectToServer() throws RemoteException;
    void retrieveCapsule(User user, String string) throws RemoteException;
}
