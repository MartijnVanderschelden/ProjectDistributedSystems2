package Doctor;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Doctor extends Remote{
    void connectToServer() throws RemoteException;
    int getId() throws RemoteException;
}
