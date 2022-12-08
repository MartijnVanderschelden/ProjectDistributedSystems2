package MixingProxy;

import Registrar.Registrar;
import User.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MixingProxyImpl extends UnicastRemoteObject implements MixingProxy{
    private PublicKey publicKey;
    private Registrar registrar;
    private Map<User, ArrayList<String>> userCapsules;
    public MixingProxyImpl(Registrar r) throws RemoteException{
        this.registrar=r;
        userCapsules = new HashMap<>();
        this.publicKey = r.getPublicKey();
    }
    @Override
    public void connectToServer() throws RemoteException {

    }

    @Override
    public String retrieveCapsule(User user, String capsule) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        ArrayList<String> capsules = new ArrayList<>();
        capsules.add(capsule);
        userCapsules.put(user, capsules);
        System.out.println("Received a capsule: " + capsule);
        //2. Capsule controleren
        //2.1 Usertoken controleren


        String stringtoken = capsule.substring(capsule.indexOf('|'),capsule.lastIndexOf('|'));
        byte[] bytetoken= stringtoken.getBytes();
        //
        if (registrar.checkToken(publicKey, user, bytetoken)){
            System.out.println("token verified");
            return "ok";

        }
        else {
            System.out.println("token not verified");
            return "not ok";
        }
    }
}
