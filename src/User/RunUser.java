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
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RunUser extends Application {
    public TextField nameTextField;
    public TextField phoneNumberTextfield;
    private Registry registry;
    private Registrar registrar;
    private MixingProxy mixingProxy;
    private MatchingService matchingService;
    public Label tokensRemainingLabel;
    public Label scanResponseLabel;
    public UserImpl user;
    public Button enrollButton;
    public Button scanQrButton = new Button("Scan QR Code");
    public TextField qr = new TextField();
    public Button leaveCateringButton;
    public Label userName;
    int count;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("User.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("User");
        primaryStage.show();
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
        scanQrButton.setVisible(true);
        nameTextField.setVisible(true);
        phoneNumberTextfield.setVisible(true);
        /*
        Gegevens user ingeven
         */
        String name = nameTextField.getText();
        long phoneNumber = Long.parseLong(phoneNumberTextfield.getText());

        UserImpl user = new UserImpl(name, String.valueOf(phoneNumber), registrar, mixingProxy);
        if (nameTextField.getText().isEmpty() || phoneNumberTextfield.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please complete all fields!");
            alert.showAndWait();
        } else {
            registrar.enrollUser(user);
            System.out.println("Daily visits remaining: "+ user.getUserTokens().size());
            enrollButton.setVisible(false);
            phoneNumberTextfield.setVisible(false);
            nameTextField.setVisible(false);
            userName.setText("User: " + user.getName());
            userName.setVisible(true);
            qr.setVisible(true);
            scanQrButton.setVisible(true);
            tokensRemainingLabel.setText("Daily visits remaining: "+ String.valueOf(user.getUserTokens().size()));
            //QR Code scannen en sturen naar Mixing proxy
            EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e)
                {
                    try {
                        String scanResponse = user.scanQR(qr.getText());
                        scanResponseLabel.setText(scanResponse);
                        scanResponseLabel.setTextFill(user.getColorAfterQrScan());
                        tokensRemainingLabel.setText("Daily visits remaining: "+ String.valueOf(user.getUserTokens().size()));
                        scanQrButton.setVisible(false);
                        leaveCateringButton.setVisible(true);
                    } catch (RemoteException | NoSuchAlgorithmException | SignatureException | InvalidKeyException ex) {
                        ex.printStackTrace();
                    }
                }
            };
            scanQrButton.setOnAction(event);
            //Knop voor catering te verlaten
            EventHandler<ActionEvent> event0 = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e0)
                {
                    try {
                        String scanResponse = user.leaveCathering(user, qr.getText());
                        scanQrButton.setVisible(true);
                        leaveCateringButton.setVisible(false);
                        scanResponseLabel.setText(scanResponse);
                        PauseTransition visiblePause = new PauseTransition(
                                Duration.seconds(5)
                        );
                        visiblePause.setOnFinished(
                                event -> scanResponseLabel.setVisible(false)
                        );
                        visiblePause.play();
                    } catch (RemoteException re) {
                        re.printStackTrace();
                    }
                }
            };
            leaveCateringButton.setOnAction(event0);
        }
    }
}
