package com.cqu.hhms;

import com.cqu.hhms.model.Auth;
import com.cqu.hhms.model.User;
import com.cqu.hhms.utils.DB;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class UserLoginPresenter implements Initializable {

    @FXML
    private Button btnLogin;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void login(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "Username or password cannot be empty!", event);
            return;
        }

        User user = DB.selectUser(username, password);
        System.out.println(user);

        if (user == null) {
            Util.showAlert(Alert.AlertType.ERROR, "Error", "Invalid credentials. Please try again!", event);
        } else {
            Auth.getInstance().setAuthUser(user);
            if (null == user.getRole().getRoleName()) {
                Util.showAlert(Alert.AlertType.ERROR, "Unknown User Role", "Empty user role!", event);
            } else {
                switch (user.getRole().getRoleName()) {
                    case "staff" ->
                        Util.redirectTo("STAFF_DASHBOARD", event);
                    case "doctor" ->
                        Util.redirectTo("DOCTOR_DASHBOARD", event);
                    case "patient" ->
                        Util.redirectTo("PATIENT_DASHBOARD", event);
                    default ->
                        Util.showAlert(Alert.AlertType.ERROR, "Unknown User Role", user.getRole().getRoleName() + " doesn't exist!", event);
                }
            }
        }
    }

}
