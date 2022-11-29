package Doctor;

import java.rmi.RemoteException;

public class RunDoctor {
    public void main(String[]args) throws RemoteException {
        DoctorImpl doctor = new DoctorImpl();
        doctor.connectToServer();
    }
}
