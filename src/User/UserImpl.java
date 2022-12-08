package User;

import MatchingService.MatchingService;
import MixingProxy.MixingProxy;
import Registrar.Registrar;

import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;


public class UserImpl extends UnicastRemoteObject implements User {
    private Registry registry;
    private Registrar registrar;
    private MixingProxy mixingProxy;
    private MatchingService matchingService;


    private String phone;
    private String name;
    private String QRCode;
    private ArrayList<byte[]> userTokens = new ArrayList<>();
    private ArrayList<String> userLogs = new ArrayList<>();


    public UserImpl(String name, String phone, Registrar registrar, MixingProxy mixingProxy) throws RemoteException{
        this.name=name;
        this.phone=phone;
        this.registrar=registrar;
        this.userTokens=new ArrayList<>();
        this.mixingProxy=mixingProxy;
    }
    // Interface methods
    @Override
    public String getPhone() throws RemoteException {
        return phone;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public void retrieveTokens(List<byte[]> userTokens) throws RemoteException {
        this.userTokens.clear();
        this.userTokens.addAll(userTokens);
    }

    @Override
    public String scanQR(UserImpl user, String qr) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        LocalDate ld = LocalDate.now();
        LocalDateTime ldt = LocalDateTime.now();
        this.QRCode = qr;
        userLogs.add(ldt + " - " + qr);
        // TODO juiste versie met echte QR
        user.userTokens.remove(0);
        boolean validityToken = mixingProxy.retrieveCapsule(user, ld, qr, user.userTokens.get(0));

        if(validityToken){
            return "ok " + ldt;
        }
        else return "not ok" + ldt;
    }


    // Methodes voor als klant weggaat
    public void exitCateringFacility(){

    }
    // Methodes voor als klant in contact was met covid
    public void fetchLogs(){

    }
    //Setters en getters

    public ArrayList<byte[]> getUserTokens() {
        return userTokens;
    }
}
