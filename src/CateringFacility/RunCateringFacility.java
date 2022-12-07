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
    public TextField facilityNameField;
    public TextField locationField;
    public TextField phoneNumberField;
    public Button registerButton;
    public TextField businessNumberField;
    public Label errorLabel;

    /*
    Server-client communicatie componenten
     */

    Registry registry;
    Registrar registrar;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("CateringFacility.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void pushButton(ActionEvent event) throws IOException, NotBoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        if (phoneNumberField.getText().isEmpty() || businessNumberField.getText().isEmpty()
                || locationField.getText().isEmpty() || facilityNameField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR,"Please complete all fields!");
            alert.showAndWait();
        } else if (!isLong(phoneNumberField, phoneNumberField.getText())
                || !isLong(businessNumberField, businessNumberField.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Please fill in the correct format!");
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

            CateringFacilityImpl cateringFacility = new CateringFacilityImpl(
                    phoneNumber, businessNumber, location, facilityName, registrar
            );
            registrar.enrollCatering(cateringFacility);
        }
    }

    private boolean isLong(TextField f, String msg)
    {
        try
        {
            Long.parseLong(f.getText());
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }
}
