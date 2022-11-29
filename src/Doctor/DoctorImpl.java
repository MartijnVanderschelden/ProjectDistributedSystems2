package Doctor;

import MatchingService.MatchingService;

import java.rmi.RemoteException;
import java.security.KeyPair;

public class DoctorImpl implements Doctor{
    private int doctorId;
    private KeyPair kp;
    private MatchingService matchingService;

    @Override
    public void connectToServer() throws RemoteException {
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
