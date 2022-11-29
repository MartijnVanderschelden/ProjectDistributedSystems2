package User;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RunUser{
    public static void main(String[] args) throws RemoteException{
        UserImpl user = new UserImpl();
        user.connectToServer();
    }
}
