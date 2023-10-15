package com.cqu.hhms;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import com.cqu.hhms.model.User;
import com.cqu.hhms.utils.DB;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class AppointmentPresenter implements Initializable {

    @FXML
    private Text txtWelcome;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnMakeAppt;
    @FXML
    private DatePicker datepicker;
    @FXML
    private ComboBox<User> cboPatient;
    @FXML
    private ComboBox<User> cboDoctor;
    @FXML
    private TextField timepicker;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populatePatients();
        populateDoctors();
    }

    private void populatePatients() {
        List<User> patients = DB.getAllPatients();
        cboPatient.setItems(FXCollections.observableArrayList(patients));
        cboPatient.setCellFactory((comboBox) -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getFullName());
                }
            }
        });
    }

    private void populateDoctors() {
        List<User> doctors = DB.getAllDoctors();
        cboDoctor.setItems(FXCollections.observableArrayList(doctors));
        cboDoctor.setCellFactory((comboBox) -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getFullName());
                }
            }
        });
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Util.redirectTo("STAFF_DASHBOARD", event);
    }

    @FXML
    private void handleMakeAppt(ActionEvent event) {
        User doctor = cboDoctor.getValue();
        User patient = cboPatient.getValue();
        LocalDate date = datepicker.getValue();
        String timeText = timepicker.getText();
        // Validate the time input format using regex
        if (!timeText.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            Util.showAlert(Alert.AlertType.ERROR, "ERROR", "Enter 24 hour format for time. e.g. 13:00", event);
            return; // exit the function if time format is incorrect
        }

        LocalTime time = LocalTime.parse(timeText);
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // Convert LocalDateTime to Timestamp for database storage
        Timestamp timeSlot = Timestamp.valueOf(dateTime);

        try {
            DB.insertAppointment(doctor, patient, timeSlot);
            Util.showAlert(Alert.AlertType.INFORMATION, "SUCCESS", "Appointment Created Successfully at " + date + ", " + time, event);
            Util.redirectTo("STAFF_DASHBOARD", event);
        } catch (SQLException ex) {
            Util.showAlert(Alert.AlertType.ERROR, "ERROR", "SQL ERROR OCCURED. " + ex.getMessage(), event);
        }

    }
}
