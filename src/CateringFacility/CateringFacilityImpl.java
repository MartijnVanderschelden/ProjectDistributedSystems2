package CateringFacility;

import Registrar.Registrar;

import javax.crypto.SecretKey;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CateringFacilityImpl implements CateringFacility, Remote {
    private Registrar registrar;
    private String CF;
    private int phone;
    private SecretKey sk;
    private SecretKey skDaily;
    private String dailyPseudonym;

    public CateringFacilityImpl() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    public void start() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        registrar = (Registrar) registry.lookup("Registrar");
        System.out.println("Catering Facility started");
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
    //Methode om gebruiker, die deze QR code scant, te registeren
    public void registerUser(){

    }
    //Setters en getters
    public void setCF(String CF) {
        this.CF = CF;
    }
    public String getCF() {
        return CF;
    }
    public void setPhone(int phone) {
        this.phone = phone;
    }
    public int getPhone() {
        return phone;
    }
}
