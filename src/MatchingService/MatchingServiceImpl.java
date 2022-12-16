package MatchingService;

import Registrar.Registrar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MatchingServiceImpl extends UnicastRemoteObject implements MatchingService {
    private Registrar registrar;
    public ObservableList<String> capsulesList;
    private List<byte[]> pseudonymsPreviousDay;

    private ArrayList<String[]> uninformedTokens;

    private ArrayList<String> informedTokens;


    public MatchingServiceImpl(Registrar r) throws RemoteException {
        capsulesList = FXCollections.observableArrayList();
        registrar = r;
        registrar.setMatchingService(this);
        uninformedTokens = new ArrayList<>();
        informedTokens = new ArrayList<>();
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

    @Override
    public void positiveUser(ArrayList<String> logs, byte[] signature, PublicKey doctorPK) throws Exception {
        if(pseudonymsPreviousDay.isEmpty()){
          System.out.println("Nog geen voorgaande dagen. Kan dus niemand besmet hebben");
          return;
        }

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        for (String s : logs) {
            dataStream.writeBytes(s);
        }
        dataStream.flush();
        byte[] input = byteStream.toByteArray();

        if (verifySignature(input, signature, doctorPK)) {
            System.out.println("Start iets");
            for(int i=0; i<logs.size(); i++){
                String timeFrom = logs.get(i).split("\\^")[0];
                String QR = logs.get(i).split("\\^")[1];
                String userToken = logs.get(i).split("\\^")[2];
                String cateringBN = QR.split("\\|")[1];
                String hashRiNym = QR.split("\\|")[2];
                i++;
                String timeUntil = logs.get(i).split("\\^")[0];
                warnCatering(cateringBN, timeFrom, timeUntil);
                markTokens(hashRiNym, timeFrom, timeUntil, userToken);
                System.out.println("Doctor en matching services zijn geverifieerd");
            }
        }
    }

    public boolean verifySignature(byte[] input, byte[] signature, PublicKey doctorPK) throws Exception {
        Signature sign = Signature.getInstance("SHA256WithDSA");
        sign.initVerify(doctorPK);
        sign.update(input);
        return sign.verify(signature);
    }

    public boolean verifyHash(ArrayList<String> logs, List<byte[]> pseudonymsPreviousDay) throws Exception {
        System.out.println("Start hash");
        boolean valid = false;
        for(String s : logs){
            String QR = s.split("\\^")[1];
            String Ri = QR.split("\\|")[0];
            byte[] dailyRandomNumber = DatatypeConverter.parseHexBinary(Ri);
            String QRHash = QR.split("\\|")[2];

            MessageDigest sha = MessageDigest.getInstance("SHA256");
            sha.update(dailyRandomNumber);
            byte[] hashNym = sha.digest(pseudonymsPreviousDay.get(0));
            String createdHashNym = DatatypeConverter.printHexBinary(hashNym);
            if(QRHash.equals(createdHashNym)){
                valid = true;
            }
        }
        System.out.println("HASH: " + valid);
        return valid;
    }

    public void markTokens(String hashRiNym, String timeFrom, String timeUntil, String userToken) throws RemoteException {
        for(String capsule : capsulesList){
            String capsuleHashRiNym = capsule.split("\\|")[1];
            String userTokenCapsule = capsule.split("\\|")[2];
            if(capsuleHashRiNym.equals(hashRiNym)){
                System.out.println("Mark user token: " + userToken);
                if(userTokenCapsule.equals(userToken)){
                    informedTokens.add(userTokenCapsule);
                } else {
                    String[] tokenAndDate= new String[3];
                    tokenAndDate[0] = userTokenCapsule;
                    tokenAndDate[1] = timeFrom;
                    tokenAndDate[2] = timeUntil;
                    //registrar.markTokens(userTokenCapsule);
                    uninformedTokens.add(tokenAndDate);
                }
            }
        }
    }

    public void warnCatering(String cateringBN, String timeFrom, String timeUntil) throws RemoteException {
        System.out.println("Warn catering: " + cateringBN);
        registrar.warnCatering(cateringBN, timeFrom, timeUntil);
    }

    @Override
    public void informedTokens(ArrayList<String> userTokens) throws RemoteException {
        informedTokens.addAll(userTokens);
        for(String userToken : userTokens){
            ArrayList<String[]> toRemove = new ArrayList<>();
            for(String[] t : uninformedTokens) {
                if (t[0].equals(userToken)) {
                    toRemove.add(t);
                }
            }
            uninformedTokens.removeAll(toRemove);
            registrar.tokenInformed(userToken);
        }
    }

    @Override
    public ArrayList<String[]> getUninformedTokens() throws RemoteException{
        return uninformedTokens;
    }

    @Override
    public LocalDate getDate() throws RemoteException{
        return registrar.getDate();
    }
}
