package Registrar;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;

public class RunRegistrar extends Application {

    Registrar registrar;
    public Label dateLabel;
    public Button button;

    int count;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Registrar.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setTitle("Registrar");

        count = 0;
    }

    public void pushButton(ActionEvent event) throws NoSuchAlgorithmException, IOException, AlreadyBoundException, SignatureException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException, InvalidAlgorithmParameterException {
        if(count == 0) {
            startRegistrar();
            button.setText("Next day");
            count++;
        }
        else nextDay();
    }

    public void startRegistrar() throws NoSuchAlgorithmException, RemoteException, AlreadyBoundException {
        //Instantie registrar aanmaken
        registrar = new RegistrarImpl();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("Registrar", registrar);
        System.out.println("Registrar started");

        //Initialisatie GUI
        dateLabel.setText(registrar.getDate().toString());

    }

    public void nextDay() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException, InvalidAlgorithmParameterException {
        registrar.nextDay();
        dateLabel.setText(registrar.getDate().toString());
    }
}
