package com.cqu.hhms.model;

import java.util.List;

public class Billing {
    private int billID;
    private Patient patient;
    private List<Service> services; // A list of services for a given bill
    private Double totalAmount;
   
    
    
    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
     
    public double getTotalAmount(){
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

}