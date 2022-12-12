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
import java.util.ArrayList;
import java.util.List;

public class MatchingServiceImpl extends UnicastRemoteObject implements MatchingService {
    private Registrar registrar;
    public ObservableList<String> capsulesList;
    private List<byte[]> pseudonymsPreviousDay;

    public MatchingServiceImpl(Registrar r) throws RemoteException {
        capsulesList = FXCollections.observableArrayList();
        registrar = r;
        registrar.setMatchingService(this);
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

        System.out.println("sing");
        boolean sign = verifySignature(input, signature, doctorPK);

        System.out.println("hash");
        boolean hash = verifyHash(logs, pseudonymsPreviousDay);

        if (verifySignature(input, signature, doctorPK) && verifyHash(logs, pseudonymsPreviousDay)) {
            for(String s : logs){
                String date = s.split("\\^")[0];
                String QR = s.split("\\^")[1];
                String cateringBN = QR.split("\\|")[1];
                warnCatering(cateringBN, date);
                //warnUsers();
                System.out.println("Doctor en matching services zijn geverifieerd");
            }
        }
    }

    public boolean verifySignature(byte[] input, byte[] signature, PublicKey doctorPK) throws Exception {
        Signature sign = Signature.getInstance("SHA256WithDSA");
        sign.initVerify(doctorPK);
        sign.update(input);
        System.out.println("SIGN: " + sign.verify(signature));
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
            byte[] QRHashBytes = DatatypeConverter.parseHexBinary(QRHash);


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

//    public void warnUsers(){
//        registrar.warnCatering(cateringBN, date);
//    }

    public void warnCatering(String cateringBN, String date) throws RemoteException {
        registrar.warnCatering(cateringBN, date);
    }
}
