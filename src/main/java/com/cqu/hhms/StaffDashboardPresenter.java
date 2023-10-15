package com.cqu.hhms;

import com.cqu.hhms.model.Auth;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.text.Text;
import com.cqu.hhms.model.User;

public class StaffDashboardPresenter implements Initializable {

    @FXML
    private Text txtWelcome;
    @FXML
    private Button btnPatientRegistrationNav;
    @FXML
    private Button btnRegisterDoctorNav;
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnMakeAppointment;
    @FXML
    private Button btnBillPatient;
    @FXML
    private Button btnViewAppointments;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User currentUser = Auth.getInstance().getAuthUser();
        txtWelcome.setText("Welcome, " + currentUser.getFullName());
    }

    @FXML
    private void handlePatientRegistrationNav(ActionEvent event) {
           Util.redirectTo("PATIENT_REGISTRATION", event);
    }

    @FXML
    private void handleRegisterDoctorNav(ActionEvent event) {
        Util.redirectTo("DOCTOR_REGISTRATION", event);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Auth.getInstance().clearAuthUser();
        Util.redirectTo("USER_LOGIN", event);
    }

    @FXML
    private void handleMakeAppointment(ActionEvent event) {
        Util.redirectTo("MAKE_APPOINTMENT", event);
    }

    @FXML
    private void handleBillPatient(ActionEvent event) {
        Util.redirectTo("BILL_PATIENT", event);
    }

    @FXML
    private void handleViewAppointments(ActionEvent event) {
                Util.redirectTo("VIEW_APPOINTMENTS", event);

    }

}
