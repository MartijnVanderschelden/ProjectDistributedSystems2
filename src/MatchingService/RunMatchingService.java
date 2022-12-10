package MatchingService;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import MixingProxy.MixingProxy;
import Registrar.Registrar;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class RunMatchingService extends Application {
    private Registry registry;
    private Registrar registrar;
    MatchingServiceImpl matchingService;

    public Label label;
    public Button button;
    int count;
    public ListView capsulesList;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MatchingService.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setTitle("Matching Service");
        count = 0;
    }
    public void pushButton(ActionEvent event) throws NoSuchAlgorithmException, RemoteException, AlreadyBoundException, SignatureException, InvalidKeyException, NotBoundException {
        if(count == 0) {
            startMatchingService();
            button.setVisible(false);
            count++;
        } else{
            button.setVisible(false);
        }
    }
    public void startMatchingService() throws NoSuchAlgorithmException, RemoteException, AlreadyBoundException, NotBoundException {
        Registry registryCreate = LocateRegistry.createRegistry(1101);
        try {
            Registry registryRegistrar = LocateRegistry.getRegistry("localhost", 1099);
            registrar = (Registrar) registryRegistrar.lookup("Registrar");
            matchingService = new MatchingServiceImpl(registrar);
            registryCreate.rebind("MatchingService", matchingService);
            System.out.println("Matching Service started");
            label.setText("Matching Service");

        } catch (Exception e) {
            e.printStackTrace();
        }
        capsulesList.setItems(matchingService.capsulesList);
    }
}
