package MatchingService;

import Registrar.Registrar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jdk.dynalink.linker.LinkerServices;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class MatchingServiceImpl extends UnicastRemoteObject implements MatchingService {
    private Registrar registrar;
    public ObservableList<String> capsulesList;
    private List<byte[]> pseudonymsPreviousDay;

    public MatchingServiceImpl(Registrar r) throws RemoteException {
        capsulesList = FXCollections.observableArrayList();
        registrar = r;
        registrar.setMatchingService(this);
    }
    @Override
    public void retrieveCapsules(String capsule) throws RemoteException {
        capsulesList.add(capsule);
    }

    @Override
    public void downloadPseudonymsOfYesterday(ArrayList<byte[]> pseudonyms) throws RemoteException {
        System.out.println(pseudonyms.size() + " pseudonyms were downloaded of yesterday.");
        this.pseudonymsPreviousDay = pseudonyms;
    }


}
