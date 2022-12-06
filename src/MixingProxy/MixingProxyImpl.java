package MixingProxy;

import Registrar.Registrar;
import User.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MixingProxyImpl extends UnicastRemoteObject implements MixingProxy{
    private static PrivateKey sk; //used to sign capsules
    private static PublicKey pk; //used by visitor to verify signing
    private Registrar registrar;
    private Map<User, ArrayList<String>> userCapsules;
    public MixingProxyImpl(Registrar r) throws RemoteException{
        this.registrar=r;
        userCapsules = new HashMap<>();
    }
    @Override
    public void connectToServer() throws RemoteException {

    }

    @Override
    public void retrieveCapsule(User user, String string) throws RemoteException {
        ArrayList<String> capsules = new ArrayList<>();
        capsules.add(string);
        userCapsules.put(user, capsules);
        System.out.println("Received a capsule: " + string);

    }
}
