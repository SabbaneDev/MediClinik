package models;

import java.time.LocalDateTime;

// Représenter un rendez-vous médical
public class Appointment {

    private int id;
    private LocalDateTime appointmentDate;
    private int patientId;
    private String doctorName;
    private String description;

    private String patientName;

    // Constructeur par défaut
    public Appointment() {}

    // Constructeur sans id
    public Appointment(LocalDateTime appointmentDate, int patientId, String doctorName, String description) {
        this.appointmentDate = appointmentDate;
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.description = description;
    }

    // Constructeur avec id et nom du patient
    public Appointment(int id, LocalDateTime appointmentDate, int patientId, String doctorName, String description, String patientName) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.description = description;
        this.patientName = patientName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String toString() {
        return "Rendez-vous #" + id + " - Dr. " + doctorName + " on " + appointmentDate;
    }
}
