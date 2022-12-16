package User;

import MixingProxy.MixingProxy;
import Registrar.Registrar;
import javafx.scene.paint.Color;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;


public class UserImpl extends UnicastRemoteObject implements User {
    private Registrar registrar;
    private MixingProxy mixingProxy;
    private Color colorAfterQrScan;
    private String phone;
    private String name;
    private String QRCode;
    private String metInfectedPerson;
    private ArrayList<byte[]> userTokens;
    private ArrayList<String> userLogs = new ArrayList<>();
    private byte[] currentToken; // Token waarmee gescand werd, en gebruikt wordt voor exitcathering

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
    public void newDay(List<byte[]> newUserTokens, List<String[]> criticalTokens, LocalDate date) throws RemoteException {
        // New daily tokens
        this.userTokens.clear();
        this.userTokens.addAll(newUserTokens);

        // Check if critical tokens are in logs
        ArrayList<String> logs = readLogs();
        ArrayList<String> informedUserTokens = new ArrayList<>();
        if(!criticalTokens.isEmpty()){
            boolean informed = false;
            for(String[] sCt: criticalTokens ){
                String criticalUserToken = sCt[0];
                LocalDateTime timeFrom = LocalDateTime.parse(sCt[1]);
                LocalDateTime timeUntil = LocalDateTime.parse(sCt[2]);
                for(int i=0; i<logs.size(); i++) {
                    String logFromString = logs.get(i).split("\\^")[0];
                    LocalDateTime logFrom = LocalDateTime.parse(logFromString);
                    String QR = logs.get(i).split("\\^")[1];
                    String userToken = logs.get(i).split("\\^")[2];
                    i++;
                    String logUntilString = logs.get(i).split("\\^")[0];
                    LocalDateTime logUntil = LocalDateTime.parse(logUntilString);

                    if (criticalUserToken.equals(userToken) &&
                            !logUntil.isBefore(timeFrom) &&
                            !logFrom.isAfter(timeUntil)) {
                        System.out.println("Je bent in contact gekomen met een positief getest persoon");
                        informedUserTokens.add(userToken);
                        informed = true;
                        break;
                    }
                }
                if(informed){
                    break;
                }
            }
            mixingProxy.informedTokens(informedUserTokens);
        }
    }


    @Override
    public String scanQR(String qr) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        // User Token
        this.currentToken = userTokens.get(0);

        //tijd
        LocalDate ld = registrar.getDate();
        LocalDateTime ldt = registrar.getDate().atTime(LocalTime.now());

        //qr code loggen
        this.QRCode = qr;
        userLogs.add(ldt + "^" + qr + "^" + currentToken);
        System.out.println("Following log is added to user logs: " + ldt + "|" + qr + "|" + currentToken);
        writeToLogFile(ldt, qr, currentToken);

        //h value van qr code splitten om door te sturen in capsule
        String h = qr.substring(qr.lastIndexOf("|") + 1);
        boolean validityToken = mixingProxy.retrieveCapsule(phone, ld, h, currentToken);

        // Gebruikte token verwijderen
        userTokens.remove(0);

        //symbool toekennen indien jusite qr code scan
        //op basis van business nummer een kleur toekennen
        String businessNumber = qr.substring(qr.indexOf('|') + 1, qr.lastIndexOf('|'));
        generateColor(businessNumber);
        if(validityToken){
            return "ok | " + ldt;
        }
        else return "not ok" + ldt;
    }

    @Override
    public String leaveCathering(UserImpl user, String qr) throws RemoteException {
        LocalDate ld = registrar.getDate();
        LocalDateTime ldt = registrar.getDate().atTime(LocalTime.now());
        userLogs.add(ldt + "^" + qr);
        writeToLogFile(ldt, qr, currentToken);
        String h = qr.substring(qr.lastIndexOf("|") + 1);
        mixingProxy.retrieveExitCapsule(ld, h, currentToken);
        return "Successfully left cathering";
    }

    public void writeToLogFile(LocalDateTime ldt, String qr, byte[] currentToken){
        String phoneForLog = phone.replace(" ", "_");
        try {
            File logFile = new File("logs/log_" + phoneForLog + ".txt");
            if (!logFile.exists()){
                logFile.createNewFile();
            }
            FileWriter logFW = new FileWriter("logs/log_" + phoneForLog + ".txt", true);
            logFW.write(ldt + "^" + qr + "^" + DatatypeConverter.printHexBinary(currentToken));
            logFW.write("\n");
            logFW.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
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
                this.colorAfterQrScan = Color.RED;
                break;
            case "4":
                this.colorAfterQrScan = Color.ORANGE;
                break;
            default: this.colorAfterQrScan = Color.BLACK;
        }
    }

    public Color getColorAfterQrScan() {
        return colorAfterQrScan;
    }

    public ArrayList<String> readLogs(){
        String phoneForLog = phone.replace(" ", "_");
        ArrayList<String> logs = new ArrayList<>();
        try {
            File userLog = new File("logs/log_" + phoneForLog + ".txt");
            if (!userLog.exists()){
                userLog.createNewFile();
            }
            Scanner userLogReader = new Scanner(userLog);
            while (userLogReader.hasNextLine()) {
                logs.add(userLogReader.nextLine());
            }
            userLogReader.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return logs;
    }

    //Setters en getters
    public ArrayList<byte[]> getUserTokens() {
        return userTokens;
    }
}
