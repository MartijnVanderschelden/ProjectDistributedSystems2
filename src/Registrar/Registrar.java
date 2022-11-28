package Registrar;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;

public interface Registrar extends Remote {
    LocalDate getDate() throws RemoteException;
    void nextDay() throws RemoteException;

}
