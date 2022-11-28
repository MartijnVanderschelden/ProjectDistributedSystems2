package CateringFacility;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RunCateringFacility {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        CateringFacilityImpl cateringFacility = new CateringFacilityImpl();
        cateringFacility.start();
    }
}
