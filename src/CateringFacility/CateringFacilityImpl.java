package CateringFacility;

import Registrar.Registrar;

import javax.crypto.SecretKey;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CateringFacilityImpl extends UnicastRemoteObject implements CateringFacility, Remote {
    /*
    Catering gegevens
     */
    private long phoneNumber;
    private long businessNumber; //used as unique identifier CF
    private String location;
    private String facilityName;

    /*
    Servers waar interactie mee gemaakt wordt
     */
    private Registry registry;
    private Registrar registrar;


    /*
    Later
     */
    private String CF;
    private SecretKey sk;
    private SecretKey skDaily;
    private String dailyPseudonym;

    public CateringFacilityImpl(long phoneNumber, long businessNumber, String location, String facilityName, Registrar registrar) throws RemoteException {
        this.phoneNumber = phoneNumber;
        this.businessNumber = businessNumber;
        this.location = location;
        this.facilityName = facilityName;
        this.registrar = registrar;
    }
    //Methode om dagelijkse secret key te ontvangen
    public void getDailySecretKey(){

    }
    //Methode om dagelijke pseudonym te krijgen
    public void getDailyPseudonym(){

    }
    //Methode om QR code aan te maken
    public void createQR(){

    }
    //Methode om gebruiker, die deze QR code scant, te registreren
    public void registerUser(){

    }
    //Setters en getters
    public void setCF(String CF) {
        this.CF = CF;
    }
    public String getCF() {
        return CF;
    }
    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public long getPhoneNumber() {
        return phoneNumber;
    }

    /*
    Getters
     */
    public long getBusinessNumber() throws RemoteException{
        return businessNumber;
    }

    @Override
    public String getFacilityName() throws RemoteException {
        return facilityName;
    }

    /*
    toString
     */
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
