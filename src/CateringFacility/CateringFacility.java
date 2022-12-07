package CateringFacility;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface CateringFacility extends Remote{
    long getBusinessNumber() throws RemoteException;
    long getPhoneNumber() throws RemoteException;
    String getFacilityName() throws RemoteException;
    String getLocation() throws RemoteException;
    void requestDailyPseudonym() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;
    void generateDailyRandomNumber() throws RemoteException, NoSuchAlgorithmException;
    void generateQRcode() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException;
}
