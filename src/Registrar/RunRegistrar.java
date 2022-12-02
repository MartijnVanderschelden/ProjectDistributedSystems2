package Registrar;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDate;

public class RunRegistrar extends Application {

    Registrar registrar;
    public Label dateLabel;
    public Button button;

    int count;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Registrar.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        count = 0;
    }

    public void pushButton(ActionEvent event) throws NoSuchAlgorithmException, RemoteException, AlreadyBoundException, SignatureException, InvalidKeyException {
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
        registry.bind("Registrar", registrar);
        System.out.println("Registrar started");

        //Initialisatie GUI
        dateLabel.setText(registrar.getDate().toString());
    }

    public void nextDay() throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        registrar.nextDay();
        dateLabel.setText(registrar.getDate().toString());
    }
}
