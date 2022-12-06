package User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface User extends Remote{
    String getPhone() throws RemoteException;
    String getName() throws RemoteException;
    void retrieveTokens(List<byte[]> tokens) throws RemoteException;
    void scanQR(UserImpl user, String qr) throws RemoteException;
}

