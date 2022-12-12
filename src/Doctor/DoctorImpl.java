package Doctor;

import MatchingService.MatchingService;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;


public class DoctorImpl extends UnicastRemoteObject implements Doctor, Remote{
    private MatchingService matchingService;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public DoctorImpl(MatchingService m) throws RemoteException {
        this.matchingService = m;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
            KeyPair kp = keyPairGenerator.generateKeyPair();
            this.privateKey = kp.getPrivate();
            this.publicKey = kp.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode om logs te verifieren
    @Override
    public byte[] signLogs(ArrayList<String> logs, String userName) throws Exception, RemoteException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        for(String s : logs){
            dataStream.writeBytes(s);
        }
        dataStream.flush();
        byte[] output = byteStream.toByteArray();

        Signature signature = Signature.getInstance("SHA256WithDSA");
        signature.initSign(privateKey);
        signature.update(output);
        byte[] digitalSignature = signature.sign();
        byteStream.reset();

        return digitalSignature;
    }

    // Methode om logs van de user te lezen. Neem enkel de logs van de dag voordien
    @Override
    public ArrayList<String> readLogs(String userName) throws RemoteException{
        String nameForLog = userName.replace(" ", "_");
        ArrayList<String> logs = new ArrayList<>();
        try {
            File userLog = new File("logs/log_" + nameForLog + ".txt");
            Scanner userLogReader = new Scanner(userLog);
            while (userLogReader.hasNextLine()) {
                logs.add(userLogReader.nextLine());
            }
            userLogReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        ArrayList<String> finalLogs = new ArrayList<>();
        for(String s : logs){
            String date = s.split("\\^")[0];
            System.out.println(date);
            LocalDateTime dateLog = LocalDateTime.parse(date);
            LocalDateTime dateUntil = LocalDateTime.now();
            LocalDateTime dateFrom = LocalDateTime.now().minusDays(1);
            if(dateLog.isAfter(dateFrom) && dateLog.isBefore(dateUntil)){
                finalLogs.add(s);
                System.out.println(s);
            }
        }
        return finalLogs;
    }

    @Override
    public PublicKey getPublicKey() throws RemoteException{
        return publicKey;
    }

    @Override
    public void sendToMatchingService(ArrayList<String> logs, byte[] signature) throws RemoteException, Exception {
        matchingService.positiveUser(logs, signature, publicKey);
    }

}
