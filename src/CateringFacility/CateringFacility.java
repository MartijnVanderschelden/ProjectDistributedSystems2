package CateringFacility;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CateringFacility extends Remote{
    long getBusinessNumber() throws RemoteException;
    String getFacilityName() throws RemoteException;
}
