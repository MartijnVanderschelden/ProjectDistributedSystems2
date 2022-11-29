package User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface User extends Remote{
    void start() throws RemoteException;
}

