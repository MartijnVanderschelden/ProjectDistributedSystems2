package MixingProxy;

import Registrar.Registrar;
import User.User;
import MatchingService.MatchingService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class MixingProxyImpl extends UnicastRemoteObject implements MixingProxy {
    private PublicKey publicKey;
    private Registrar registrar;
    private MatchingService matchingService;
    List<byte[]> spentTokens = new ArrayList<>();
    public ObservableList<String> capsulesList;

    public MixingProxyImpl(Registrar r, MatchingService m) throws RemoteException {
        this.registrar = r;
        this.capsulesList = FXCollections.observableArrayList();
        this.publicKey = r.getPublicKey();
        this.matchingService= m;
    }

    @Override
    public void connectToServer() throws RemoteException {

    }

    @Override
    public boolean retrieveCapsule(String phoneNumber, LocalDate ldt, String h, byte[] userToken) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        LocalDate ldtTest = registrar.getDate();
        String userTokenString = DatatypeConverter.printHexBinary(userToken);
        System.out.println("Received a capsule: " + ldt + "|" + "|" + h + "|" + userTokenString);
        spentTokens.add(userToken);
        ////// Toevoegen aan capsules
        LocalDateTime localdatetime = registrar.getDate().atTime(LocalTime.now());

        /////
        int validCount = 0;
        Signature signatureVerify = Signature.getInstance("SHA256WithDSA");
        signatureVerify.initVerify(publicKey);
        signatureVerify.update(phoneNumber.getBytes(StandardCharsets.UTF_8));
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
            Platform.runLater(() -> {
                capsulesList.add(localdatetime + "|" + h + "|" + userTokenString);
            });
            System.out.println("Capsule is valid and got added to capsules");
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void retrieveExitCapsule(LocalDate ldt, String h, byte[] userToken) throws RemoteException {
        LocalDateTime localdatetime = registrar.getDate().atTime(LocalTime.now());
        String userTokenString = DatatypeConverter.printHexBinary(userToken);
        Platform.runLater(() -> {
            capsulesList.add(localdatetime + "|" + h + "|" + userTokenString);
        });
        System.out.println("Exit capsule received");
    }

    @Override
    public void flush() throws RemoteException {
        Collections.shuffle(capsulesList);
        for (int i = 0; i < capsulesList.size(); i++) {
            matchingService.retrieveCapsules(capsulesList.get(i));
        }
        capsulesList.clear();
        System.out.println("Capsules got flushed and sent to Matching Service");
    }

    @Override
    public void informedTokens(ArrayList<String> userTokens) throws RemoteException {
        matchingService.informedTokens(userTokens);
    }
}
