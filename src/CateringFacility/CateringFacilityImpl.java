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
    private SecretKey sk;
    private int phone;

    public CateringFacilityImpl() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    public void start() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        registrar = (Registrar) registry.lookup("Registrar");
        System.out.println("Catering Facility started");
    }
    //Methode om QR code aan te maken
    public void createQR(){

    }
    //Methode om dagelijkse
    //Methode om dagelijke pseudonym te krijgen
    public void getDailyPseudonym(){

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
