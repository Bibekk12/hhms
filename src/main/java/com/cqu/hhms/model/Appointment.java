package com.cqu.hhms.model;

import java.sql.Timestamp;

public class Appointment {
    private int appointmentID;
    private java.sql.Timestamp timeSlot;
    private User doctor;
    private Patient patient;

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public Timestamp getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Timestamp timeSlot) {
        this.timeSlot = timeSlot;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
}
