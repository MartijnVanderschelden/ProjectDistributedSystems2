package User;

import MatchingService.MatchingService;
import MixingProxy.MixingProxy;
import Registrar.Registrar;
import javafx.scene.paint.Color;

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

    private Color colorAfterQrScan;
    private String phone;
    private String name;
    private String QRCode;
    private ArrayList<byte[]> userTokens = new ArrayList<>();
    private ArrayList<String> userLogs = new ArrayList<>();


    public UserImpl(String name, String phone, Registrar registrar, MixingProxy mixingProxy) throws RemoteException {
        this.name = name;
        this.phone = phone;
        this.registrar = registrar;
        this.userTokens = new ArrayList<>();
        this.mixingProxy = mixingProxy;
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
        //tijd
        LocalDate ld = LocalDate.now();
        LocalDateTime ldt = LocalDateTime.now();
        //qr code loggen
        this.QRCode = qr;
        userLogs.add(ldt + "^" + qr);
        System.out.println("Following log is added to user logs: " + ldt + "|" + qr);
        //h value van qr code splitten om door te sturen in capsule
        String h = qr.substring(qr.lastIndexOf("|") + 1);
        boolean validityToken = mixingProxy.retrieveCapsule(user, ld, h, user.userTokens.get(0));
        user.userTokens.remove(0);
        //symbool toekennen indien jusite qr code scan
        //op basis van business nummer een kleur toekennen
        String businessNumber = qr.substring(qr.indexOf('|') + 1, qr.lastIndexOf('|'));
        generateColor(businessNumber);
        System.out.println("Business nummer:" + businessNumber);
        if(validityToken){
            return "ok | " + ldt;
        }
        else return "not ok" + ldt;

    }

    public void generateColor(String b){

        switch (b) {
            case "1":
                this.colorAfterQrScan = Color.BLUE;
                break;
            case "2":
                this.colorAfterQrScan = Color.GREEN;
                break;
            case "3":
                this.colorAfterQrScan = Color.YELLOW;
                break;
            case "4":
                this.colorAfterQrScan = Color.ORANGE;
                break;
            default: this.colorAfterQrScan = Color.RED;
        }
    }

    public Color getColorAfterQrScan() {
        return colorAfterQrScan;
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
