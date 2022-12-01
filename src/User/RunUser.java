package User;

import java.rmi.RemoteException;

public class RunUser{
    public static void main(String[] args) throws RemoteException{
        UserImpl user = new UserImpl();
        user.setName("Jonas");
        user.setPhone("5");
        user.start();
        for (int i = 0; i <user.getUserTokens().size() ; i++) {
            System.out.println(user.getUserTokens().get(i));
        }

    }
}
