/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.cqu.hhms;

import com.cqu.hhms.model.Auth;
import com.cqu.hhms.model.Role;
import com.cqu.hhms.model.User;
import com.cqu.hhms.model.Patient;
import com.cqu.hhms.utils.DB;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


public class PatientRegistrationPresenter implements Initializable {

    @FXML
    private Button btnRegister;
    @FXML
    private Text txtWelcome;
    @FXML
    private TextField name;
    @FXML
    private TextField email;
    @FXML
    private TextField phone;
    @FXML
    private TextField address;
    @FXML
    private TextArea details;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button btnBack;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            String uName = username.getText();
            String pass = password.getText();
            String fullName = name.getText();
            String uEmail = email.getText();
            String uPhone = phone.getText();
            String uAddress = address.getText();
            String uDetails = details.getText();

            if (uName.isEmpty() || pass.isEmpty() || fullName.isEmpty() || uEmail.isEmpty() || uPhone.isEmpty() || uAddress.isEmpty()) {
                Util.showAlert(Alert.AlertType.ERROR, "Regitration Failed", "Please enter all details", event);
                return;
            }
            User existingUser = DB.selectUser(uName);
            if (existingUser != null) {
                Util.showAlert(Alert.AlertType.ERROR, "Regitration Failed", "Username already exists", event);
                return;
            }

            User newUser = new User();
            newUser.setUsername(uName);
            newUser.setPassword(pass);
            newUser.setFullName(fullName);
            newUser.setEmail(uEmail);
            newUser.setPhone(uPhone);
            newUser.setOtherDetails(uDetails);

            Role patientRole = DB.selectRoleByName("patient");
            newUser.setRole(patientRole);

            // Insert user
            User insertedUser = DB.insertUser(newUser);

            if (insertedUser == null) {
                Util.showAlert(Alert.AlertType.ERROR, "Registration Failed", "Unknown error!", event);
                return;
            }

            Patient newPatient = new Patient();
            newPatient.setAddress(uAddress);
            newPatient.setUser(insertedUser);

            // Insert patient
            DB.insertPatient(newPatient);
            Util.showAlert(Alert.AlertType.INFORMATION, "SUCCESS", "Successfully created an account for " + uName, event);
                        Util.redirectTo("STAFF_DASHBOARD", event);


        } catch (Exception e) {
            Util.showAlert(Alert.AlertType.ERROR, "Registration Failed", e.getMessage(), event);

        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Util.redirectTo("STAFF_DASHBOARD", event);
    }

}
