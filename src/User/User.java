package User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;
import java.util.List;

public interface User extends Remote{
    String getPhone() throws RemoteException;
    String getName() throws RemoteException;
    void newDay(List<byte[]> newUserTokens, List<String[]> criticalTokens, LocalDate date) throws RemoteException;

    String scanQR(String qr) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException;
    String leaveCathering(UserImpl user, String qr) throws RemoteException;
}

