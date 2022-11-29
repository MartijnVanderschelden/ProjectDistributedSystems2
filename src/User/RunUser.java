package User;

import java.rmi.RemoteException;

public class RunUser{
    public static void main(String[] args) throws RemoteException{
        UserImpl user = new UserImpl();
        user.setName("Jonas");
        user.setPhone(468204065);
        user.start();
    }
}
