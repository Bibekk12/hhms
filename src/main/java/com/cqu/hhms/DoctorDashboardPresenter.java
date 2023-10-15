/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.cqu.hhms;

import com.cqu.hhms.model.Auth;
import com.cqu.hhms.model.Patient;
import com.cqu.hhms.model.User;
import com.cqu.hhms.utils.DB;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.SelectionMode;


import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class DoctorDashboardPresenter implements Initializable {

    @FXML
    private Text txtWelcome;
    @FXML
    private Button btnLogout;
    @FXML
    private TableColumn<Patient, String> colName;
    @FXML
    private TableColumn<Patient, String> colPhone;
    @FXML
    private TableColumn<Patient, String> colAddress;
    @FXML
    private Button btnAddEHR;
    @FXML
    private Button btnViewEHR;
    @FXML
    private TableView<Patient> tablePatient;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User currentUser = Auth.getInstance().getAuthUser();
        txtWelcome.setText("Welcome, " + currentUser.getFullName());
        populatePatients();

    }

private void populatePatients() {
    // Fetch all patients from the database
    List<Patient> patients = DB.selectPatients();
    
    // Bind the patient data to the table
    ObservableList<Patient> observablePatients = FXCollections.observableArrayList(patients);
    tablePatient.setItems(observablePatients);
    
    // Set cell value factories for the columns
    colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getFullName()));
    colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getPhone()));
    colAddress.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress())); //  address is directly in Patient and not in User
    
    // Add a listener to the selection model to handle selection changes
    tablePatient.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        if (newSelection != null) {
            EHRPresenter.selectedPatient = newSelection;
            AddEHRPresenter.selectedPatient = newSelection;
        }
    });
}

    @FXML
    private void handleLogout(ActionEvent event) {
        Auth.getInstance().clearAuthUser();
        Util.redirectTo("USER_LOGIN", event);
    }


    @FXML
    private void handleAddEHR(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ADD_EHR.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewEHR(ActionEvent event) {
        if (EHRPresenter.selectedPatient != null) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("EHR.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Util.showAlert(Alert.AlertType.ERROR, "No Patient Selected", "Please select a Patient", event);
        }
    }
}
