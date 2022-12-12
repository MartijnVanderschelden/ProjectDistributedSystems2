package Doctor;

import MatchingService.MatchingService;
import Registrar.Registrar;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.ArrayList;


public class RunDoctor extends Application {

    public Button readLogs;
    public Label explanationLogs;

    public TextField userNameField;

    /*
   Server-client communicatie componenten
    */
    MatchingService matchingService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Doctor.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setTitle("Doctor");
    }

    public void pushReadLogs(ActionEvent event) throws Exception {
        try {
            Registry registryMatching = LocateRegistry.getRegistry("localhost", 1101);
            matchingService = (MatchingService) registryMatching.lookup("MatchingService");
        } catch (NotBoundException nbe) {
            nbe.printStackTrace();
        }

        Doctor doctor = new DoctorImpl(matchingService);

        ArrayList<String> logs = doctor.readLogs(userNameField.getText());
        System.out.println("1: " + logs);
        byte[] signature = doctor.signLogs(logs, userNameField.getText());
        System.out.println("2: " + logs);
        System.out.println("3: " + signature);
        doctor.sendToMatchingService(logs, signature);
    }
}
