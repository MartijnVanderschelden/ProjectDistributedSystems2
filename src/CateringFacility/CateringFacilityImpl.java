package CateringFacility;

import Registrar.Registrar;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.Base64;
import javax.xml.bind.DatatypeConverter;

public class CateringFacilityImpl extends UnicastRemoteObject implements CateringFacility, Remote {
    /*
    Catering gegevens
     */
    private long phoneNumber;
    private long businessNumber; //used as unique identifier CF
    private String location;
    private String facilityName;

    /*
    Remote opgehaalde gegevens en berekende gegevens
     */
    private byte[] dailyPseudonym;
    private String QRcode;

    /*
    Servers waar interactie mee gemaakt wordt
     */
    private Registry registry;
    private Registrar registrar;

    /*
    Client-side variabelen
     */
    private byte[] dailyRandomNumber;
    private LocalDate date;
    /*
    Later
     */
    private SecretKey sk;
    private SecretKey skDaily;

    public CateringFacilityImpl(long phoneNumber, long businessNumber, String location, String facilityName, Registrar registrar) throws RemoteException {
        this.phoneNumber = phoneNumber;
        this.businessNumber = businessNumber;
        this.location = location;
        this.facilityName = facilityName;
        this.registrar = registrar;
        this.date = registrar.getDate();
    }
    //Methode om dagelijke pseudonym te krijgen

    //Methode om gebruiker, die deze QR code scant, te registreren
    public void registerUser(){

    }
    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    /*
    Getters
     */
    @Override
    public long getBusinessNumber() throws RemoteException{
        return businessNumber;
    }

    @Override
    public long getPhoneNumber() throws RemoteException{
        return phoneNumber;
    }

    @Override
    public String getFacilityName() throws RemoteException {
        return facilityName;
    }

    @Override
    public String getLocation() throws RemoteException {
        return this.location;
    }

    @Override
    public void requestDailyPseudonym() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        dailyPseudonym = registrar.calculateDailyPseudonym(businessNumber, location);
        this.date = registrar.getDate();
        System.out.println("Daily pseudonym has arrived at: " + facilityName);
    }

    @Override
    public void generateDailyRandomNumber() throws RemoteException{
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[124];
        random.nextBytes(salt);
        System.out.println("Random number generated on: " + date + " at: " + facilityName);
        dailyRandomNumber = Base64.getEncoder().encodeToString(salt).getBytes();
    }

    @Override
    public void generateQRcode() throws NoSuchAlgorithmException{
        // 1) genereer hash van R_i en nym_(CF, day_i)
        MessageDigest sha = MessageDigest.getInstance("SHA256");
        sha.update(dailyRandomNumber);
        byte[] h = sha.digest();
        this.QRcode = DatatypeConverter.printHexBinary(dailyRandomNumber) + "|" + businessNumber + "|" + DatatypeConverter.printHexBinary(h);
        System.out.println("QR code: " + QRcode + " has been generated on: " + date + " at: " + facilityName);
    }


    @Override
    public String toString() {
        return "CateringFacilityImpl{" +
                "phoneNumber=" + phoneNumber +
                ", businessNumber=" + businessNumber +
                ", location='" + location + '\'' +
                ", facilityName='" + facilityName + '\'' +
                '}';
    }
}
