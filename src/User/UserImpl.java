package User;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UserImpl implements User{
    int nummer;
    String naam;
    ArrayList<String> userTokens = new ArrayList<>();
    ArrayList<String> userLogs = new ArrayList<>();

    public UserImpl(){

    }

    @Override
    public void connectToServer() throws RemoteException {

    }
    public int getNummer() {
        return nummer;
    }
    public String getNaam() {
        return naam;
    }
    public void setNummer(int nummer) {
        this.nummer = nummer;
    }
    public void setNaam(String naam) {
        this.naam = naam;
    }
}
