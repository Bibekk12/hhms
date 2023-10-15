package com.cqu.hhms.model;

public class Patient {
    private int patientID;
    private String address;
    private User user;
    private ElectronicHealthRecord electronicHealthRecord;

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ElectronicHealthRecord getElectronicHealthRecord() {
        return electronicHealthRecord;
    }

    public void setElectronicHealthRecord(ElectronicHealthRecord electronicHealthRecord) {
        this.electronicHealthRecord = electronicHealthRecord;
    }
}