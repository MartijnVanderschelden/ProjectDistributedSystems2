package MatchingService;

import javafx.collections.ObservableList;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatchingService extends Remote {
    void retrieveCapsules(String capsule) throws RemoteException;
}
