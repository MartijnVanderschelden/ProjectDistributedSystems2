package MatchingService;

import Registrar.Registrar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class MatchingServiceImpl extends UnicastRemoteObject implements MatchingService {
    private Registrar registrar;
    public ObservableList<String> capsulesList;
    public MatchingServiceImpl(Registrar r) throws RemoteException {
        capsulesList = FXCollections.observableArrayList();
        registrar = r;
    }
    @Override
    public void retrieveCapsules(String capsule) throws RemoteException {
        capsulesList.add(capsule);
    }
}
