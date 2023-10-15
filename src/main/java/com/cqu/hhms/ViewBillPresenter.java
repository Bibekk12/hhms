/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.cqu.hhms;

import com.cqu.hhms.model.Billing;
import com.cqu.hhms.model.Patient;
import com.cqu.hhms.model.Service;
import com.cqu.hhms.utils.DB;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;


public class ViewBillPresenter implements Initializable {

    @FXML
    private Text txtWelcome;
    @FXML
    private TextArea textareaBill;
    /**
     * Initializes the controller class.
     */
    public static Patient selectedPatient;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtWelcome.setText("Billing Record for " + selectedPatient.getUser().getFullName());

        // Retrieve the EHR for the selected patient
        ArrayList<Billing> bills = DB.selectBills(selectedPatient);
        // Display the records on the text area nicely

        // Display the records on the text area nicely
        if (bills != null) {
            StringBuilder billingDetails = new StringBuilder();

            for (Billing bill : bills) {
                billingDetails.append("Bill ID: ").append(bill.getBillID()).append("\n");
                billingDetails.append("Total Amount: $").append(bill.getTotalAmount()).append("\n");

                List<Service> services = bill.getServices();
                if (services != null && !services.isEmpty()) {
                    billingDetails.append("Services:\n");

                    for (Service service : services) {
                        billingDetails.append("  - ").append(service.getDescription());
                        billingDetails.append(" (Cost: $").append(service.getCost()).append(")\n");
                    }
                }

                billingDetails.append("------------------------------\n"); // Separator for clarity
            }

            textareaBill.setText(billingDetails.toString());
        } else {
            textareaBill.setText("No Bill found for " + selectedPatient.getUser().getFullName());
        }
    }

}
