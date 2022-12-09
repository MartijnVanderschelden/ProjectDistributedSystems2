package MixingProxy;

import Registrar.Registrar;
import User.User;
import MatchingService.MatchingService;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class MixingProxyImpl extends UnicastRemoteObject implements MixingProxy {
    private PublicKey publicKey;
    private Registrar registrar;
    private MatchingService matchingService;
    List<byte[]> spentTokens = new ArrayList<>();
    private ArrayList<String> capsules;

    public MixingProxyImpl(Registrar r) throws RemoteException {
        this.registrar = r;
        capsules = new ArrayList<>();
        this.publicKey = r.getPublicKey();
    }

    @Override
    public void connectToServer() throws RemoteException {

    }

    @Override
    public boolean retrieveCapsule(User user, LocalDate ldt, String qr, byte[] userToken) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        LocalDate ldtTest = registrar.getDate();

        String userTokenString = new String(userToken, StandardCharsets.UTF_8);
        System.out.println("Received a capsule: " + ldt + "|" + "|" + qr + "|" + userTokenString);
        spentTokens.add(userToken);
        ////// Toevoegen aan capsules
        LocalDateTime localdatetime = LocalDateTime.now();
        capsules.add(localdatetime + "|" + qr + "|" + userTokenString);
                /////
        int validCount = 0;
        Signature signatureVerify = Signature.getInstance("SHA256WithDSA");
        signatureVerify.initVerify(publicKey);
        signatureVerify.update(user.getPhone().getBytes(StandardCharsets.UTF_8));
        System.out.println("------Checking sent Capsule------");
        //1. token verifieren
        if (signatureVerify.verify(userToken)) {
            System.out.println("1. User token is valid!");
            validCount++;
        } else{
            System.out.println("1. User token is not valid!");
        }
        //2. Datum controleren
        if (ldtTest.equals(ldt)){
            validCount++;
            System.out.println("2. Date is valid!");
        } else{
            System.out.println("2. Date is not valid!");
        }
        int countOfDuplicates = Collections.frequency(spentTokens, userToken);
        if (countOfDuplicates<=1){
            validCount++;
            System.out.println("3. User Token has not been spent yet!");
        } else {
            System.out.println("3. User token has already been spent!");
        }
        //3. Controleren of het niet nog eens gebruikt is
        if (validCount==3) {
            System.out.println("Capsule is valid and got added to capsules");
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void flush() throws RemoteException {
        matchingService.retrieveCapsules(capsules);
        capsules.clear();
        System.out.println("Capsules got flushed and sent to Matching Service");
    }
}
