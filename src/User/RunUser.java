package User;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import MatchingService.MatchingService;
import MixingProxy.MixingProxy;
import Registrar.Registrar;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class RunUser extends Application {
    private Registry registry;
    private Registrar registrar;
    private MixingProxy mixingProxy;
    private MatchingService matchingService;
    public Label tokensRemainingLabel;
    public UserImpl user;
    public Button button;
    int count;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("User.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        count = 0;
    }
    public void pushButton(ActionEvent event) throws NoSuchAlgorithmException, RemoteException, AlreadyBoundException, SignatureException, InvalidKeyException, NotBoundException {
        if (count == 0) {
            startUser();
            button.setText("Enroll user");
            count++;
        }
    }
    public void startUser() throws RemoteException, NotBoundException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        registry = LocateRegistry.getRegistry("localhost", 1099);
        registrar = (Registrar) registry.lookup("Registrar");
        System.out.println("User started");
        UserImpl user = new UserImpl("Amin", "44", registrar);
        registrar.enrollUser(user);
        System.out.println(user.getUserTokens().size());
        button.setVisible(false);
        tokensRemainingLabel.setText("Daily visits remaining: "+ String.valueOf(user.getUserTokens().size()));
    }
}
