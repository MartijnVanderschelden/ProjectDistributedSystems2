package User;

import Registrar.RegistrarImpl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class UserImpl implements User{
    private Registry myRegistry;
    private RegistrarImpl registrar;

    private int telefoonnummer;
    private String naam;
    private String token;
    private String dataString;
    private ArrayList<String> userTokens = new ArrayList<>();
    private ArrayList<String> userLogs = new ArrayList<>();


    public UserImpl(){}
    public UserImpl(String naam, int nummer){
        this.naam=naam;
        this.telefoonnummer =nummer;
        this.userLogs=new ArrayList<>();
        this.userTokens=new ArrayList<>();
    }

    @Override
    public void connectToServer() throws RemoteException {
        try {
            // fire to localhostport 1099
            myRegistry = LocateRegistry.getRegistry("localhost", 1099);
            // search for CounterService
            registrar = (RegistrarImpl) myRegistry.lookup("Registrar");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Methodes voor als klant binnengaat
    public void sendCapsule(){

    }
    // Methodes voor als klant weggaat
    public void exitCatering(){

    }
    // Methodes voor als klant in contact was met covid
    public void fetchCovidLog(){

    }
    //Getters en Setters
    public int getTelefoonnummer() {
        return telefoonnummer;
    }
    public String getNaam() {
        return naam;
    }
    public void setTelefoonnummer(int telefoonnummer) {
        this.telefoonnummer = telefoonnummer;
    }
    public void setNaam(String naam) {
        this.naam = naam;
    }
}
