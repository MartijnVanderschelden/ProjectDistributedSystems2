package MatchingService;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface MatchingService extends Remote {
    void retrieveCapsules(String capsule) throws RemoteException;

    void downloadPseudonymsOfYesterday(ArrayList<byte[]> pseudonyms) throws RemoteException;

}
