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
    public Button enrollButton;
    public Button scanQrButton;
    public TextField qr = new TextField();
    public Button leaveCateringButton;
    public Label userNameLabel;
    public Label phoneNumberLabel;
    public Button stopButton;

    int count;

    private UserImpl user;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("User.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("User");
        primaryStage.show();
        count = 0;
    }

    public void pushEnrollButton(ActionEvent event) throws NoSuchAlgorithmException, RemoteException, AlreadyBoundException, SignatureException, InvalidKeyException, NotBoundException {
        registry = LocateRegistry.getRegistry("localhost", 1099);
        registrar = (Registrar) registry.lookup("Registrar");
        Registry registryMixingProxy = LocateRegistry.getRegistry("localhost", 1100);
        mixingProxy = (MixingProxy) registryMixingProxy.lookup("MixingProxy");
        /*
        Gegevens user ingeven
         */
        String name = nameTextField.getText();
        long phoneNumber = Long.parseLong(phoneNumberTextfield.getText());

        if (nameTextField.getText().isEmpty() || phoneNumberTextfield.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please complete all fields!");
            alert.showAndWait();
        }
        else if (!registrar.userUniquePhoneNumber(phoneNumberTextfield.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There already exists an user with the same phone number");
            alert.showAndWait();
        } else {
            System.out.println("User started");
            user = new UserImpl(name, String.valueOf(phoneNumber), registrar, mixingProxy);
            registrar.enrollUser(user);
            System.out.println("Daily visits remaining: "+ user.getUserTokens().size());
            enrollButton.setVisible(false);
            phoneNumberTextfield.setVisible(false);
            nameTextField.setVisible(false);
            userNameLabel.setText("User: " + user.getName());
            userNameLabel.setVisible(true);
            phoneNumberLabel.setText("Phone: " + user.getPhone());
            phoneNumberLabel.setVisible(true);
            qr.setVisible(true);
            scanQrButton.setVisible(true);
            stopButton.setVisible(true);
            tokensRemainingLabel.setText("Daily visits remaining: "+ user.getUserTokens().size());
        }
    }

    public void pushScanCateringButton(ActionEvent event) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        String scanResponse = user.scanQR(qr.getText());
        scanResponseLabel.setText(scanResponse);
        scanResponseLabel.setTextFill(user.getColorAfterQrScan());
        tokensRemainingLabel.setText("Daily visits remaining: "+ user.getUserTokens().size());
        scanQrButton.setVisible(false);
        leaveCateringButton.setVisible(true);
    }


    public void pushExitCateringButton(ActionEvent event) throws IOException{
        String scanResponse = user.leaveCathering(user, qr.getText());
        scanQrButton.setVisible(true);
        leaveCateringButton.setVisible(false);
        scanResponseLabel.setText(scanResponse);
//                PauseTransition visiblePause = new PauseTransition(
//                        Duration.seconds(5)
//                );
//                visiblePause.setOnFinished(
//                        (finish) -> scanResponseLabel.setVisible(false)
//                );
//                visiblePause.play();
//                scanResponseLabel.setVisible(false);

    }

    public void pushStopButton(ActionEvent event) throws IOException{
        if(registrar != null && user != null){
            registrar.disconnectUser(user);
        }
        System.exit(0);
    }
}
