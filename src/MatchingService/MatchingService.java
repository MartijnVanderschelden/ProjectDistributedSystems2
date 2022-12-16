package MatchingService;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.ArrayList;

public interface MatchingService extends Remote {
    void retrieveCapsules(String capsule) throws RemoteException;

    void downloadPseudonymsOfYesterday(ArrayList<byte[]> pseudonyms) throws RemoteException;

    void positiveUser(ArrayList<String> logs, byte[] signature, PublicKey doctorPK) throws Exception;

    void informedTokens(ArrayList<String> userTokens) throws RemoteException;

    ArrayList<String[]> getUninformedTokens() throws RemoteException;

    LocalDate getDate() throws RemoteException;
}
