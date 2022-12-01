package User;

import MatchingService.MatchingService;
import MixingProxy.MixingProxy;
import Registrar.Registrar;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class UserImpl extends UnicastRemoteObject implements User {
    private Registry registry;
    private Registrar registrar;
    private MixingProxy mixingProxy;
    private Registry registryMixingProxy;
    private MatchingService matchingService;
    private Registry registryMatchingService;

    private String phone;
    private String name;
    private String token;
    private String dataString;
    private ArrayList<byte[]> userTokens = new ArrayList<>();
    private ArrayList<String> userLogs = new ArrayList<>();


    public UserImpl() throws RemoteException{}
    public UserImpl(String name, String phone) throws RemoteException{
        this.name =name;
        this.phone = phone;
        this.userLogs=new ArrayList<>();
        this.userTokens=new ArrayList<>();
    }

    @Override
    public void start() throws RemoteException {
        try {
            registry = LocateRegistry.getRegistry("localhost", 1099);
            registrar = (Registrar) registry.lookup("Registrar");
            System.out.println("User started");
            //registrar.connectUser(this);
            userTokens = registrar.enrollUser(this.phone);

            /*registryMatchingService = LocateRegistry.getRegistry("localhost", 1098);
            matchingService = (MatchingService) registryMatchingService.lookup("MatchingService");

            registryMixingProxy = LocateRegistry.getRegistry("localhost",1097);
            mixingProxy = (MixingProxy) registryMixingProxy.lookup("MixingProxy");*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Methodes voor als klant binnengaat
    public void enterCateringFacility(){

    }
    // Methodes voor als klant weggaat
    public void exitCateringFacility(){

    }
    // Methodes voor als klant in contact was met covid
    public void fetchLogs(){

    }
    //Getters en Setters
    public String getPhone() {
        return phone;
    }
    public String getName() {
        return name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ArrayList<byte[]> getUserTokens() {return userTokens;}
    public void setUserTokens(ArrayList<byte[]> userTokens) {this.userTokens = userTokens;}
}
