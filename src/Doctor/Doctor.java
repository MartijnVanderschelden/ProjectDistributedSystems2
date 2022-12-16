package Doctor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;

public interface Doctor extends Remote{
    // Methode om logs te verifieren
    byte[] signLogs(ArrayList<String> logs, String userName) throws Exception, RemoteException;

    ArrayList<String> readLogs(String userName) throws RemoteException;

    void sendToMatchingService(ArrayList<String> logs, byte[] signature) throws RemoteException, Exception;
}
