package MixingProxy;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.*;

public interface MixingProxy extends Remote {
    void connectToServer() throws RemoteException;
}
