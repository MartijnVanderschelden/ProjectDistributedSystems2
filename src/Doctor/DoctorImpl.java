package Doctor;

import MatchingService.MatchingService;
import Registrar.Registrar;
import Registrar.RegistrarImpl;

import java.rmi.RemoteException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DoctorImpl implements Doctor{
    private Registry registry;
    private Registrar registrar;

    private int doctorId;
    private KeyPair kp;
    private MatchingService matchingService;

    public DoctorImpl(){

    }
    @Override
    public void start() throws RemoteException {
        try {
            registry = LocateRegistry.getRegistry("localhost", 1099);
            registrar = (Registrar) registry.lookup("Registrar");
            System.out.println("Doctor started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getId() throws RemoteException {
        return doctorId;
    }
    //Methode als er covid infectie is en doorgeven aan matching service
    public void submitLogs(){

    }
    //Methode om logs van de user te lezen
    public void readReleasedLogs(){

    }
}
