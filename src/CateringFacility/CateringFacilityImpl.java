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
    private int phoneNumber;
    private String CF;
    private SecretKey sk;
    private String location;

    public CateringFacilityImpl() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    public void start() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        registrar = (Registrar) registry.lookup("Registrar");
        System.out.println("Catering Facility started");
    }
}
