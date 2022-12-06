package User;

import MatchingService.MatchingService;
import MixingProxy.MixingProxy;
import Registrar.Registrar;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
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
        this.userTokens=new ArrayList<byte[]>();
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
    public void scanQR(UserImpl user, String qr) throws RemoteException {
        LocalDate ld = LocalDate.now();
        this.QRCode = qr;
        userLogs.add(ld + " - " + qr);
        // TODO juiste versie met echte QR
        mixingProxy.retrieveCapsule(user, ld + " - " + user.getUserTokens().get(0) + qr );
        user.getUserTokens().remove(0);
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