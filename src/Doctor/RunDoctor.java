package Doctor;

import java.rmi.RemoteException;

public class RunDoctor {
    public static void main(String[]args) throws RemoteException {
        DoctorImpl doctor = new DoctorImpl();
        doctor.start();
    }
}
