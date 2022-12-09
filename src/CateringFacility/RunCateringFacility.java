package CateringFacility;

import Registrar.Registrar;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class RunCateringFacility extends Application {
    /*

     */
    public TextField businessNumberField;
    public TextField facilityNameField;
    public TextField locationField;
    public TextField phoneNumberField;
    public Button registerButton;
    public Button stopButton;
    public Label errorLabel;
    public Label qrCodeLabel;
    public Label businessNameLabel;
    public int count=0;
    /*
    Server-client communicatie componenten
     */
    CateringFacilityImpl cateringFacility;
    Registry registry;
    Registrar registrar;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("CateringFacility.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setTitle("Catering Facility");
    }

    public void pushRegisterButton(ActionEvent event) throws IOException, NotBoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        if (count == 0) {
            if (phoneNumberField.getText().isEmpty() || businessNumberField.getText().isEmpty()
                    || locationField.getText().isEmpty() || facilityNameField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please complete all fields!");
                alert.showAndWait();
            } else if (!isLong(phoneNumberField, phoneNumberField.getText())
                    || !isLong(businessNumberField, businessNumberField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in the correct format!");
                alert.showAndWait();
            } else {
            /*
        Registrarverbinding opzetten
         */
                registry = LocateRegistry.getRegistry("localhost", 1099);
                registrar = (Registrar) registry.lookup("Registrar");
                System.out.println("Catering Facility started");

            /*
        Credentials ingeven van het catering bedrijf
         */

                long phoneNumber = Long.parseLong(phoneNumberField.getText());
                long businessNumber = Long.parseLong(businessNumberField.getText());
                String location = locationField.getText();
                String facilityName = facilityNameField.getText();

                cateringFacility = new CateringFacilityImpl(
                        phoneNumber, businessNumber, location, facilityName, registrar
                );

                String[] uniqueCatering = registrar.checkAuthenticityCatering(cateringFacility);
                String alertString = "";

                if (uniqueCatering[0] != null) {
                    alertString += "Business number: " + uniqueCatering[0] + "\n";
                }
                if (uniqueCatering[1] != null) {
                    alertString += "Facility name: " + uniqueCatering[1] + "\n";
                }
                if (uniqueCatering[2] != null) {
                    alertString += "Location: " + uniqueCatering[2] + "\n";
                }
                if (uniqueCatering[3] != null) {
                    alertString += "Phone number: " + uniqueCatering[3] + "\n";
                }
                if (!alertString.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "There already exists a catering with the following fields: \n" + alertString);
                    alert.showAndWait();
                } else {
                    registrar.enrollCatering(cateringFacility);
                    businessNumberField.setText(Long.toString(cateringFacility.getBusinessNumber()));
                    businessNumberField.setEditable(false);
                    businessNumberField.setVisible(false);
                    facilityNameField.setText(cateringFacility.getFacilityName());
                    facilityNameField.setEditable(false);
                    facilityNameField.setVisible(false);
                    locationField.setText(cateringFacility.getLocation());
                    locationField.setEditable(false);
                    locationField.setVisible(false);
                    phoneNumberField.setText(Long.toString(cateringFacility.getPhoneNumber()));
                    phoneNumberField.setEditable(false);
                    phoneNumberField.setVisible(false);
                    registerButton.setVisible(false);
                    stopButton.setVisible(true);
                    businessNameLabel.setText("Catering Facility: "+ cateringFacility.getFacilityName());
                    qrCodeLabel.setText("QR Code: " +cateringFacility.getQRcode());
                }
            }
        }
    }

    public void pushStopButton(ActionEvent event) throws IOException{
        if(registrar != null && cateringFacility != null){
            registrar.disconnectCatering(cateringFacility);
        }
        System.exit(0);
    }

    @Override
    // Werkt niet, verwijdert CF niet
    public void stop() throws IOException{
        if(registrar != null && cateringFacility != null){
            registrar.disconnectCatering(cateringFacility);
        }
        System.exit(0);
    }

    private boolean isLong(TextField f, String msg) {
        try {
            Long.parseLong(f.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
