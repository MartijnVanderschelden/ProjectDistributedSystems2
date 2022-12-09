package MatchingService;

import java.lang.reflect.Array;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface MatchingService extends Remote {
    void retrieveCapsules(ArrayList<String> capsules) throws RemoteException;
}
