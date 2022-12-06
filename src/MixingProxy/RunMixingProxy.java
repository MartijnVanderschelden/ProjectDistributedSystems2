package MixingProxy;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import Registrar.Registrar;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class RunMixingProxy extends Application {
    private Registry registry;
    private Registrar registrar;
    private MixingProxy mixingProxy;

    public Label label;
    public Button button;
    int count;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MixingProxy.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setTitle("Mixing Proxy");
        count = 0;
    }
    public void pushButton(ActionEvent event) throws NoSuchAlgorithmException, RemoteException, AlreadyBoundException, SignatureException, InvalidKeyException, NotBoundException {
        if(count == 0) {
            startMixingProxy();
            button.setVisible(false);
            count++;
        } else{
            button.setVisible(false);
        }
    }
    public void startMixingProxy() throws NoSuchAlgorithmException, RemoteException, AlreadyBoundException, NotBoundException {
        Registry registryCreate = LocateRegistry.createRegistry(1100);
        try {
            Registry myRegistryGet = LocateRegistry.getRegistry("localhost", 1101);
            Registry registryRegistrar = LocateRegistry.getRegistry("localhost", 1099);
            registrar = (Registrar) registryRegistrar.lookup("Registrar");
            mixingProxy = new MixingProxyImpl(registrar);
            registryCreate.rebind("MixingProxy", mixingProxy);
            System.out.println("Mixing Proxy started");
            label.setText("Mixing Proxy");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
