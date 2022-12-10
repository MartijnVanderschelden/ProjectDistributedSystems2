package MixingProxy;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import MatchingService.MatchingService;
import Registrar.Registrar;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class RunMixingProxy extends Application {
    public Registrar registrar;
    public MixingProxyImpl mixingProxy;
    public MatchingService matchingService;

    public Label label;
    public Button button, flushButton;
    int count;
    public ListView<String> capsulesList;

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
            matchingService = (MatchingService) myRegistryGet.lookup("MatchingService");
            Registry registryRegistrar = LocateRegistry.getRegistry("localhost", 1099);
            registrar = (Registrar) registryRegistrar.lookup("Registrar");
            mixingProxy = new MixingProxyImpl(registrar, matchingService);
            registryCreate.rebind("MixingProxy", mixingProxy);
            System.out.println("Mixing Proxy started");
            label.setText("Mixing Proxy");
        } catch (NotBoundException nbe) {
            nbe.printStackTrace();
        }

        capsulesList.setItems(mixingProxy.capsulesList);

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                try {
                    mixingProxy.flush();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        };
        flushButton.setOnAction(event);
    }
}
