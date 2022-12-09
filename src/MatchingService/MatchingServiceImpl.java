package MatchingService;

import Registrar.Registrar;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class MatchingServiceImpl implements MatchingService {
    Registrar registrar;
    private ArrayList<String> capsulesList;
    public MatchingServiceImpl(Registrar r) throws RemoteException {
        capsulesList = new ArrayList<>();
        registrar = r;
    }
    @Override
    public void retrieveCapsules(ArrayList<String> capsules) throws RemoteException {
        for (int i = 0; i < capsules.size(); i++) {
            capsulesList.add(capsules.get(i));
        }
    }
}
