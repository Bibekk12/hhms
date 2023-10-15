package com.cqu.hhms.utils;

import com.cqu.hhms.model.Role;
import com.cqu.hhms.model.User;
import com.cqu.hhms.Util;
import com.cqu.hhms.model.Appointment;
import com.cqu.hhms.model.Billing;
import com.cqu.hhms.model.ElectronicHealthRecord;
import com.cqu.hhms.model.Patient;
import com.cqu.hhms.model.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DB {

    static String MYSQL_URL;
    static String DB_NAME;
    static String DB_URL;

    private static String USERNAME;
    private static String PASSWORD;
    private static Connection sqlConnection;
    private static PreparedStatement createDatabase;
    public static Connection conn;

    public static void init() {
        MYSQL_URL = "jdbc:mysql://localhost:3306";
        DB_NAME = "hhms";
        DB_URL = MYSQL_URL + "/" + DB_NAME;
        USERNAME = "root";
        PASSWORD = "new_password_here";

        try {
            //Connects to the SQL instance
            sqlConnection = DriverManager.getConnection(MYSQL_URL, USERNAME, PASSWORD);
            createDatabase = sqlConnection.prepareStatement("create database if not exists " + DB_NAME);
            createDatabase.executeUpdate();
            if (sqlConnection != null) {
                sqlConnection.close();
            }
            //Connects to database
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
        }
    }

    public static void setupTables() throws SQLException {
        // Role table creation
        PreparedStatement createRole = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Role ("
                + "roleID INT PRIMARY KEY AUTO_INCREMENT, "
                + "roleName VARCHAR(255) NOT NULL UNIQUE)"
        );
        createRole.executeUpdate();

        // User table creation
        PreparedStatement createUser = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS User ("
                + "userID INT PRIMARY KEY AUTO_INCREMENT, "
                + "username VARCHAR(255) NOT NULL UNIQUE, "
                + "password VARCHAR(255) NOT NULL, "
                + "fullName VARCHAR(255), "
                + "email VARCHAR(255) UNIQUE, "
                + "phone VARCHAR(255) UNIQUE, "
                + "otherDetails VARCHAR(255), "
                + "roleID INT, "
                + "FOREIGN KEY (roleID) REFERENCES Role(roleID))"
        );
        createUser.executeUpdate();

        // Patient table creation
        PreparedStatement createPatient = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Patient ("
                + "patientID INT PRIMARY KEY AUTO_INCREMENT, "
                + "address VARCHAR(255), "
                + "userID INT, "
                + "FOREIGN KEY (userID) REFERENCES User(userID))"
        );
        createPatient.executeUpdate();

        // ElectronicHealthRecord table creation
        PreparedStatement createEHR = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS ElectronicHealthRecord ("
                + "patientID INT PRIMARY KEY, "
                + "medicalHistory TEXT, "
                + "allergies TEXT, "
                + "medications TEXT, "
                + "otherDetails TEXT, "
                + "FOREIGN KEY (patientID) REFERENCES Patient(patientID))"
        );
        createEHR.executeUpdate();

        // Appointment table creation
        PreparedStatement createAppointment = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Appointment ("
                + "appointmentID INT PRIMARY KEY AUTO_INCREMENT, "
                + "timeSlot TIMESTAMP, "
                + "doctorID INT, "
                + "patientID INT, "
                + "FOREIGN KEY (doctorID) REFERENCES User(userID), "
                + "FOREIGN KEY (patientID) REFERENCES Patient(patientID))"
        );
        createAppointment.executeUpdate();

        // Service table creation
        PreparedStatement createService = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Service ("
                + "serviceID INT PRIMARY KEY AUTO_INCREMENT, "
                + "description VARCHAR(255), "
                + "cost DECIMAL)"
        );
        createService.executeUpdate();

        // Billing table creation
        PreparedStatement createBilling = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Billing ("
                + "billID INT PRIMARY KEY AUTO_INCREMENT, "
                + "totalAmount DECIMAL, "
                + "patientID INT, "
                + "FOREIGN KEY (patientID) REFERENCES Patient(patientID))"
        );
        createBilling.executeUpdate();

        // BillingService table creation (for many-to-many relationship)
        PreparedStatement createBillingService = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS BillingService ("
                + "billID INT, "
                + "serviceID INT, "
                + "FOREIGN KEY (billID) REFERENCES Billing(billID), "
                + "FOREIGN KEY (serviceID) REFERENCES Service(serviceID), "
                + "PRIMARY KEY (billID, serviceID))"
        );
        createBillingService.executeUpdate();

        // Report table creation
        PreparedStatement createReport = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Report ("
                + "reportID INT PRIMARY KEY AUTO_INCREMENT, "
                + "data TEXT, "
                + "createdByUserID INT, "
                + "FOREIGN KEY (createdByUserID) REFERENCES User(userID))"
        );
        createReport.executeUpdate();
    }

    public static void prefillData() throws SQLException {
        // Inserting default roles
        PreparedStatement insertRole = conn.prepareStatement(
                "INSERT INTO Role (roleName) VALUES (?), (?), (?) ON DUPLICATE KEY UPDATE roleName=roleName"
        );
        insertRole.setString(1, "staff");
        insertRole.setString(2, "doctor");
        insertRole.setString(3, "patient");
        insertRole.executeUpdate();

        // Setting up default staff user
        String username = "staff";
        User usr = selectUser(username);
        if (usr == null) {
            String hashedPass = Util.hashPassword("staff123");

            // Get roleID for staff
            PreparedStatement getStaffRole = conn.prepareStatement(
                    "SELECT roleID FROM Role WHERE roleName = ?"
            );
            getStaffRole.setString(1, "staff");
            ResultSet rs = getStaffRole.executeQuery();
            if (rs.next()) {
                int staffRoleID = rs.getInt("roleID");
                PreparedStatement insertStaff = conn.prepareStatement(
                        "INSERT INTO User (username, password,fullName, email, phone, otherDetails, roleID) VALUES (?, ?, ?, ?, ?, ?, ?)"
                );
                insertStaff.setString(1, username);
                insertStaff.setString(2, hashedPass);
                insertStaff.setString(3, "John");
                insertStaff.setString(4, "john@cqu.com");
                insertStaff.setString(5, "042212121");
                insertStaff.setString(6, "Administration Department. Can make appointment with Doctors.");
                insertStaff.setInt(7, staffRoleID);
                insertStaff.executeUpdate();
            }
        }
        // Inserting default services
        String[] defaultServices = {"General Check-up", "X-ray", "Blood Test", "MRI Scan"};
        double[] serviceCosts = {50.0, 100.0, 20.0, 200.0};

        for (int i = 0; i < defaultServices.length; i++) {
            Service service = selectService(defaultServices[i]);
            if (service == null) {
                PreparedStatement insertService = conn.prepareStatement(
                        "INSERT INTO Service (description, cost) VALUES (?, ?)"
                );
                insertService.setString(1, defaultServices[i]);
                insertService.setDouble(2, serviceCosts[i]); // use setDouble method
                insertService.executeUpdate();
            }
        }

    }

    private static Service selectService(String description) throws SQLException {
        Service service = null;

        String SQL = "SELECT * FROM Service WHERE description = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, description);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                service = new Service();
                service.setServiceID(rs.getInt("serviceID"));
                service.setDescription(rs.getString("description"));
                service.setCost(rs.getDouble("cost"));
            }
        }
        return service;
    }

    public static User insertUser(User user) throws SQLException {
        String SQL = "INSERT INTO User (username, password, fullName, email, phone, otherDetails, roleID) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, Util.hashPassword(user.getPassword()));
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getOtherDetails());
            pstmt.setInt(7, user.getRole().getRoleID());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserID(rs.getInt(1));
                    }
                }
                return user;
            }
        } catch (SQLException ex) {
            throw ex;
        }
        return null;
    }

    public static User selectUser(String username, String hashedPassword) {
        String SQL = "SELECT u.*, r.roleName FROM User u LEFT JOIN Role r ON u.roleID = r.roleID WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, username);
            pstmt.setString(2, Util.hashPassword(hashedPassword));

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("userID"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("fullName"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setOtherDetails(rs.getString("otherDetails"));

                Role role = new Role();
                role.setRoleID(rs.getInt("roleID"));
                role.setRoleName(rs.getString("roleName"));
                user.setRole(role);

                return user;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static List<User> getAllPatients() {
        List<User> patients = new ArrayList<>();
        String SQL = "SELECT u.*, r.roleName FROM User u JOIN Role r ON u.roleID = r.roleID WHERE r.roleName = 'patient'";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User patient = new User();
                patient.setUserID(rs.getInt("userID"));
                patient.setUsername(rs.getString("username"));
                patient.setFullName(rs.getString("fullName"));
                patient.setEmail(rs.getString("email"));
                patient.setPhone(rs.getString("phone"));
                patient.setOtherDetails(rs.getString("otherDetails"));

                Role role = new Role();
                role.setRoleID(rs.getInt("roleID"));
                role.setRoleName(rs.getString("roleName"));
                patient.setRole(role);

                patients.add(patient);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return patients;
    }

    public static List<User> getAllDoctors() {
        List<User> doctors = new ArrayList<>();
        String SQL = "SELECT u.*, r.roleName FROM User u JOIN Role r ON u.roleID = r.roleID WHERE r.roleName = 'doctor'";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User doctor = new User();
                doctor.setUserID(rs.getInt("userID"));
                doctor.setUsername(rs.getString("username"));
                doctor.setFullName(rs.getString("fullName"));
                doctor.setEmail(rs.getString("email"));
                doctor.setPhone(rs.getString("phone"));
                doctor.setOtherDetails(rs.getString("otherDetails"));

                Role role = new Role();
                role.setRoleID(rs.getInt("roleID"));
                role.setRoleName(rs.getString("roleName"));
                doctor.setRole(role);

                doctors.add(doctor);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return doctors;
    }

    public static User selectUser(String username) {
        // We're joining the User table with the Role table to get the roleName.
        String SQL = "SELECT u.*, r.roleName FROM User u LEFT JOIN Role r ON u.roleID = r.roleID WHERE u.username = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("userID"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("fullName"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setOtherDetails(rs.getString("otherDetails"));

                Role role = new Role();
                role.setRoleID(rs.getInt("roleID"));
                role.setRoleName(rs.getString("roleName"));
                user.setRole(role);

                return user;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return null; // Return null if an exception occurs or no user is found.
    }

    public static Patient insertPatient(Patient patient) throws SQLException {
        String SQL = "INSERT INTO Patient (address, userID) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, patient.getAddress());
            pstmt.setInt(2, patient.getUser().getUserID());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        patient.setPatientID(rs.getInt(1));
                    }
                }
                return patient;
            }
        } catch (SQLException ex) {
            throw ex;
        }
        return null;
    }

    public static Role selectRoleByName(String roleName) {
        String SQL = "SELECT * FROM Role WHERE roleName = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, roleName);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Role role = new Role();
                role.setRoleID(rs.getInt("roleID"));
                role.setRoleName(rs.getString("roleName"));
                return role;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return null; // Return null if no role is found or an exception occurs.
    }

    public static void insertAppointment(User doctor, User patient, Timestamp timeSlot) throws SQLException {
        String SQL = "INSERT INTO Appointment (timeSlot, doctorID, patientID) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setTimestamp(1, timeSlot);
            pstmt.setInt(2, doctor.getUserID());
            String PATIENT_SQL = "SELECT * FROM Patient WHERE userID = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(PATIENT_SQL)) {
                pstmt2.setInt(1, patient.getUserID());
                ResultSet rs = pstmt2.executeQuery();
                if (rs.next()) {
                    pstmt.setInt(3, rs.getInt("patientID"));

                }
            } catch (SQLException ex) {
                throw ex;
            }

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public static List<Patient> selectPatients() {
        List<Patient> patients = new ArrayList<>();
        // Joining Patient with User to get the required details
        String SQL = "SELECT p.*, u.fullName, u.phone "
                + "FROM Patient p "
                + "JOIN User u ON p.userID = u.userID";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Patient patient = new Patient();
                User user = new User();

                patient.setPatientID(rs.getInt("patientID"));
                patient.setAddress(rs.getString("address"));
                user.setUserID(rs.getInt("userID"));
                user.setFullName(rs.getString("fullName"));
                user.setPhone(rs.getString("phone"));
                patient.setUser(user);
                patients.add(patient);
                System.out.println(patient);

            }
            System.out.println(patients);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return patients;
    }

    public static ElectronicHealthRecord selectEHR(Patient selectedPatient) {
        ElectronicHealthRecord ehr = null;

        // Your SQL query to retrieve the EHR details for the given patient
        String SQL = "SELECT * FROM ElectronicHealthRecord WHERE patientID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, selectedPatient.getPatientID()); // Setting the patientID parameter

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ehr = new ElectronicHealthRecord();

                ehr.setPatient(selectedPatient);
                ehr.setMedicalHistory(rs.getString("medicalHistory"));
                ehr.setAllergies(rs.getString("allergies"));
                ehr.setMedications(rs.getString("medications"));
                ehr.setOtherDetails(rs.getString("otherDetails"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return ehr; // Will return null if there's no EHR for the given patient
    }

    public static void insertEHR(ElectronicHealthRecord ehr) throws SQLException {
        String SQL = "INSERT INTO ElectronicHealthRecord (patientID, medicalHistory, allergies, medications, otherDetails) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, ehr.getPatient().getPatientID());
            pstmt.setString(2, ehr.getMedicalHistory());
            pstmt.setString(3, ehr.getAllergies());
            pstmt.setString(4, ehr.getMedications());
            pstmt.setString(5, ehr.getOtherDetails());

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public static void updateEHR(ElectronicHealthRecord ehr) throws SQLException {
        String SQL = "UPDATE ElectronicHealthRecord SET medicalHistory = ?, allergies = ?, medications = ?, otherDetails = ? WHERE patientID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, ehr.getMedicalHistory());
            pstmt.setString(2, ehr.getAllergies());
            pstmt.setString(3, ehr.getMedications());
            pstmt.setString(4, ehr.getOtherDetails());
            pstmt.setInt(5, ehr.getPatient().getPatientID());

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public static List<Service> selectServices() {
        List<Service> services = new ArrayList<>();

        String SQL = "SELECT * FROM Service";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Service service = new Service();
                service.setServiceID(rs.getInt("serviceID"));
                service.setDescription(rs.getString("description"));
                service.setCost(rs.getDouble("cost"));
                services.add(service);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return services;
    }

    public static int insertBilling(Double totalAmount, int patientID) {
        int generatedBillID = -1;

        String SQL = "INSERT INTO Billing (totalAmount, patientID) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDouble(1, totalAmount);
            pstmt.setInt(2, patientID);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedBillID = rs.getInt(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return generatedBillID;
    }

    public static void insertBillingService(int billID, int serviceID) {
        String SQL = "INSERT INTO BillingService (billID, serviceID) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, billID);
            pstmt.setInt(2, serviceID);

            pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static ArrayList<Billing> selectBills(Patient selectedPatient) {
        ArrayList<Billing> bills = new ArrayList<>();

        // SQL query to retrieve the bills for the given patient
        String fetchBillsSQL = "SELECT * FROM Billing WHERE patientID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(fetchBillsSQL)) {
            pstmt.setInt(1, selectedPatient.getPatientID());

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Billing bill = new Billing();

                bill.setBillID(rs.getInt("billID"));
                bill.setPatient(selectedPatient);
                bill.setTotalAmount(rs.getDouble("totalAmount")); // Fetch and set the totalAmount

                // Fetch services for this bill
                List<Service> servicesForBill = new ArrayList<>();
                String fetchServicesSQL = "SELECT s.* FROM Service s "
                        + "JOIN BillingService bs ON s.serviceID = bs.serviceID "
                        + "WHERE bs.billID = ?";
                try (PreparedStatement pstmtServices = conn.prepareStatement(fetchServicesSQL)) {
                    pstmtServices.setInt(1, bill.getBillID());
                    ResultSet rsServices = pstmtServices.executeQuery();

                    while (rsServices.next()) {
                        Service service = new Service();
                        service.setServiceID(rsServices.getInt("serviceID"));
                        service.setDescription(rsServices.getString("description"));
                        service.setCost(rsServices.getDouble("cost"));
                        servicesForBill.add(service);
                    }
                }
                bill.setServices(servicesForBill); // Set the services to the bill

                bills.add(bill);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return bills;
    }

    public static ArrayList<Appointment> selectAppointments() {
        ArrayList<Appointment> appointments = new ArrayList<>();

        String selectAppointmentsSQL = "SELECT a.appointmentID, a.timeSlot, d.*, pu.*, p.address "
                + "FROM Appointment a "
                + "JOIN User d ON a.doctorID = d.userID " // Joining with User table for doctor details
                + "JOIN Patient p ON a.patientID = p.patientID " // Joining with Patient table for patient details
                + "JOIN User pu ON p.userID = pu.userID ";         // Joining with User table for patient's user details

        try (PreparedStatement pstmt = conn.prepareStatement(selectAppointmentsSQL); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Appointment appointment = new Appointment();
                User doctor = new User();
                Patient patient = new Patient();
                User userForPatient = new User();

                // Populate the doctor object
                doctor.setUserID(rs.getInt("d.userID"));
                doctor.setFullName(rs.getString("d.fullName"));
                doctor.setPhone(rs.getString("d.phone"));
                // ... Populate other fields for doctor as needed ...

                // Populate the userForPatient object
                userForPatient.setUserID(rs.getInt("pu.userID"));
                userForPatient.setFullName(rs.getString("pu.fullName"));
                userForPatient.setPhone(rs.getString("pu.phone"));
                // ... Populate other fields for userForPatient as needed ...

                patient.setUser(userForPatient);
                patient.setAddress(rs.getString("p.address"));

                appointment.setAppointmentID(rs.getInt("a.appointmentID"));
                appointment.setTimeSlot(rs.getTimestamp("a.timeSlot"));
                appointment.setDoctor(doctor);
                appointment.setPatient(patient);

                appointments.add(appointment);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public static void deleteAllTables() throws SQLException {
        // Deleting BillingService table
        PreparedStatement deleteBillingService = conn.prepareStatement("DROP TABLE IF EXISTS BillingService");
        deleteBillingService.executeUpdate();

        // Deleting ElectronicHealthRecord, Appointment, and Billing tables
        PreparedStatement deleteEHR = conn.prepareStatement("DROP TABLE IF EXISTS ElectronicHealthRecord");
        deleteEHR.executeUpdate();

        PreparedStatement deleteAppointment = conn.prepareStatement("DROP TABLE IF EXISTS Appointment");
        deleteAppointment.executeUpdate();

        PreparedStatement deleteBilling = conn.prepareStatement("DROP TABLE IF EXISTS Billing");
        deleteBilling.executeUpdate();

        // Deleting Patient table
        PreparedStatement deletePatient = conn.prepareStatement("DROP TABLE IF EXISTS Patient");
        deletePatient.executeUpdate();

        // Deleting Report table
        PreparedStatement deleteReport = conn.prepareStatement("DROP TABLE IF EXISTS Report");
        deleteReport.executeUpdate();

        // Deleting User table
        PreparedStatement deleteUser = conn.prepareStatement("DROP TABLE IF EXISTS User");
        deleteUser.executeUpdate();

        // Deleting Service table
        PreparedStatement deleteService = conn.prepareStatement("DROP TABLE IF EXISTS Service");
        deleteService.executeUpdate();

        // Deleting Role table
        PreparedStatement deleteRole = conn.prepareStatement("DROP TABLE IF EXISTS Role");
        deleteRole.executeUpdate();
    }

}
