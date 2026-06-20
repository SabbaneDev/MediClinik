package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// Accès aux données des rendez-vous
public class AppointmentDAO {

    // Ajouter un nouveau rendez-vous
    public boolean addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (appointment_date, patient_id, doctor_name, description) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(appointment.getAppointmentDate()));
            pstmt.setInt(2, appointment.getPatientId());
            pstmt.setString(3, appointment.getDoctorName());
            pstmt.setString(4, appointment.getDescription());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Récupèrer tous les rendez-vous
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(p.first_name, ' ', p.last_name) AS patient_name " +
                     "FROM appointments a " +
                     "LEFT JOIN patients p ON a.patient_id = p.id " +
                     "ORDER BY a.appointment_date ASC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getInt("id"),
                    rs.getTimestamp("appointment_date").toLocalDateTime(),
                    rs.getInt("patient_id"),
                    rs.getString("doctor_name"),
                    rs.getString("description"),
                    rs.getString("patient_name") != null ? rs.getString("patient_name") : "Unknown Patient"
                );
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    // Met à jour un rendez-vous
    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET appointment_date = ?, patient_id = ?, doctor_name = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(appointment.getAppointmentDate()));
            pstmt.setInt(2, appointment.getPatientId());
            pstmt.setString(3, appointment.getDoctorName());
            pstmt.setString(4, appointment.getDescription());
            pstmt.setInt(5, appointment.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Supprimer un rendez-vous
    public boolean deleteAppointment(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Chercher les rendez-vous par mots-clés
    public List<Appointment> searchAppointments(String query) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(p.first_name, ' ', p.last_name) AS patient_name " +
                     "FROM appointments a " +
                     "LEFT JOIN patients p ON a.patient_id = p.id " +
                     "WHERE a.doctor_name LIKE ? OR p.first_name LIKE ? OR p.last_name LIKE ? " +
                     "ORDER BY a.appointment_date ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = new Appointment(
                        rs.getInt("id"),
                        rs.getTimestamp("appointment_date").toLocalDateTime(),
                        rs.getInt("patient_id"),
                        rs.getString("doctor_name"),
                        rs.getString("description"),
                        rs.getString("patient_name") != null ? rs.getString("patient_name") : "Unknown Patient"
                    );
                    appointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    // Compte le nombre total de rendez-vous
    public int getAppointmentCount() {
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Compte les rendez-vous futurs
    public int getUpcomingCount() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date >= NOW()";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting upcoming appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Récupèrer les rendez-vous futurs récents
    public List<Appointment> getRecentUpcomingAppointments(int limit) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(p.first_name, ' ', p.last_name) AS patient_name " +
                     "FROM appointments a " +
                     "LEFT JOIN patients p ON a.patient_id = p.id " +
                     "WHERE a.appointment_date >= NOW() " +
                     "ORDER BY a.appointment_date ASC LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = new Appointment(
                        rs.getInt("id"),
                        rs.getTimestamp("appointment_date").toLocalDateTime(),
                        rs.getInt("patient_id"),
                        rs.getString("doctor_name"),
                        rs.getString("description"),
                        rs.getString("patient_name") != null ? rs.getString("patient_name") : "Unknown Patient"
                    );
                    appointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recent upcoming appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }
}
