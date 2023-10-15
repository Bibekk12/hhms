/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.cqu.hhms;

import com.cqu.hhms.model.Patient;
import com.cqu.hhms.model.Service;
import com.cqu.hhms.utils.DB;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class AddServicePresenter implements Initializable {

    @FXML
    private Text txtWelcome;
    @FXML
    private Button btnBack;
    @FXML
    private TableView<Service> tableServices;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnSave;
    @FXML
    private ComboBox<Service> cboService; // made generic for Service
    @FXML
    private TableColumn<Service, String> colDesc;
    @FXML
    private TableColumn<Service, Double> colCost; //  cost is stored as a Double

    private final ObservableList<Service> serviceList = FXCollections.observableArrayList();
    private Double totalAmount = 0.0;

    public static Patient selectedPatient;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtWelcome.setText("Add Services For " + selectedPatient.getUser().getFullName());

        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        tableServices.setItems(serviceList);

        // Populate ComboBox with services
        List<Service> services = DB.selectServices();
        if (services != null && !services.isEmpty()) {
            System.out.println(services.get(0).getDescription());

            cboService.setItems(FXCollections.observableArrayList(services));
            cboService.setConverter(new StringConverter<Service>() {
                @Override
                public String toString(Service service) {
                    if (service != null) {
                        return service.getDescription();
                    }
                    return "";
                }

                @Override
                public Service fromString(String string) {
                    return services.stream().filter(service
                            -> service != null && service.getDescription().equals(string)
                    ).findFirst().orElse(null);
                }
            });
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Util.redirectTo("BILL_PATIENT", event);
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        Service selectedService = cboService.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
            serviceList.add(selectedService);
            totalAmount += selectedService.getCost();
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!serviceList.isEmpty()) {
            int billID = DB.insertBilling(totalAmount, selectedPatient.getPatientID());
            for (Service service : serviceList) {
                DB.insertBillingService(billID, service.getServiceID());
                Util.showAlert(Alert.AlertType.INFORMATION, "SUCCESS", "Bill info has been saved for "+selectedPatient.getUser().getFullName(), event);
                Util.redirectTo("BILL_PATIENT", event);
            }
        }
    }
}
