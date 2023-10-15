/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.cqu.hhms;

import com.cqu.hhms.model.ElectronicHealthRecord;
import com.cqu.hhms.model.Patient;
import com.cqu.hhms.utils.DB;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import javafx.scene.text.Text;

public class EHRPresenter implements Initializable {

    @FXML
    private Text txtWelcome;

    public static Patient selectedPatient;
    @FXML
    private TextArea textareaEHR;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtWelcome.setText("Electronic Health Record for " + selectedPatient.getUser().getFullName());

        // Retrieve the EHR for the selected patient
        ElectronicHealthRecord ehr = DB.selectEHR(selectedPatient);

        // Display the records on the text area nicely
        if (ehr != null) {
            StringBuilder ehrDetails = new StringBuilder();
            ehrDetails.append("Medical History: ").append(ehr.getMedicalHistory()).append("\n\n");
            ehrDetails.append("Allergies: ").append(ehr.getAllergies()).append("\n\n");
            ehrDetails.append("Medications: ").append(ehr.getMedications()).append("\n\n");
            ehrDetails.append("Other Details: ").append(ehr.getOtherDetails()).append("\n\n");

            textareaEHR.setText(ehrDetails.toString());
        } else {
            textareaEHR.setText("No EHR found for " + selectedPatient.getUser().getFullName());
        }
    }
}
