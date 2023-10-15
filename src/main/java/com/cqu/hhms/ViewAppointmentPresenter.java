/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.cqu.hhms;

import com.cqu.hhms.model.Appointment;
import com.cqu.hhms.utils.DB;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


public class ViewAppointmentPresenter implements Initializable {

    @FXML
    private Text txtWelcome;
    @FXML
    private Button btnBack;
    @FXML
    private TableView<Appointment> tableAppts;
    @FXML
    private TableColumn<Appointment, String> pName;
    @FXML
    private TableColumn<Appointment, String> pPhone;
    @FXML
    private TableColumn<Appointment, String> docName;
    @FXML
    private TableColumn<Appointment, String> apptDateAndTime;
    @FXML
    private Button btnSearch;
    @FXML
    private TextField txtSearchKey;
    private final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateAppointments();
    }

    private void populateAppointments() {
        // Fetch all appointments from the database
        List<Appointment> appointments = DB.selectAppointments();

        // Bind the appointment data to the table
        ObservableList<Appointment> observableAppointments = FXCollections.observableArrayList(appointments);
        allAppointments.setAll(appointments); // Update the original list

        tableAppts.setItems(allAppointments);

        // Set cell value factories for the columns
        pName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatient().getUser().getFullName()));
        pPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatient().getUser().getPhone()));
        docName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDoctor().getFullName()));

        // Assuming timeSlot returns a Timestamp object, converting it to string
        apptDateAndTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimeSlot().toString()));
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Util.redirectTo("STAFF_DASHBOARD", event);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String searchKey = txtSearchKey.getText().toLowerCase();

        // Create a filtered list based on the original list of appointments
        FilteredList<Appointment> filteredAppointments = new FilteredList<>(allAppointments, appointment -> {
            // If the search key is empty, display all records
            if (searchKey == null || searchKey.isEmpty()) {
                return true;
            }

            // Filter based on the patient's name
            String patientName = appointment.getPatient().getUser().getFullName().toLowerCase();
            return patientName.contains(searchKey); // Filter matches patient name
        });

        // Set the filtered list to the table view
        tableAppts.setItems(filteredAppointments);
    }

}
