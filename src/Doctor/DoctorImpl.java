package Doctor;

import java.rmi.RemoteException;

public class DoctorImpl implements Doctor{
    int doctorId;

    public int getDoctorId() {
        return doctorId;
    }

    @Override
    public void connectToServer() throws RemoteException {

    }
}
