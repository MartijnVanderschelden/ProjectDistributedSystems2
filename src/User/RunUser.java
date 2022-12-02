package User;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;

import MatchingService.MatchingService;
import MixingProxy.MixingProxy;
import Registrar.Registrar;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class RunUser extends Application {
    private Registry registry;
    private Registrar registrar;
    private MixingProxy mixingProxy;
    private MatchingService matchingService;
    private Label tokensRemainingLabel;
    public UserImpl user;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("User.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        registry = LocateRegistry.getRegistry("localhost", 1099);
        registrar = (Registrar) registry.lookup("Registrar");
        System.out.println("User started");
        UserImpl user = new UserImpl("Amin", "44", registrar);
        registrar.enrollUser(user);
        System.out.println(user.getUserTokens().size());
        //tokensRemainingLabel.setText(String.valueOf(user.getUserTokens().size()));


    }
}
