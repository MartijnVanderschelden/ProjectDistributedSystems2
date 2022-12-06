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
import java.time.LocalDateTime;

import MatchingService.MatchingService;
import MixingProxy.MixingProxy;
import Registrar.Registrar;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RunUser extends Application {
    private Registry registry;
    private Registrar registrar;
    private MixingProxy mixingProxy;
    private MatchingService matchingService;
    public Label tokensRemainingLabel;
    public UserImpl user;
    public Button enrollButton;
    public Button scanQrButton = new Button("Scan QR Code");
    public TextField qr = new TextField();

    int count;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("User.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("User");
        primaryStage.show();
        scanQrButton.setVisible(false);
        qr.setVisible(false);
        count = 0;
    }
    public void pushEnrollButton(ActionEvent event) throws NoSuchAlgorithmException, RemoteException, AlreadyBoundException, SignatureException, InvalidKeyException, NotBoundException {
        if (count == 0) {
            startUser();
            enrollButton.setText("Enroll user");
            count++;
        }
    }

    public void startUser() throws RemoteException, NotBoundException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        registry = LocateRegistry.getRegistry("localhost", 1099);
        registrar = (Registrar) registry.lookup("Registrar");
        Registry registryMixingProxy = LocateRegistry.getRegistry("localhost", 1100);
        mixingProxy = (MixingProxy) registryMixingProxy.lookup("MixingProxy");

        System.out.println("User started");
        UserImpl user = new UserImpl("Amin", "44", registrar, mixingProxy);
        registrar.enrollUser(user);
        System.out.println("Daily visits remaining: "+ user.getUserTokens().size());
        enrollButton.setVisible(false);
        tokensRemainingLabel.setText("Daily visits remaining: "+ String.valueOf(user.getUserTokens().size()));
        //QR Code scannen en sturen naar Mixing proxy
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                try {
                    LocalDateTime ldt = LocalDateTime.now();
                    String capsule = ldt + "-" + qr.getText() + "-" + user.getUserTokens().get(0);
                    user.getUserTokens().remove(0);
                    mixingProxy.retrieveCapsule(user, capsule);
                    tokensRemainingLabel.setText("Daily visits remaining: "+ String.valueOf(user.getUserTokens().size()));
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        };
        scanQrButton.setOnAction(event);
    }
}
