/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.cqu.hhms;

import com.cqu.hhms.model.ElectronicHealthRecord;
import com.cqu.hhms.model.Patient;
import com.cqu.hhms.utils.DB;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AddEHRPresenter implements Initializable {

    @FXML
    private Text txtWelcome;
    @FXML
    private TextArea medicalHistory;
    @FXML
    private TextArea allergies;
    @FXML
    private TextArea medications;
    @FXML
    private TextArea otherDetails;
    @FXML
    private Button btnUpdate;
    /**
     * Initializes the controller class.
     */
    public static Patient selectedPatient;

    private ElectronicHealthRecord currentEHR;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtWelcome.setText("Update Electronic Health Record for " + selectedPatient.getUser().getFullName());

        currentEHR = DB.selectEHR(selectedPatient);

        if (currentEHR != null) {
            medicalHistory.setText(currentEHR.getMedicalHistory());
            allergies.setText(currentEHR.getAllergies());
            medications.setText(currentEHR.getMedications());
            otherDetails.setText(currentEHR.getOtherDetails());
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {

        ElectronicHealthRecord updateEHR = new ElectronicHealthRecord();
        updateEHR.setPatient(selectedPatient);
        
        // For medicalHistory:
        String medHistoryText = medicalHistory.getText();
        if (medHistoryText != null && !medHistoryText.trim().isEmpty()) {
            updateEHR.setMedicalHistory(medHistoryText.trim());
        } else {
            updateEHR.setMedicalHistory("Not Avialable"); // or some default value if you don't want to set it to null
        }

        // For allergies:
        String allergiesText = allergies.getText();
        if (allergiesText != null && !allergiesText.trim().isEmpty()) {
            updateEHR.setAllergies(allergiesText.trim());
        } else {
            updateEHR.setAllergies("Not Avialable");
        }

        // For medications:
        String medicationsText = medications.getText();
        if (medicationsText != null && !medicationsText.trim().isEmpty()) {
            updateEHR.setMedications(medicationsText.trim());
        } else {
            updateEHR.setMedications("Not Avialable");
        }

        // For otherDetails:
        String otherDetailsText = otherDetails.getText();
        if (otherDetailsText != null && !otherDetailsText.trim().isEmpty()) {
            updateEHR.setOtherDetails(otherDetailsText.trim());
        } else {
            updateEHR.setOtherDetails("Not Avialable");
        }
        try {

            if (currentEHR == null) {
                DB.insertEHR(updateEHR);
            } else {
                DB.updateEHR(updateEHR);
            }
            Util.showAlert(Alert.AlertType.INFORMATION, "Success", "EHR for " + selectedPatient.getUser().getFullName() + " is successfully updated!", event);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (SQLException ex) {
            Util.showAlert(Alert.AlertType.ERROR, "Adding EHR Failed!", ex.getMessage(), event);

        }
    }
}
