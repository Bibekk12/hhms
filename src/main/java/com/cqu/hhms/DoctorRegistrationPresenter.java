/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.cqu.hhms;

import com.cqu.hhms.model.Role;
import com.cqu.hhms.model.User;
import com.cqu.hhms.utils.DB;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


public class DoctorRegistrationPresenter implements Initializable {

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
        // TODO
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            String uName = username.getText();
            String pass = password.getText();
            String fullName = name.getText();
            String uEmail = email.getText();
            String uPhone = phone.getText();
            String uDetails = details.getText();

            if (uName.isEmpty() || pass.isEmpty() || fullName.isEmpty() || uEmail.isEmpty() || uPhone.isEmpty()) {
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

            Role patientRole = DB.selectRoleByName("doctor");
            newUser.setRole(patientRole);

       DB.insertUser(newUser);


            Util.showAlert(Alert.AlertType.INFORMATION, "SUCCESS", "Successfully created an account for " + uName, event);
            Util.redirectTo("STAFF_DASHBOARD", event);

        } catch (SQLException e) {
            Util.showAlert(Alert.AlertType.ERROR, "Registration Failed", e.getMessage(), event);

        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Util.redirectTo("STAFF_DASHBOARD", event);
    }

}
