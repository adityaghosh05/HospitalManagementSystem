import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
public class HospitalManagementSystem extends JFrame {
    private static final String DB_URL = "";
    private static final String DB_USER = "";          
    private static final String DB_PASSWORD = ""; 
    private Connection connection;
    private JTabbedPane tabbedPane;
    public HospitalManagementSystem() {
        setTitle("Hospital Management System");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        if (!connectToDatabase()) {
            System.exit(1);
        }
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.addTab("Patients", createPatientPanel());
        tabbedPane.addTab("Doctors", createDoctorPanel());
        tabbedPane.addTab("Departments", createDepartmentPanel());
        tabbedPane.addTab("Medications", createMedicationPanel());
        tabbedPane.addTab("Prescriptions", createPrescriptionPanel());
        tabbedPane.addTab("Procedures", createProcedurePanel());
        tabbedPane.addTab("Performed Procedures", createUndergoesPanel());
        tabbedPane.addTab("Primary Care", createPrimaryCarePanel());
        tabbedPane.addTab("Secondary Care", createSecondaryCarePanel());
        tabbedPane.addTab("Interactions", createInteractionPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        add(tabbedPane);
        setVisible(true);
    }
    private boolean connectToDatabase() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            JOptionPane.showMessageDialog(this, 
                "Connected to Oracle Database Successfully!\n\n" +
                "Database: " + DB_URL, 
                "Connection Successful", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, 
                "Oracle JDBC Driver not found!\n\n" +
                "Please add ojdbc8.jar to your classpath.\n" +
                "Download from: https://www.oracle.com/database/technologies/jdbc-drivers-12c-downloads.html",
                "Driver Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database Connection Failed!\n\n" +
                "Please verify:\n" +
                "1. Oracle Database is running\n" +
                "2. Connection URL is correct: " + DB_URL + "\n" +
                "3. Username and password are correct\n" +
                "4. Database is accessible\n\n" +
                "Error: " + e.getMessage(),
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel formPanel = new JPanel(new GridLayout(7, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Patient Information", 
            0, 0, new Font("Arial", Font.BOLD, 14)));
        JTextField patientIdField = new JTextField();
        JTextField ssnField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField conditionField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField permanentAddressField = new JTextField();
        JTextField permanentPhoneField = new JTextField();
        JTextField birthdateField = new JTextField();
        JTextField sexField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField primaryDoctorField = new JTextField();
        JTextField secondaryDoctorField = new JTextField();
        formPanel.add(createLabel("Patient ID:"));
        formPanel.add(patientIdField);
        formPanel.add(createLabel("SSN (9 digits):"));
        formPanel.add(ssnField);
        formPanel.add(createLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(createLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(createLabel("Condition:"));
        formPanel.add(conditionField);
        formPanel.add(createLabel("Current Address:"));
        formPanel.add(addressField);
        formPanel.add(createLabel("Current Phone:"));
        formPanel.add(phoneField);
        formPanel.add(createLabel("Permanent Address:"));
        formPanel.add(permanentAddressField);
        formPanel.add(createLabel("Permanent Phone:"));
        formPanel.add(permanentPhoneField);
        formPanel.add(createLabel("Birthdate (MM-DD-YYYY):"));
        formPanel.add(birthdateField);
        formPanel.add(createLabel("Sex:"));
        formPanel.add(sexField);
        formPanel.add(createLabel("Age (optional):"));
        formPanel.add(ageField);
        formPanel.add(createLabel("Primary Doctor Name:"));
        formPanel.add(primaryDoctorField);
        formPanel.add(createLabel("Secondary Doctor Name:"));
        formPanel.add(secondaryDoctorField);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = createStyledButton("Add Patient", new Color(76, 175, 80));
        JButton updateBtn = createStyledButton("Update", new Color(33, 150, 243));
        JButton deleteBtn = createStyledButton("Delete", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Refresh", new Color(255, 152, 0));
        JButton clearBtn = createStyledButton("Clear", new Color(158, 158, 158));
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);
        String[] columns = {"ID", "SSN", "First Name", "Last Name", "Condition", "Current Address", "Current Phone", "Permanent Address", "Permanent Phone", "Birthdate", "Sex", "Age"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        addBtn.addActionListener(e -> {
            if (!validateFields(patientIdField, ssnField, firstNameField, lastNameField)) {
                JOptionPane.showMessageDialog(panel, "Please fill in required fields: Patient ID, SSN, First Name, Last Name");
                return;
            }
            String ageText = ageField.getText().trim();
            if (!ageText.isEmpty()) {
                try {
                    Integer.parseInt(ageText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Age must be a valid number or left empty", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            try {
                String birthdateText = birthdateField.getText().trim();
                String sql;
                if (birthdateText.isEmpty()) {
                    sql = "INSERT INTO PATIENT (PATIENT_ID, SSN, FIRST_NAME, LAST_NAME, PATIENT_CONDITION, " +
                          "CURRENT_ADDRESS, CURRENT_PHONE_NUMBER, PERMANENT_ADDRESS, PERMANENT_PHONE, " +
                          "BIRTHDATE, SEX, AGE) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
                } else {
                    sql = "INSERT INTO PATIENT (PATIENT_ID, SSN, FIRST_NAME, LAST_NAME, PATIENT_CONDITION, " +
                          "CURRENT_ADDRESS, CURRENT_PHONE_NUMBER, PERMANENT_ADDRESS, PERMANENT_PHONE, " +
                          "BIRTHDATE, SEX, AGE) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?, 'MM-DD-YYYY'), ?, ?)";
                }
                PreparedStatement pstmt = connection.prepareStatement(sql);
                String patientId = patientIdField.getText().trim();
                pstmt.setString(1, patientId);
                pstmt.setString(2, ssnField.getText().trim());
                pstmt.setString(3, firstNameField.getText().trim());
                pstmt.setString(4, lastNameField.getText().trim());
                pstmt.setString(5, nullIfEmpty(conditionField.getText()));
                pstmt.setString(6, nullIfEmpty(addressField.getText()));
                pstmt.setString(7, nullIfEmpty(phoneField.getText()));
                pstmt.setString(8, nullIfEmpty(permanentAddressField.getText()));
                pstmt.setString(9, nullIfEmpty(permanentPhoneField.getText()));
                int paramIndex = 10;
                if (!birthdateText.isEmpty()) {
                    pstmt.setString(paramIndex++, birthdateText);
                }
                pstmt.setString(paramIndex++, sexField.getText().trim());
                if (ageText.isEmpty()) {
                    pstmt.setNull(paramIndex, Types.INTEGER);
                } else {
                    pstmt.setInt(paramIndex, Integer.parseInt(ageText));
                }
                pstmt.executeUpdate();
                if (primaryDoctorField.getText() != null && !primaryDoctorField.getText().trim().isEmpty()) {
                    String doctorName = primaryDoctorField.getText().trim();
                    String lookupSql = "SELECT DOCTOR_ID FROM DOCTOR WHERE FIRST_NAME || ' ' || LAST_NAME = ?";
                    PreparedStatement lookupStmt = connection.prepareStatement(lookupSql);
                    lookupStmt.setString(1, doctorName);
                    ResultSet rs = lookupStmt.executeQuery();
                    if (rs.next()) {
                        String doctorId = rs.getString("DOCTOR_ID");
                        String primarySql = "INSERT INTO PRIMARY_CARE (DOCTOR_ID, PATIENT_ID) VALUES (?, ?)";
                        PreparedStatement primaryStmt = connection.prepareStatement(primarySql);
                        primaryStmt.setString(1, doctorId);
                        primaryStmt.setString(2, patientId);
                        primaryStmt.executeUpdate();
                    }
                }
                if (secondaryDoctorField.getText() != null && !secondaryDoctorField.getText().trim().isEmpty()) {
                    String doctorName = secondaryDoctorField.getText().trim();
                    String lookupSql = "SELECT DOCTOR_ID FROM DOCTOR WHERE FIRST_NAME || ' ' || LAST_NAME = ?";
                    PreparedStatement lookupStmt = connection.prepareStatement(lookupSql);
                    lookupStmt.setString(1, doctorName);
                    ResultSet rs = lookupStmt.executeQuery();
                    if (rs.next()) {
                        String doctorId = rs.getString("DOCTOR_ID");
                        String secondarySql = "INSERT INTO SECONDARY_CARE (DOCTOR_ID, PATIENT_ID) VALUES (?, ?)";
                        PreparedStatement secondaryStmt = connection.prepareStatement(secondarySql);
                        secondaryStmt.setString(1, doctorId);
                        secondaryStmt.setString(2, patientId);
                        secondaryStmt.executeUpdate();
                    }
                }
                JOptionPane.showMessageDialog(panel, "Patient added successfully!");
                loadPatients(tableModel);
                clearAllFields(patientIdField, ssnField, firstNameField, lastNameField, conditionField, 
                              addressField, phoneField, permanentAddressField, permanentPhoneField, 
                              birthdateField, ageField, primaryDoctorField, secondaryDoctorField);
            } catch (SQLException ex) {
                showError(panel, "Failed to add patient", ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Patient ID and Age must be valid numbers", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a patient to update");
                return;
            }
            String patientId = (String) table.getValueAt(row, 0);
            try {
                String sql = "UPDATE PATIENT SET SSN=?, FIRST_NAME=?, LAST_NAME=?, " +
                            "PATIENT_CONDITION=?, CURRENT_ADDRESS=?, CURRENT_PHONE_NUMBER=?, " +
                            "PERMANENT_ADDRESS=?, PERMANENT_PHONE=?, " +
                            "BIRTHDATE=TO_DATE(?, 'MM-DD-YYYY'), SEX=?, AGE=? WHERE PATIENT_ID=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, ssnField.getText().trim());
                pstmt.setString(2, firstNameField.getText().trim());
                pstmt.setString(3, lastNameField.getText().trim());
                pstmt.setString(4, nullIfEmpty(conditionField.getText()));
                pstmt.setString(5, nullIfEmpty(addressField.getText()));
                pstmt.setString(6, nullIfEmpty(phoneField.getText()));
                pstmt.setString(7, nullIfEmpty(permanentAddressField.getText()));
                pstmt.setString(8, nullIfEmpty(permanentPhoneField.getText()));
                pstmt.setString(9, birthdateField.getText().trim());
                pstmt.setString(10, sexField.getText().trim());
                String ageText = ageField.getText().trim();
                if (ageText.isEmpty()) {
                    pstmt.setNull(11, Types.INTEGER);
                } else {
                    pstmt.setInt(11, Integer.parseInt(ageText));
                }
                pstmt.setString(12, patientId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Patient updated successfully!");
                loadPatients(tableModel);
            } catch (SQLException ex) {
                showError(panel, "Failed to update patient", ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Age must be a valid number", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a patient to delete");
                return;
            }
            String patientId = (String) table.getValueAt(row, 0);
            String patientName = table.getValueAt(row, 2) + " " + table.getValueAt(row, 3);
            int confirm = JOptionPane.showConfirmDialog(panel, 
                "Delete patient: " + patientName + "?\n\nThis will also delete all related records" +
                " (prescriptions, procedures, etc.)", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM PATIENT WHERE PATIENT_ID = ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, patientId);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(panel, "Patient deleted successfully!");
                    loadPatients(tableModel);
                    clearAllFields(patientIdField, ssnField, firstNameField, lastNameField, conditionField, 
                                  addressField, phoneField, permanentAddressField, permanentPhoneField, 
                                  birthdateField, ageField, primaryDoctorField, secondaryDoctorField);
                } catch (SQLException ex) {
                    showError(panel, "Failed to delete patient", ex);
                }
            }
        });
        refreshBtn.addActionListener(e -> loadPatients(tableModel));
        clearBtn.addActionListener(e -> {
            clearAllFields(patientIdField, ssnField, firstNameField, lastNameField, conditionField, 
                          addressField, phoneField, permanentAddressField, permanentPhoneField, 
                          birthdateField, ageField, primaryDoctorField, secondaryDoctorField);
            table.clearSelection();
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    patientIdField.setText(getString(table, row, 0));
                    ssnField.setText(getString(table, row, 1));
                    firstNameField.setText(getString(table, row, 2));
                    lastNameField.setText(getString(table, row, 3));
                    conditionField.setText(getString(table, row, 4));
                    addressField.setText(getString(table, row, 5));
                    phoneField.setText(getString(table, row, 6));
                    permanentAddressField.setText(getString(table, row, 7));
                    permanentAddressField.setText(getString(table, row, 7));
                    permanentPhoneField.setText(getString(table, row, 8));
                    birthdateField.setText(getString(table, row, 9));
                    sexField.setText(getString(table, row, 10));
                    ageField.setText(getString(table, row, 11));
                }
            }
        });
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        JLabel infoLabel = new JLabel("Select a row to edit | Double-click to view details");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        panel.add(infoLabel, BorderLayout.SOUTH);
        loadPatients(tableModel);
        return panel;
    }
    private void loadPatients(DefaultTableModel model) {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try {
            String sql = "SELECT PATIENT_ID, SSN, FIRST_NAME, LAST_NAME, PATIENT_CONDITION, " +
                        "CURRENT_ADDRESS, CURRENT_PHONE_NUMBER, PERMANENT_ADDRESS, PERMANENT_PHONE, " +
                        "BIRTHDATE, SEX, AGE FROM PATIENT ORDER BY PATIENT_ID";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Date birthdate = rs.getDate("BIRTHDATE");
                String birthdateStr = (birthdate != null) ? sdf.format(birthdate) : "";
                model.addRow(new Object[]{
                    rs.getString("PATIENT_ID"),
                    rs.getString("SSN"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("LAST_NAME"),
                    rs.getString("PATIENT_CONDITION"),
                    rs.getString("CURRENT_ADDRESS"),
                    rs.getString("CURRENT_PHONE_NUMBER"),
                    rs.getString("PERMANENT_ADDRESS"),
                    rs.getString("PERMANENT_PHONE"),
                    birthdateStr,
                    rs.getString("SEX"),
                    rs.getObject("AGE")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private JPanel createDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel formPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Doctor Information", 
            0, 0, new Font("Arial", Font.BOLD, 14)));
        JTextField doctorIdField = new JTextField();
        JTextField ssnField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField birthdateField = new JTextField("01-01-1975");
        JComboBox<String> deptCombo = new JComboBox<>();
        formPanel.add(createLabel("Doctor ID:"));
        formPanel.add(doctorIdField);
        formPanel.add(createLabel("SSN (###-##-####):"));
        formPanel.add(ssnField);
        formPanel.add(createLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(createLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(createLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(createLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(createLabel("Contact Number:"));
        formPanel.add(contactField);
        formPanel.add(createLabel("Birthdate (MM-DD-YYYY):"));
        formPanel.add(birthdateField);
        formPanel.add(createLabel("Department:"));
        formPanel.add(deptCombo);
        formPanel.add(new JLabel()); 
        formPanel.add(new JLabel()); 
        loadDepartmentsCombo(deptCombo);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = createStyledButton("Add Doctor", new Color(76, 175, 80));
        JButton updateBtn = createStyledButton("Update", new Color(33, 150, 243));
        JButton deleteBtn = createStyledButton("Delete", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Refresh", new Color(255, 152, 0));
        JButton clearBtn = createStyledButton("Clear", new Color(158, 158, 158));
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);
        String[] columns = {"ID", "SSN", "First Name", "Last Name", "Address", "Phone", "Contact", "Birthdate", "Department"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        addBtn.addActionListener(e -> {
            if (!validateFields(doctorIdField, ssnField, firstNameField, lastNameField)) {
                JOptionPane.showMessageDialog(panel, "Please fill in required fields");
                return;
            }
            try {
                Integer deptCode = null;
                if (deptCombo.getSelectedItem() != null) {
                    String deptItem = (String) deptCombo.getSelectedItem();
                    deptCode = Integer.parseInt(deptItem.split(":")[0].trim());
                }
                String sql = "INSERT INTO DOCTOR (DOCTOR_ID, SSN, FIRST_NAME, LAST_NAME, ADDRESS, " +
                            "PHONE_NUMBER, CONTACT_NUMBER, BIRTHDATE, DEPARTMENT_CODE) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, TO_DATE(?, 'MM-DD-YYYY'), ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, doctorIdField.getText().trim());
                pstmt.setString(2, ssnField.getText().trim());
                pstmt.setString(3, firstNameField.getText().trim());
                pstmt.setString(4, lastNameField.getText().trim());
                pstmt.setString(5, nullIfEmpty(addressField.getText()));
                pstmt.setString(6, nullIfEmpty(phoneField.getText()));
                pstmt.setString(7, nullIfEmpty(contactField.getText()));
                pstmt.setString(8, birthdateField.getText().trim());
                if (deptCode != null) {
                    pstmt.setInt(9, deptCode);
                } else {
                    pstmt.setNull(9, Types.INTEGER);
                }
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Doctor added successfully!");
                loadDoctors(tableModel);
                clearAllFields(doctorIdField, ssnField, firstNameField, lastNameField, addressField, phoneField, contactField, birthdateField);
            } catch (SQLException ex) {
                showError(panel, "Failed to add doctor", ex);
            }
        });
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a doctor to update");
                return;
            }
            String doctorId = table.getValueAt(row, 0).toString();
            try {
                Integer deptCode = null;
                if (deptCombo.getSelectedItem() != null) {
                    String deptItem = (String) deptCombo.getSelectedItem();
                    deptCode = Integer.parseInt(deptItem.split(":")[0].trim());
                }
                String sql = "UPDATE DOCTOR SET SSN=?, FIRST_NAME=?, LAST_NAME=?, ADDRESS=?, " +
                            "PHONE_NUMBER=?, CONTACT_NUMBER=?, BIRTHDATE=TO_DATE(?, 'MM-DD-YYYY'), DEPARTMENT_CODE=? " +
                            "WHERE DOCTOR_ID=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, ssnField.getText().trim());
                pstmt.setString(2, firstNameField.getText().trim());
                pstmt.setString(3, lastNameField.getText().trim());
                pstmt.setString(4, nullIfEmpty(addressField.getText()));
                pstmt.setString(5, nullIfEmpty(phoneField.getText()));
                pstmt.setString(6, nullIfEmpty(contactField.getText()));
                pstmt.setString(7, birthdateField.getText().trim());
                if (deptCode != null) {
                    pstmt.setInt(8, deptCode);
                } else {
                    pstmt.setNull(8, Types.INTEGER);
                }
                pstmt.setString(9, doctorId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Doctor updated successfully!");
                loadDoctors(tableModel);
            } catch (SQLException ex) {
                showError(panel, "Failed to update doctor", ex);
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a doctor to delete");
                return;
            }
            String doctorId = table.getValueAt(row, 0).toString();
            String doctorName = table.getValueAt(row, 3) + " " + table.getValueAt(row, 4);
            int confirm = JOptionPane.showConfirmDialog(panel, 
                "Delete doctor: " + doctorName + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM DOCTOR WHERE DOCTOR_ID = ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, doctorId);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(panel, "Doctor deleted successfully!");
                    loadDoctors(tableModel);
                } catch (SQLException ex) {
                    showError(panel, "Failed to delete doctor", ex);
                }
            }
        });
        refreshBtn.addActionListener(e -> {
            loadDoctors(tableModel);
            deptCombo.removeAllItems();
            loadDepartmentsCombo(deptCombo);
        });
        clearBtn.addActionListener(e -> {
            clearAllFields(doctorIdField, ssnField, firstNameField, lastNameField, addressField, phoneField, contactField, birthdateField);
            table.clearSelection();
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    doctorIdField.setText(getString(table, row, 0));
                    ssnField.setText(getString(table, row, 1));
                    firstNameField.setText(getString(table, row, 2));
                    lastNameField.setText(getString(table, row, 3));
                    addressField.setText(getString(table, row, 4));
                    phoneField.setText(getString(table, row, 5));
                    contactField.setText(getString(table, row, 6));
                    birthdateField.setText(getString(table, row, 7));
                    String dept = getString(table, row, 8);
                    deptCombo.setSelectedItem(dept.isEmpty() ? null : dept);
                }
            }
        });
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadDoctors(tableModel);
        return panel;
    }
    private void loadDoctors(DefaultTableModel model) {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try {
            String sql = "SELECT d.DOCTOR_ID, d.SSN, d.FIRST_NAME, d.LAST_NAME, " +
                        "d.ADDRESS, d.PHONE_NUMBER, d.CONTACT_NUMBER, d.BIRTHDATE, dep.DEPARTMENT_NAME " +
                        "FROM DOCTOR d " +
                        "LEFT JOIN DEPARTMENT dep ON d.DEPARTMENT_CODE = dep.DEPARTMENT_CODE " +
                        "ORDER BY d.DOCTOR_ID";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Date birthdate = rs.getDate("BIRTHDATE");
                String birthdateStr = (birthdate != null) ? sdf.format(birthdate) : "";
                model.addRow(new Object[]{
                    rs.getString("DOCTOR_ID"),
                    rs.getString("SSN"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("LAST_NAME"),
                    rs.getString("ADDRESS"),
                    rs.getString("PHONE_NUMBER"),
                    rs.getString("CONTACT_NUMBER"),
                    birthdateStr,
                    rs.getString("DEPARTMENT_NAME")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private JPanel createDepartmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Department Information", 
            0, 0, new Font("Arial", Font.BOLD, 14)));
        JTextField codeField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField officeField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField headField = new JTextField();
        formPanel.add(createLabel("Department Code:"));
        formPanel.add(codeField);
        formPanel.add(createLabel("Department Name:"));
        formPanel.add(nameField);
        formPanel.add(createLabel("Office Number:"));
        formPanel.add(officeField);
        formPanel.add(createLabel("Office Phone:"));
        formPanel.add(phoneField);
        formPanel.add(createLabel("Department Head:"));
        formPanel.add(headField);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = createStyledButton("Add Department", new Color(76, 175, 80));
        JButton updateBtn = createStyledButton("Update", new Color(33, 150, 243));
        JButton deleteBtn = createStyledButton("Delete", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Refresh", new Color(255, 152, 0));
        JButton clearBtn = createStyledButton("Clear", new Color(158, 158, 158));
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);
        String[] columns = {"Code", "Name", "Office", "Phone", "Head"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        addBtn.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Department name is required");
                return;
            }
            try {
                String sql = "INSERT INTO DEPARTMENT (DEPARTMENT_CODE, DEPARTMENT_NAME, OFFICE_NUMBER, " +
                            "OFFICE_PHONE, DEPARTMENT_HEAD) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, codeField.getText().trim());
                pstmt.setString(2, nameField.getText().trim());
                pstmt.setString(3, nullIfEmpty(officeField.getText()));
                pstmt.setString(4, nullIfEmpty(phoneField.getText()));
                pstmt.setString(5, nullIfEmpty(headField.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Department added successfully!");
                loadDepartments(tableModel);
                clearAllFields(codeField, nameField, officeField, phoneField, headField);
            } catch (SQLException ex) {
                showError(panel, "Failed to add department", ex);
            }
        });
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a department to update");
                return;
            }
            String deptCode = (String) table.getValueAt(row, 0);
            try {
                String sql = "UPDATE DEPARTMENT SET DEPARTMENT_NAME=?, OFFICE_NUMBER=?, " +
                            "OFFICE_PHONE=?, DEPARTMENT_HEAD=? WHERE DEPARTMENT_CODE=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, nullIfEmpty(officeField.getText()));
                pstmt.setString(3, nullIfEmpty(phoneField.getText()));
                pstmt.setString(4, nullIfEmpty(headField.getText()));
                pstmt.setString(5, deptCode);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Department updated successfully!");
                loadDepartments(tableModel);
            } catch (SQLException ex) {
                showError(panel, "Failed to update department", ex);
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a department to delete");
                return;
            }
            String deptCode = (String) table.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(panel, 
                "Delete this department?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM DEPARTMENT WHERE DEPARTMENT_CODE = ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, deptCode);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(panel, "Department deleted successfully!");
                    loadDepartments(tableModel);
                } catch (SQLException ex) {
                    showError(panel, "Failed to delete department", ex);
                }
            }
        });
        refreshBtn.addActionListener(e -> loadDepartments(tableModel));
        clearBtn.addActionListener(e -> {
            clearAllFields(codeField, nameField, officeField, phoneField, headField);
            table.clearSelection();
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    codeField.setText(getString(table, row, 0));
                    nameField.setText(getString(table, row, 1));
                    officeField.setText(getString(table, row, 2));
                    phoneField.setText(getString(table, row, 3));
                    headField.setText(getString(table, row, 4));
                }
            }
        });
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadDepartments(tableModel);
        return panel;
    }
    private void loadDepartments(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            String sql = "SELECT * FROM DEPARTMENT ORDER BY DEPARTMENT_CODE";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("DEPARTMENT_CODE"),
                    rs.getString("DEPARTMENT_NAME"),
                    rs.getString("OFFICE_NUMBER"),
                    rs.getString("OFFICE_PHONE"),
                    rs.getString("DEPARTMENT_HEAD")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private JPanel createMedicationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Medication Information", 
            0, 0, new Font("Arial", Font.BOLD, 14)));
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField mfgField = new JTextField();
        formPanel.add(createLabel("Medication Name:"));
        formPanel.add(nameField);
        formPanel.add(createLabel("Description:"));
        formPanel.add(descField);
        formPanel.add(createLabel("Manufacturer:"));
        formPanel.add(mfgField);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = createStyledButton("Add Medication", new Color(76, 175, 80));
        JButton updateBtn = createStyledButton("Update", new Color(33, 150, 243));
        JButton deleteBtn = createStyledButton("Delete", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Refresh", new Color(255, 152, 0));
        JButton clearBtn = createStyledButton("Clear", new Color(158, 158, 158));
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);
        String[] columns = {"Name", "Description", "Manufacturer"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        addBtn.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Medication name is required");
                return;
            }
            try {
                String sql = "INSERT INTO MEDICATION VALUES (?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, nullIfEmpty(descField.getText()));
                pstmt.setString(3, nullIfEmpty(mfgField.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Medication added successfully!");
                loadMedications(tableModel);
                clearAllFields(nameField, descField, mfgField);
            } catch (SQLException ex) {
                showError(panel, "Failed to add medication", ex);
            }
        });
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a medication to update");
                return;
            }
            String oldName = getString(table, row, 0);
            try {
                String sql = "UPDATE MEDICATION SET MEDICATION_NAME=?, DESCRIPTION=?, MANUFACTURER=? " +
                            "WHERE MEDICATION_NAME=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, nullIfEmpty(descField.getText()));
                pstmt.setString(3, nullIfEmpty(mfgField.getText()));
                pstmt.setString(4, oldName);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Medication updated successfully!");
                loadMedications(tableModel);
            } catch (SQLException ex) {
                showError(panel, "Failed to update medication", ex);
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a medication to delete");
                return;
            }
            String medName = getString(table, row, 0);
            int confirm = JOptionPane.showConfirmDialog(panel, 
                "Delete medication: " + medName + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM MEDICATION WHERE MEDICATION_NAME = ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, medName);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(panel, "Medication deleted successfully!");
                    loadMedications(tableModel);
                } catch (SQLException ex) {
                    showError(panel, "Failed to delete medication", ex);
                }
            }
        });
        refreshBtn.addActionListener(e -> loadMedications(tableModel));
        clearBtn.addActionListener(e -> clearAllFields(nameField, descField, mfgField));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    nameField.setText(getString(table, row, 0));
                    descField.setText(getString(table, row, 1));
                    mfgField.setText(getString(table, row, 2));
                }
            }
        });
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadMedications(tableModel);
        return panel;
    }
    private void loadMedications(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            String sql = "SELECT * FROM MEDICATION ORDER BY MEDICATION_NAME";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("MEDICATION_NAME"),
                    rs.getString("DESCRIPTION"),
                    rs.getString("MANUFACTURER")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private JPanel createPrescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Prescription Information", 
            0, 0, new Font("Arial", Font.BOLD, 14)));
        JComboBox<String> patientCombo = new JComboBox<>();
        JComboBox<String> doctorCombo = new JComboBox<>();
        JComboBox<String> medicationCombo = new JComboBox<>();
        formPanel.add(createLabel("Patient:"));
        formPanel.add(patientCombo);
        formPanel.add(createLabel("Doctor:"));
        formPanel.add(doctorCombo);
        formPanel.add(createLabel("Medication:"));
        formPanel.add(medicationCombo);
        loadPatientsCombo(patientCombo);
        loadDoctorsCombo(doctorCombo);
        loadMedicationsCombo(medicationCombo);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = createStyledButton("Add Prescription", new Color(76, 175, 80));
        JButton deleteBtn = createStyledButton("Delete", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Refresh", new Color(255, 152, 0));
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        String[] columns = {"Patient ID", "Doctor ID", "Medication", "Patient", "Doctor", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        addBtn.addActionListener(e -> {
            try {
                String patientItem = (String) patientCombo.getSelectedItem();
                String doctorItem = (String) doctorCombo.getSelectedItem();
                String medicationItem = (String) medicationCombo.getSelectedItem();
                if (patientItem == null || doctorItem == null || medicationItem == null) {
                    JOptionPane.showMessageDialog(panel, "Please select all fields");
                    return;
                }
                String patientId = patientItem.split(":")[0].trim();
                String doctorId = doctorItem.split(":")[0].trim();
                String medName = medicationItem;
                String sql = "INSERT INTO PRESCRIPTION (PATIENT_ID, DOCTOR_ID, MEDICATION_NAME) VALUES (?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, patientId);
                pstmt.setString(2, doctorId);
                pstmt.setString(3, medName);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Prescription added successfully!");
                loadPrescriptions(tableModel);
            } catch (SQLException ex) {
                showError(panel, "Failed to add prescription", ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid patient or doctor ID format", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a prescription to delete");
                return;
            }
            String patientId = getString(table, row, 0);
            String doctorId = getString(table, row, 1);
            String medName = getString(table, row, 2);
            int confirm = JOptionPane.showConfirmDialog(panel, 
                "Delete this prescription?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM PRESCRIPTION WHERE PATIENT_ID = ? AND DOCTOR_ID = ? AND MEDICATION_NAME = ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, patientId);
                    pstmt.setString(2, doctorId);
                    pstmt.setString(3, medName);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(panel, "Prescription deleted successfully!");
                    loadPrescriptions(tableModel);
                } catch (SQLException ex) {
                    showError(panel, "Failed to delete prescription", ex);
                }
            }
        });
        refreshBtn.addActionListener(e -> {
            loadPrescriptions(tableModel);
            patientCombo.removeAllItems();
            doctorCombo.removeAllItems();
            medicationCombo.removeAllItems();
            loadPatientsCombo(patientCombo);
            loadDoctorsCombo(doctorCombo);
            loadMedicationsCombo(medicationCombo);
        });
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadPrescriptions(tableModel);
        return panel;
    }
    private void loadPrescriptions(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            String sql = "SELECT pr.PATIENT_ID, pr.DOCTOR_ID, pr.MEDICATION_NAME, " +
                        "p.FIRST_NAME || ' ' || p.LAST_NAME AS PATIENT_NAME, " +
                        "d.FIRST_NAME || ' ' || d.LAST_NAME AS DOCTOR_NAME, " +
                        "pr.PRESCRIPTION_DATE " +
                        "FROM PRESCRIPTION pr " +
                        "JOIN PATIENT p ON pr.PATIENT_ID = p.PATIENT_ID " +
                        "JOIN DOCTOR d ON pr.DOCTOR_ID = d.DOCTOR_ID " +
                        "ORDER BY pr.PRESCRIPTION_DATE DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("PATIENT_ID"),
                    rs.getString("DOCTOR_ID"),
                    rs.getString("MEDICATION_NAME"),
                    rs.getString("PATIENT_NAME"),
                    rs.getString("DOCTOR_NAME"),
                    rs.getDate("PRESCRIPTION_DATE")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private JPanel createProcedurePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Procedure Type Information", 
            0, 0, new Font("Arial", Font.BOLD, 14)));
        JTextField procNumField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField durationField = new JTextField();
        JComboBox<String> deptCombo = new JComboBox<>();
        formPanel.add(createLabel("Procedure Number:"));
        formPanel.add(procNumField);
        formPanel.add(createLabel("Procedure Name:"));
        formPanel.add(nameField);
        formPanel.add(createLabel("Description:"));
        formPanel.add(descField);
        formPanel.add(createLabel("Duration (minutes):"));
        formPanel.add(durationField);
        formPanel.add(createLabel("Offering Department:"));
        formPanel.add(deptCombo);
        loadDepartmentsCombo(deptCombo);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = createStyledButton("Add Procedure", new Color(76, 175, 80));
        JButton updateBtn = createStyledButton("Update", new Color(33, 150, 243));
        JButton deleteBtn = createStyledButton("Delete", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Refresh", new Color(255, 152, 0));
        JButton clearBtn = createStyledButton("Clear", new Color(158, 158, 158));
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);
        String[] columns = {"Number", "Name", "Description", "Duration (min)", "Department"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        addBtn.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty() || procNumField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Procedure number and name are required");
                return;
            }
            try {
                String deptCode = null;
                if (deptCombo.getSelectedItem() != null) {
                    String deptItem = (String) deptCombo.getSelectedItem();
                    deptCode = deptItem.split(":")[0].trim();
                }
                String sql = "INSERT INTO PROCEDURE_TYPE (PROCEDURE_NUMBER, PROCEDURE_NAME, DESCRIPTION, " +
                            "DURATION_MINUTES, OFFERING_DEPARTMENT_CODE) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, procNumField.getText().trim());
                pstmt.setString(2, nameField.getText().trim());
                pstmt.setString(3, nullIfEmpty(descField.getText()));
                String durationText = durationField.getText().trim();
                if (durationText.isEmpty()) {
                    pstmt.setNull(4, Types.INTEGER);
                } else {
                    pstmt.setInt(4, Integer.parseInt(durationText));
                }
                if (deptCode != null) {
                    pstmt.setString(5, deptCode);
                } else {
                    pstmt.setNull(5, Types.VARCHAR);
                }
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Procedure added successfully!");
                loadProcedures(tableModel);
                clearAllFields(procNumField, nameField, descField, durationField);
            } catch (SQLException ex) {
                showError(panel, "Failed to add procedure", ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Duration must be a valid number");
            }
        });
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a procedure to update");
                return;
            }
            String procNum = getString(table, row, 0);
            try {
                String deptCode = null;
                if (deptCombo.getSelectedItem() != null) {
                    String deptItem = (String) deptCombo.getSelectedItem();
                    deptCode = deptItem.split(":")[0].trim();
                }
                String sql = "UPDATE PROCEDURE_TYPE SET PROCEDURE_NAME=?, DESCRIPTION=?, DURATION_MINUTES=?, " +
                            "OFFERING_DEPARTMENT_CODE=? WHERE PROCEDURE_NUMBER=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, nullIfEmpty(descField.getText()));
                String durationText = durationField.getText().trim();
                if (durationText.isEmpty()) {
                    pstmt.setNull(3, Types.INTEGER);
                } else {
                    pstmt.setInt(3, Integer.parseInt(durationText));
                }
                if (deptCode != null) {
                    pstmt.setString(4, deptCode);
                } else {
                    pstmt.setNull(4, Types.VARCHAR);
                }
                pstmt.setString(5, procNum);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Procedure updated successfully!");
                loadProcedures(tableModel);
            } catch (SQLException ex) {
                showError(panel, "Failed to update procedure", ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Duration must be a valid number");
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a procedure to delete");
                return;
            }
            String procNum = getString(table, row, 0);
            int confirm = JOptionPane.showConfirmDialog(panel, 
                "Delete this procedure?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM PROCEDURE_TYPE WHERE PROCEDURE_NUMBER = ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, procNum);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(panel, "Procedure deleted successfully!");
                    loadProcedures(tableModel);
                } catch (SQLException ex) {
                    showError(panel, "Failed to delete procedure", ex);
                }
            }
        });
        refreshBtn.addActionListener(e -> {
            loadProcedures(tableModel);
            deptCombo.removeAllItems();
            loadDepartmentsCombo(deptCombo);
        });
        clearBtn.addActionListener(e -> clearAllFields(nameField, descField, durationField));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    procNumField.setText(getString(table, row, 0));
                    nameField.setText(getString(table, row, 1));
                    descField.setText(getString(table, row, 2));
                    durationField.setText(getString(table, row, 3));
                    String dept = getString(table, row, 4);
                    deptCombo.setSelectedItem(dept.isEmpty() ? null : dept);
                }
            }
        });
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadProcedures(tableModel);
        return panel;
    }
    private void loadProcedures(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            String sql = "SELECT pt.PROCEDURE_NUMBER, pt.PROCEDURE_NAME, pt.DESCRIPTION, " +
                        "pt.DURATION_MINUTES, d.DEPARTMENT_NAME " +
                        "FROM PROCEDURE_TYPE pt " +
                        "LEFT JOIN DEPARTMENT d ON pt.OFFERING_DEPARTMENT_CODE = d.DEPARTMENT_CODE " +
                        "ORDER BY pt.PROCEDURE_NUMBER";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("PROCEDURE_NUMBER"),
                    rs.getString("PROCEDURE_NAME"),
                    rs.getString("DESCRIPTION"),
                    rs.getObject("DURATION_MINUTES"),
                    rs.getString("DEPARTMENT_NAME")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private JPanel createUndergoesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Performed Procedure Information", 
            0, 0, new Font("Arial", Font.BOLD, 14)));
        JComboBox<String> patientCombo = new JComboBox<>();
        JComboBox<String> procedureCombo = new JComboBox<>();
        JComboBox<String> doctorCombo = new JComboBox<>();
        JTextField dateField = new JTextField("01-01-2025");
        JTextField timeField = new JTextField("10:00");
        JTextField notesField = new JTextField();
        formPanel.add(createLabel("Patient:"));
        formPanel.add(patientCombo);
        formPanel.add(createLabel("Procedure:"));
        formPanel.add(procedureCombo);
        formPanel.add(createLabel("Doctor:"));
        formPanel.add(doctorCombo);
        formPanel.add(createLabel("Date (MM-DD-YYYY):"));
        formPanel.add(dateField);
        formPanel.add(createLabel("Time (HH:MM):"));
        formPanel.add(timeField);
        formPanel.add(createLabel("Notes:"));
        formPanel.add(notesField);
        loadPatientsCombo(patientCombo);
        loadProceduresCombo(procedureCombo);
        loadDoctorsCombo(doctorCombo);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = createStyledButton("Add", new Color(76, 175, 80));
        JButton deleteBtn = createStyledButton("Delete", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Refresh", new Color(255, 152, 0));
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        String[] columns = {"Patient ID", "Procedure Number", "Doctor ID", "Date", "Notes", "Time", "Patient", "Procedure", "Doctor"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        addBtn.addActionListener(e -> {
            try {
                String patientItem = (String) patientCombo.getSelectedItem();
                String procedureItem = (String) procedureCombo.getSelectedItem();
                String doctorItem = (String) doctorCombo.getSelectedItem();
                if (patientItem == null || procedureItem == null || doctorItem == null) {
                    JOptionPane.showMessageDialog(panel, "Please select all fields");
                    return;
                }
                String patientId = patientItem.split(":")[0].trim();
                String procNum = procedureItem.split(":")[0].trim();
                String doctorId = doctorItem.split(":")[0].trim();
                String date = dateField.getText().trim();
                String time = timeField.getText().trim();
                String sql = "INSERT INTO UNDERGOES (PATIENT_ID, PROCEDURE_NUMBER, DOCTOR_ID, PROCEDURE_DATE, PROCEDURE_TIME, NOTES) " +
                            "VALUES (?, ?, ?, TO_DATE(?, 'MM-DD-YYYY'), ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, patientId);
                pstmt.setString(2, procNum);
                pstmt.setString(3, doctorId);
                pstmt.setString(4, date);
                pstmt.setString(5, time);
                pstmt.setString(6, nullIfEmpty(notesField.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Performed procedure added successfully!");
                loadUndergoes(tableModel);
            } catch (SQLException ex) {
                showError(panel, "Failed to add performed procedure", ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid ID format", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a record to delete");
                return;
            }
            String patientId = getString(table, row, 0);
            String procNum = getString(table, row, 1);
            String dateStr = getString(table, row, 3);
            int confirm = JOptionPane.showConfirmDialog(panel, 
                "Delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM UNDERGOES WHERE PATIENT_ID = ? AND PROCEDURE_NUMBER = ? AND PROCEDURE_DATE = TO_DATE(?, 'MM-DD-YYYY')";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, patientId);
                    pstmt.setString(2, procNum);
                    pstmt.setString(3, dateStr);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(panel, "Record deleted successfully!");
                    loadUndergoes(tableModel);
                } catch (SQLException ex) {
                    showError(panel, "Failed to delete record", ex);
                }
            }
        });
        refreshBtn.addActionListener(e -> {
            loadUndergoes(tableModel);
            patientCombo.removeAllItems();
            procedureCombo.removeAllItems();
            doctorCombo.removeAllItems();
            loadPatientsCombo(patientCombo);
            loadProceduresCombo(procedureCombo);
            loadDoctorsCombo(doctorCombo);
        });
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadUndergoes(tableModel);
        return panel;
    }
    private void loadUndergoes(DefaultTableModel model) {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try {
            String sql = "SELECT u.PATIENT_ID, u.PROCEDURE_NUMBER, u.DOCTOR_ID, u.PROCEDURE_DATE, u.NOTES, u.PROCEDURE_TIME, " +
                        "p.FIRST_NAME || ' ' || p.LAST_NAME AS PATIENT_NAME, " +
                        "pt.PROCEDURE_NAME, " +
                        "d.FIRST_NAME || ' ' || d.LAST_NAME AS DOCTOR_NAME " +
                        "FROM UNDERGOES u " +
                        "JOIN PATIENT p ON u.PATIENT_ID = p.PATIENT_ID " +
                        "JOIN PROCEDURE_TYPE pt ON u.PROCEDURE_NUMBER = pt.PROCEDURE_NUMBER " +
                        "JOIN DOCTOR d ON u.DOCTOR_ID = d.DOCTOR_ID " +
                        "ORDER BY u.PROCEDURE_DATE DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Date procedureDate = rs.getDate("PROCEDURE_DATE");
                String procedureDateStr = (procedureDate != null) ? sdf.format(procedureDate) : "";
                model.addRow(new Object[]{
                    rs.getString("PATIENT_ID"),
                    rs.getString("PROCEDURE_NUMBER"),
                    rs.getString("DOCTOR_ID"),
                    procedureDateStr,
                    rs.getString("NOTES"),
                    rs.getString("PROCEDURE_TIME"),
                    rs.getString("PATIENT_NAME"),
                    rs.getString("PROCEDURE_NAME"),
                    rs.getString("DOCTOR_NAME")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadProceduresCombo(JComboBox<String> combo) {
        try {
            String sql = "SELECT PROCEDURE_NUMBER, PROCEDURE_NAME FROM PROCEDURE_TYPE ORDER BY PROCEDURE_NAME";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                combo.addItem(rs.getString("PROCEDURE_NUMBER") + ": " + rs.getString("PROCEDURE_NAME"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private JPanel createPrimaryCarePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Primary Care Doctor Assignment", 
            0, 0, new Font("Arial", Font.BOLD, 14)));
        JComboBox<String> patientCombo = new JComboBox<>();
        JComboBox<String> doctorCombo = new JComboBox<>();
        formPanel.add(createLabel("Patient:"));
        formPanel.add(patientCombo);
        formPanel.add(createLabel("Doctor:"));
        formPanel.add(doctorCombo);
        loadPatientsCombo(patientCombo);
        loadDoctorsCombo(doctorCombo);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = createStyledButton("➕ Assign", new Color(76, 175, 80));
        JButton deleteBtn = createStyledButton("🗑️ Remove", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(255, 152, 0));
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        String[] columns = {"Doctor ID", "Patient ID", "Doctor", "Patient"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        addBtn.addActionListener(e -> {
            try {
                String patientItem = (String) patientCombo.getSelectedItem();
                String doctorItem = (String) doctorCombo.getSelectedItem();
                if (patientItem == null || doctorItem == null) {
                    JOptionPane.showMessageDialog(panel, "Please select all fields");
                    return;
                }
                int patientId = Integer.parseInt(patientItem.split(":")[0].trim());
                String doctorId = doctorItem.split(":")[0].trim();
                String sql = "INSERT INTO PRIMARY_CARE (DOCTOR_ID, PATIENT_ID) VALUES (?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, doctorId);
                pstmt.setInt(2, patientId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Primary care doctor assigned successfully!");
                loadPrimaryCare(tableModel);
            } catch (SQLException ex) {
                showError(panel, "Failed to assign primary care doctor", ex);
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a record to remove");
                return;
            }
            int patientId = (int) table.getValueAt(row, 1);
            String doctorId = table.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(panel, 
                "Remove this assignment?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM PRIMARY_CARE WHERE DOCTOR_ID = ? AND PATIENT_ID = ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, doctorId);
                    pstmt.setInt(2, patientId);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(panel, "✓ Assignment removed successfully!");
                    loadPrimaryCare(tableModel);
                } catch (SQLException ex) {
                    showError(panel, "Failed to remove assignment", ex);
                }
            }
        });
        refreshBtn.addActionListener(e -> {
            loadPrimaryCare(tableModel);
            patientCombo.removeAllItems();
            doctorCombo.removeAllItems();
            loadPatientsCombo(patientCombo);
            loadDoctorsCombo(doctorCombo);
        });
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadPrimaryCare(tableModel);
        return panel;
    }
    private void loadPrimaryCare(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            String sql = "SELECT pc.DOCTOR_ID, pc.PATIENT_ID, " +
                        "d.FIRST_NAME || ' ' || d.LAST_NAME AS DOCTOR_NAME, " +
                        "p.FIRST_NAME || ' ' || p.LAST_NAME AS PATIENT_NAME " +
                        "FROM PRIMARY_CARE pc " +
                        "JOIN PATIENT p ON pc.PATIENT_ID = p.PATIENT_ID " +
                        "JOIN DOCTOR d ON pc.DOCTOR_ID = d.DOCTOR_ID " +
                        "ORDER BY p.LAST_NAME";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("DOCTOR_ID"),
                    rs.getString("PATIENT_ID"),
                    rs.getString("DOCTOR_NAME"),
                    rs.getString("PATIENT_NAME")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private JPanel createSecondaryCarePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Secondary Care Doctor Assignment", 
            0, 0, new Font("Arial", Font.BOLD, 14)));
        JComboBox<String> patientCombo = new JComboBox<>();
        JComboBox<String> doctorCombo = new JComboBox<>();
        formPanel.add(createLabel("Patient:"));
        formPanel.add(patientCombo);
        formPanel.add(createLabel("Doctor:"));
        formPanel.add(doctorCombo);
        loadPatientsCombo(patientCombo);
        loadDoctorsCombo(doctorCombo);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = createStyledButton("➕ Assign", new Color(76, 175, 80));
        JButton deleteBtn = createStyledButton("🗑️ Remove", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(255, 152, 0));
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        String[] columns = {"Doctor ID", "Patient ID", "Doctor", "Patient"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        addBtn.addActionListener(e -> {
            try {
                String patientItem = (String) patientCombo.getSelectedItem();
                String doctorItem = (String) doctorCombo.getSelectedItem();
                if (patientItem == null || doctorItem == null) {
                    JOptionPane.showMessageDialog(panel, "Please select all fields");
                    return;
                }
                int patientId = Integer.parseInt(patientItem.split(":")[0].trim());
                String doctorId = doctorItem.split(":")[0].trim();
                String sql = "INSERT INTO SECONDARY_CARE (DOCTOR_ID, PATIENT_ID) VALUES (?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, doctorId);
                pstmt.setInt(2, patientId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Secondary care doctor assigned successfully!");
                loadSecondaryCare(tableModel);
            } catch (SQLException ex) {
                showError(panel, "Failed to assign secondary care doctor", ex);
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a record to remove");
                return;
            }
            int patientId = (int) table.getValueAt(row, 1);
            String doctorId = table.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(panel, 
                "Remove this assignment?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM SECONDARY_CARE WHERE DOCTOR_ID = ? AND PATIENT_ID = ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, doctorId);
                    pstmt.setInt(2, patientId);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(panel, "✓ Assignment removed successfully!");
                    loadSecondaryCare(tableModel);
                } catch (SQLException ex) {
                    showError(panel, "Failed to remove assignment", ex);
                }
            }
        });
        refreshBtn.addActionListener(e -> {
            loadSecondaryCare(tableModel);
            patientCombo.removeAllItems();
            doctorCombo.removeAllItems();
            loadPatientsCombo(patientCombo);
            loadDoctorsCombo(doctorCombo);
        });
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadSecondaryCare(tableModel);
        return panel;
    }
    private void loadSecondaryCare(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            String sql = "SELECT sc.DOCTOR_ID, sc.PATIENT_ID, " +
                        "d.FIRST_NAME || ' ' || d.LAST_NAME AS DOCTOR_NAME, " +
                        "p.FIRST_NAME || ' ' || p.LAST_NAME AS PATIENT_NAME " +
                        "FROM SECONDARY_CARE sc " +
                        "JOIN PATIENT p ON sc.PATIENT_ID = p.PATIENT_ID " +
                        "JOIN DOCTOR d ON sc.DOCTOR_ID = d.DOCTOR_ID " +
                        "ORDER BY p.LAST_NAME";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("DOCTOR_ID"),
                    rs.getString("PATIENT_ID"),
                    rs.getString("DOCTOR_NAME"),
                    rs.getString("PATIENT_NAME")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private JPanel createInteractionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Interaction Record", 
            0, 0, new Font("Arial", Font.BOLD, 14)));
        JTextField interactionIdField = new JTextField();
        JComboBox<String> patientCombo = new JComboBox<>();
        JTextField dateField = new JTextField("01-01-2025");
        JTextField timeField = new JTextField("09:00");
        JTextField descField = new JTextField();
        formPanel.add(createLabel("Interaction ID:"));
        formPanel.add(interactionIdField);
        formPanel.add(createLabel("Patient:"));
        formPanel.add(patientCombo);
        formPanel.add(createLabel("Date (MM-DD-YYYY):"));
        formPanel.add(dateField);
        formPanel.add(createLabel("Time (HH:MM):"));
        formPanel.add(timeField);
        formPanel.add(createLabel("Description:"));
        formPanel.add(descField);
        loadPatientsCombo(patientCombo);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addBtn = createStyledButton("Add", new Color(76, 175, 80));
        JButton deleteBtn = createStyledButton("Delete", new Color(244, 67, 54));
        JButton refreshBtn = createStyledButton("Refresh", new Color(255, 152, 0));
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        String[] columns = {"ID", "Patient ID", "Date", "Time", "Description", "Patient"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        addBtn.addActionListener(e -> {
            try {
                String patientItem = (String) patientCombo.getSelectedItem();
                if (patientItem == null) {
                    JOptionPane.showMessageDialog(panel, "Please select patient");
                    return;
                }
                String patientId = patientItem.split(":")[0].trim();
                String date = dateField.getText().trim();
                String time = timeField.getText().trim();
                int interactionId = Integer.parseInt(interactionIdField.getText().trim());
                String sql = "INSERT INTO INTERACTION_RECORD (INTERACTION_ID, PATIENT_ID, INTERACTION_DATE, INTERACTION_TIME, DESCRIPTION) " +
                            "VALUES (?, ?, TO_DATE(?, 'MM-DD-YYYY'), ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, interactionId);
                pstmt.setString(2, patientId);
                pstmt.setString(3, date);
                pstmt.setString(4, time);
                pstmt.setString(5, nullIfEmpty(descField.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Interaction record added successfully!");
                loadInteractions(tableModel);
            } catch (SQLException ex) {
                showError(panel, "Failed to add interaction record", ex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Interaction ID must be a valid number", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a record to delete");
                return;
            }
            int interactionId = (int) table.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(panel, 
                "Delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM INTERACTION_RECORD WHERE INTERACTION_ID = ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setInt(1, interactionId);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(panel, "Record deleted successfully!");
                    loadInteractions(tableModel);
                } catch (SQLException ex) {
                    showError(panel, "Failed to delete record", ex);
                }
            }
        });
        refreshBtn.addActionListener(e -> {
            loadInteractions(tableModel);
            patientCombo.removeAllItems();
            loadPatientsCombo(patientCombo);
        });
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        loadInteractions(tableModel);
        return panel;
    }
    private void loadInteractions(DefaultTableModel model) {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try {
            String sql = "SELECT i.INTERACTION_ID, i.PATIENT_ID, i.INTERACTION_DATE, i.INTERACTION_TIME, i.DESCRIPTION, " +
                        "p.FIRST_NAME || ' ' || p.LAST_NAME AS PATIENT_NAME " +
                        "FROM INTERACTION_RECORD i " +
                        "JOIN PATIENT p ON i.PATIENT_ID = p.PATIENT_ID " +
                        "ORDER BY i.INTERACTION_DATE DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Date interactionDate = rs.getDate("INTERACTION_DATE");
                String interactionDateStr = (interactionDate != null) ? sdf.format(interactionDate) : "";
                model.addRow(new Object[]{
                    rs.getInt("INTERACTION_ID"),
                    rs.getString("PATIENT_ID"),
                    interactionDateStr,
                    rs.getString("INTERACTION_TIME"),
                    rs.getString("DESCRIPTION"),
                    rs.getString("PATIENT_NAME")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Select Report"));
        String[] reportTypes = {
            "Database Summary",
            "Patients by Department",
            "Doctor Workload",
            "Medication Usage",
            "Recent Prescriptions",
            "Patient Health Record",
            "Procedures by Department",
            "Doctor Procedures List"
        };
        JComboBox<String> reportCombo = new JComboBox<>(reportTypes);
        JButton generateBtn = createStyledButton("Generate Report", new Color(33, 150, 243));
        controlPanel.add(new JLabel("Report Type:"));
        controlPanel.add(reportCombo);
        controlPanel.add(generateBtn);
        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        generateBtn.addActionListener(e -> {
            String selectedReport = (String) reportCombo.getSelectedItem();
            generateReport(selectedReport, reportArea);
        });
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        generateReport("Database Summary", reportArea);
        return panel;
    }
    private void generateReport(String reportType, JTextArea area) {
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append("HOSPITAL MANAGEMENT SYSTEM - ").append(reportType.toUpperCase()).append("\n");
        report.append("Generated: ").append(new java.util.Date()).append("\n");
        report.append("=".repeat(80)).append("\n\n");
        try {
            switch (reportType) {
                case "Database Summary":
                    generateSummaryReport(report);
                    break;
                case "Patients by Department":
                    generatePatientsByDepartmentReport(report);
                    break;
                case "Doctor Workload":
                    generateDoctorWorkloadReport(report);
                    break;
                case "Medication Usage":
                    generateMedicationUsageReport(report);
                    break;
                case "Recent Prescriptions":
                    generateRecentPrescriptionsReport(report);
                    break;
                case "Patient Health Record":
                    generatePatientHealthRecordReport(report);
                    break;
                case "Procedures by Department":
                    generateProceduresByDepartmentReport(report);
                    break;
                case "Doctor Procedures List":
                    generateDoctorProceduresReport(report);
                    break;
            }
        } catch (SQLException e) {
            report.append("ERROR: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }
        area.setText(report.toString());
        area.setCaretPosition(0);
    }
    private void generateSummaryReport(StringBuilder report) throws SQLException {
        report.append("DATABASE SUMMARY\n");
        report.append("-".repeat(80)).append("\n\n");
        String[] tables = {"PATIENT", "DOCTOR", "DEPARTMENT", "MEDICATION", 
                          "PRESCRIPTION", "PROCEDURE_TYPE", "UNDERGOES", "PRIMARY_CARE", "SECONDARY_CARE", "INTERACTION_RECORD"};
        for (String table : tables) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM " + table);
            if (rs.next()) {
                report.append(String.format("%-25s: %d records\n", table, rs.getInt("cnt")));
            }
            rs.close();
            stmt.close();
        }
        report.append("\n");
    }
    private void generatePatientsByDepartmentReport(StringBuilder report) throws SQLException {
        report.append("PATIENTS BY DEPARTMENT\n");
        report.append("-".repeat(80)).append("\n\n");
        String sql = "SELECT d.DEPARTMENT_NAME, COUNT(DISTINCT p.PATIENT_ID) as PATIENT_COUNT " +
                    "FROM DEPARTMENT d " +
                    "LEFT JOIN DOCTOR doc ON d.DEPARTMENT_CODE = doc.DEPARTMENT_CODE " +
                    "LEFT JOIN PRESCRIPTION pr ON doc.DOCTOR_ID = pr.DOCTOR_ID " +
                    "LEFT JOIN PATIENT p ON pr.PATIENT_ID = p.PATIENT_ID " +
                    "GROUP BY d.DEPARTMENT_NAME " +
                    "ORDER BY PATIENT_COUNT DESC";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        report.append(String.format("%-40s %s\n", "Department", "Patients"));
        report.append("-".repeat(80)).append("\n");
        while (rs.next()) {
            report.append(String.format("%-40s %d\n", 
                rs.getString("DEPARTMENT_NAME"),
                rs.getInt("PATIENT_COUNT")));
        }
        rs.close();
        stmt.close();
    }
    private void generateDoctorWorkloadReport(StringBuilder report) throws SQLException {
        report.append("DOCTOR WORKLOAD (By Prescriptions)\n");
        report.append("-".repeat(80)).append("\n\n");
        String sql = "SELECT d.FIRST_NAME || ' ' || d.LAST_NAME as DOCTOR_NAME, " +
                    "dep.DEPARTMENT_NAME, COUNT(*) as PRESCRIPTION_COUNT " +
                    "FROM DOCTOR d " +
                    "LEFT JOIN DEPARTMENT dep ON d.DEPARTMENT_CODE = dep.DEPARTMENT_CODE " +
                    "LEFT JOIN PRESCRIPTION p ON d.DOCTOR_ID = p.DOCTOR_ID " +
                    "GROUP BY d.FIRST_NAME, d.LAST_NAME, dep.DEPARTMENT_NAME " +
                    "ORDER BY PRESCRIPTION_COUNT DESC";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        report.append(String.format("%-30s %-25s %s\n", "Doctor", "Department", "Prescriptions"));
        report.append("-".repeat(80)).append("\n");
        while (rs.next()) {
            report.append(String.format("%-30s %-25s %d\n", 
                rs.getString("DOCTOR_NAME"),
                rs.getString("DEPARTMENT_NAME") != null ? rs.getString("DEPARTMENT_NAME") : "N/A",
                rs.getInt("PRESCRIPTION_COUNT")));
        }
        rs.close();
        stmt.close();
    }
    private void generateMedicationUsageReport(StringBuilder report) throws SQLException {
        report.append("MEDICATION USAGE\n");
        report.append("-".repeat(80)).append("\n\n");
        String sql = "SELECT m.MEDICATION_NAME, m.MANUFACTURER, COUNT(*) as USAGE_COUNT " +
                    "FROM MEDICATION m " +
                    "LEFT JOIN PRESCRIPTION p ON m.MEDICATION_NAME = p.MEDICATION_NAME " +
                    "GROUP BY m.MEDICATION_NAME, m.MANUFACTURER " +
                    "ORDER BY USAGE_COUNT DESC";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        report.append(String.format("%-30s %-30s %s\n", "Medication", "Manufacturer", "Prescriptions"));
        report.append("-".repeat(80)).append("\n");
        while (rs.next()) {
            report.append(String.format("%-30s %-30s %d\n", 
                rs.getString("MEDICATION_NAME"),
                rs.getString("MANUFACTURER") != null ? rs.getString("MANUFACTURER") : "N/A",
                rs.getInt("USAGE_COUNT")));
        }
        rs.close();
        stmt.close();
    }
    private void generateRecentPrescriptionsReport(StringBuilder report) throws SQLException {
        report.append("RECENT PRESCRIPTIONS (Last 20)\n");
        report.append("-".repeat(80)).append("\n\n");
        String sql = "SELECT p.FIRST_NAME || ' ' || p.LAST_NAME as PATIENT_NAME, " +
                    "d.FIRST_NAME || ' ' || d.LAST_NAME as DOCTOR_NAME, " +
                    "pr.MEDICATION_NAME, pr.PRESCRIPTION_DATE " +
                    "FROM PRESCRIPTION pr " +
                    "JOIN PATIENT p ON pr.PATIENT_ID = p.PATIENT_ID " +
                    "JOIN DOCTOR d ON pr.DOCTOR_ID = d.DOCTOR_ID " +
                    "ORDER BY pr.PRESCRIPTION_DATE DESC " +
                    "FETCH FIRST 20 ROWS ONLY";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        report.append(String.format("%-25s %-25s %-20s %s\n", 
            "Patient", "Doctor", "Medication", "Date"));
        report.append("-".repeat(80)).append("\n");
        while (rs.next()) {
            report.append(String.format("%-25s %-25s %-20s %s\n", 
                rs.getString("PATIENT_NAME"),
                rs.getString("DOCTOR_NAME"),
                rs.getString("MEDICATION_NAME"),
                rs.getDate("PRESCRIPTION_DATE")));
        }
        rs.close();
        stmt.close();
    }
    private void generatePatientHealthRecordReport(StringBuilder report) throws SQLException {
        report.append("PATIENT HEALTH RECORD\n");
        report.append("-".repeat(80)).append("\n\n");
        String patientId = JOptionPane.showInputDialog(null, "Enter Patient ID:");
        if (patientId == null || patientId.trim().isEmpty()) {
            report.append("No patient ID provided.\n");
            return;
        }
        patientId = patientId.trim();
        String sql = "SELECT * FROM PATIENT WHERE PATIENT_ID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, patientId);
        ResultSet rs = pstmt.executeQuery();
        if (!rs.next()) {
            report.append("Patient ID not found: ").append(patientId).append("\n");
            rs.close();
            pstmt.close();
            return;
        }
        report.append("PATIENT INFORMATION\n");
        report.append("-".repeat(80)).append("\n");
        report.append(String.format("ID: %s\n", rs.getString("PATIENT_ID")));
        report.append(String.format("Name: %s %s\n", rs.getString("FIRST_NAME"), rs.getString("LAST_NAME")));
        report.append(String.format("Sex: %s\n", rs.getString("SEX") != null ? rs.getString("SEX") : "N/A"));
        report.append(String.format("Age: %s\n", rs.getString("AGE") != null ? rs.getString("AGE") : "N/A"));
        java.sql.Date birthDate = rs.getDate("BIRTHDATE");
        if (birthDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            report.append(String.format("Birth Date: %s\n", sdf.format(birthDate)));
        }
        report.append(String.format("Current Address: %s\n", rs.getString("CURRENT_ADDRESS") != null ? rs.getString("CURRENT_ADDRESS") : "N/A"));
        report.append(String.format("Current Phone: %s\n", rs.getString("CURRENT_PHONE_NUMBER") != null ? rs.getString("CURRENT_PHONE_NUMBER") : "N/A"));
        report.append(String.format("Condition: %s\n", rs.getString("PATIENT_CONDITION") != null ? rs.getString("PATIENT_CONDITION") : "N/A"));
        report.append(String.format("SSN: %s\n", rs.getString("SSN") != null ? rs.getString("SSN") : "N/A"));
        report.append("\n");
        rs.close();
        pstmt.close();
        report.append("PRIMARY CARE PHYSICIAN\n");
        report.append("-".repeat(80)).append("\n");
        sql = "SELECT d.FIRST_NAME, d.LAST_NAME, dep.DEPARTMENT_NAME " +
              "FROM PRIMARY_CARE pc " +
              "JOIN DOCTOR d ON pc.DOCTOR_ID = d.DOCTOR_ID " +
              "LEFT JOIN DEPARTMENT dep ON d.DEPARTMENT_CODE = dep.DEPARTMENT_CODE " +
              "WHERE pc.PATIENT_ID = ?";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, patientId);
        rs = pstmt.executeQuery();
        if (rs.next()) {
            report.append(String.format("Dr. %s %s (%s)\n", 
                rs.getString("FIRST_NAME"), 
                rs.getString("LAST_NAME"),
                rs.getString("DEPARTMENT_NAME") != null ? rs.getString("DEPARTMENT_NAME") : "N/A"));
        } else {
            report.append("No primary care physician assigned\n");
        }
        report.append("\n");
        rs.close();
        pstmt.close();
        report.append("PRESCRIPTIONS\n");
        report.append("-".repeat(80)).append("\n");
        sql = "SELECT pr.MEDICATION_NAME, pr.PRESCRIPTION_DATE, d.FIRST_NAME, d.LAST_NAME, m.MANUFACTURER " +
              "FROM PRESCRIPTION pr " +
              "JOIN DOCTOR d ON pr.DOCTOR_ID = d.DOCTOR_ID " +
              "LEFT JOIN MEDICATION m ON pr.MEDICATION_NAME = m.MEDICATION_NAME " +
              "WHERE pr.PATIENT_ID = ? " +
              "ORDER BY pr.PRESCRIPTION_DATE DESC";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, patientId);
        rs = pstmt.executeQuery();
        boolean hasPrescriptions = false;
        while (rs.next()) {
            hasPrescriptions = true;
            report.append(String.format("- %s (by Dr. %s %s) - Date: %s\n",
                rs.getString("MEDICATION_NAME"),
                rs.getString("FIRST_NAME"),
                rs.getString("LAST_NAME"),
                rs.getDate("PRESCRIPTION_DATE")));
            if (rs.getString("MANUFACTURER") != null) {
                report.append(String.format("  Manufacturer: %s\n", rs.getString("MANUFACTURER")));
            }
        }
        if (!hasPrescriptions) {
            report.append("No prescriptions found\n");
        }
        report.append("\n");
        rs.close();
        pstmt.close();
        report.append("PROCEDURES UNDERGONE\n");
        report.append("-".repeat(80)).append("\n");
        sql = "SELECT u.PROCEDURE_NUMBER, pt.PROCEDURE_NAME, u.PROCEDURE_DATE, u.PROCEDURE_TIME, " +
              "d.FIRST_NAME, d.LAST_NAME, dep.DEPARTMENT_NAME " +
              "FROM UNDERGOES u " +
              "JOIN PROCEDURE_TYPE pt ON u.PROCEDURE_NUMBER = pt.PROCEDURE_NUMBER " +
              "JOIN DOCTOR d ON u.DOCTOR_ID = d.DOCTOR_ID " +
              "LEFT JOIN DEPARTMENT dep ON pt.OFFERING_DEPARTMENT_CODE = dep.DEPARTMENT_CODE " +
              "WHERE u.PATIENT_ID = ? " +
              "ORDER BY u.PROCEDURE_DATE DESC";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, patientId);
        rs = pstmt.executeQuery();
        boolean hasProcedures = false;
        while (rs.next()) {
            hasProcedures = true;
            report.append(String.format("- %s (Code: %s)\n",
                rs.getString("PROCEDURE_NAME"),
                rs.getString("PROCEDURE_NUMBER")));
            report.append(String.format("  Date: %s at %s\n",
                rs.getDate("PROCEDURE_DATE"),
                rs.getString("PROCEDURE_TIME") != null ? rs.getString("PROCEDURE_TIME") : "N/A"));
            report.append(String.format("  Performed by: Dr. %s %s\n",
                rs.getString("FIRST_NAME"),
                rs.getString("LAST_NAME")));
            if (rs.getString("DEPARTMENT_NAME") != null) {
                report.append(String.format("  Department: %s\n", rs.getString("DEPARTMENT_NAME")));
            }
        }
        if (!hasProcedures) {
            report.append("No procedures found\n");
        }
        report.append("\n");
        rs.close();
        pstmt.close();
        report.append("INTERACTION RECORDS\n");
        report.append("-".repeat(80)).append("\n");
        sql = "SELECT i.INTERACTION_ID, i.INTERACTION_DATE, i.INTERACTION_TIME, i.DESCRIPTION " +
              "FROM INTERACTION_RECORD i " +
              "WHERE i.PATIENT_ID = ? " +
              "ORDER BY i.INTERACTION_DATE DESC";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, patientId);
        rs = pstmt.executeQuery();
        boolean hasInteractions = false;
        while (rs.next()) {
            hasInteractions = true;
            report.append(String.format("Interaction ID: %s\n", rs.getString("INTERACTION_ID")));
            report.append(String.format("Date: %s at %s\n",
                rs.getDate("INTERACTION_DATE"),
                rs.getString("INTERACTION_TIME") != null ? rs.getString("INTERACTION_TIME") : "N/A"));
            String description = rs.getString("DESCRIPTION");
            if (description != null && !description.isEmpty()) {
                report.append(String.format("Description: %s\n", description));
            }
            report.append("\n");
        }
        if (!hasInteractions) {
            report.append("No interaction records found\n");
        }
        rs.close();
        pstmt.close();
    }
    private void generateProceduresByDepartmentReport(StringBuilder report) throws SQLException {
        report.append("PROCEDURES BY DEPARTMENT\n");
        report.append("-".repeat(80)).append("\n\n");
        String deptInput = JOptionPane.showInputDialog(null, "Enter Department Name or Code:");
        if (deptInput == null || deptInput.trim().isEmpty()) {
            report.append("No department provided.\n");
            return;
        }
        deptInput = deptInput.trim();
        String sql = "SELECT d.DEPARTMENT_CODE, d.DEPARTMENT_NAME, d.OFFICE_NUMBER, d.OFFICE_PHONE, d.DEPARTMENT_HEAD, " +
                    "pt.PROCEDURE_NUMBER, pt.PROCEDURE_NAME, pt.DURATION_MINUTES " +
                    "FROM DEPARTMENT d " +
                    "LEFT JOIN PROCEDURE_TYPE pt ON d.DEPARTMENT_CODE = pt.OFFERING_DEPARTMENT_CODE " +
                    "WHERE UPPER(d.DEPARTMENT_NAME) LIKE UPPER(?) OR UPPER(d.DEPARTMENT_CODE) LIKE UPPER(?) " +
                    "ORDER BY pt.PROCEDURE_NAME";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        String searchPattern = "%" + deptInput + "%";
        pstmt.setString(1, searchPattern);
        pstmt.setString(2, searchPattern);
        ResultSet rs = pstmt.executeQuery();
        String deptCode = null;
        String deptName = null;
        String officeNumber = null;
        String officePhone = null;
        String deptHead = null;
        boolean found = false;
        int procedureCount = 0;
        while (rs.next()) {
            if (!found) {
                deptCode = rs.getString("DEPARTMENT_CODE");
                deptName = rs.getString("DEPARTMENT_NAME");
                officeNumber = rs.getString("OFFICE_NUMBER");
                officePhone = rs.getString("OFFICE_PHONE");
                deptHead = rs.getString("DEPARTMENT_HEAD");
                found = true;
                report.append("DEPARTMENT INFORMATION\n");
                report.append("-".repeat(80)).append("\n");
                report.append(String.format("Code: %s\n", deptCode));
                report.append(String.format("Name: %s\n", deptName));
                report.append(String.format("Office Number: %s\n", officeNumber != null ? officeNumber : "N/A"));
                report.append(String.format("Office Phone: %s\n", officePhone != null ? officePhone : "N/A"));
                report.append(String.format("Department Head: %s\n\n", deptHead != null ? deptHead : "N/A"));
                report.append("PROCEDURES OFFERED\n");
                report.append("-".repeat(80)).append("\n");
                report.append(String.format("%-15s %-45s %s\n", "Code", "Procedure Name", "Duration (min)"));
                report.append("-".repeat(80)).append("\n");
            }
            String procNumber = rs.getString("PROCEDURE_NUMBER");
            if (procNumber != null) {
                procedureCount++;
                String duration = rs.getString("DURATION_MINUTES");
                report.append(String.format("%-15s %-45s %s\n",
                    procNumber,
                    rs.getString("PROCEDURE_NAME"),
                    duration != null ? duration : "N/A"));
            }
        }
        if (!found) {
            report.append("Department not found: ").append(deptInput).append("\n");
        } else if (procedureCount == 0) {
            report.append("No procedures offered by this department.\n");
        } else {
            report.append("\n").append(String.format("Total Procedures: %d\n", procedureCount));
        }
        rs.close();
        pstmt.close();
    }
    private void generateDoctorProceduresReport(StringBuilder report) throws SQLException {
        report.append("DOCTOR PROCEDURES LIST\n");
        report.append("-".repeat(80)).append("\n\n");
        String doctorId = JOptionPane.showInputDialog(null, "Enter Doctor ID:");
        if (doctorId == null || doctorId.trim().isEmpty()) {
            report.append("No doctor ID provided.\n");
            return;
        }
        doctorId = doctorId.trim();
        String sql = "SELECT d.FIRST_NAME, d.LAST_NAME, d.PHONE_NUMBER, d.ADDRESS, dep.DEPARTMENT_NAME " +
                    "FROM DOCTOR d " +
                    "LEFT JOIN DEPARTMENT dep ON d.DEPARTMENT_CODE = dep.DEPARTMENT_CODE " +
                    "WHERE d.DOCTOR_ID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, doctorId);
        ResultSet rs = pstmt.executeQuery();
        if (!rs.next()) {
            report.append("Doctor ID not found: ").append(doctorId).append("\n");
            rs.close();
            pstmt.close();
            return;
        }
        report.append("DOCTOR INFORMATION\n");
        report.append("-".repeat(80)).append("\n");
        report.append(String.format("ID: %s\n", doctorId));
        report.append(String.format("Name: Dr. %s %s\n", rs.getString("FIRST_NAME"), rs.getString("LAST_NAME")));
        report.append(String.format("Phone: %s\n", rs.getString("PHONE_NUMBER") != null ? rs.getString("PHONE_NUMBER") : "N/A"));
        report.append(String.format("Address: %s\n", rs.getString("ADDRESS") != null ? rs.getString("ADDRESS") : "N/A"));
        report.append(String.format("Department: %s\n\n", 
            rs.getString("DEPARTMENT_NAME") != null ? rs.getString("DEPARTMENT_NAME") : "N/A"));
        rs.close();
        pstmt.close();
        report.append("PROCEDURES PERFORMED\n");
        report.append("-".repeat(80)).append("\n");
        sql = "SELECT u.PROCEDURE_NUMBER, pt.PROCEDURE_NAME, u.PROCEDURE_DATE, u.PROCEDURE_TIME, " +
              "p.FIRST_NAME, p.LAST_NAME, p.PATIENT_ID " +
              "FROM UNDERGOES u " +
              "JOIN PROCEDURE_TYPE pt ON u.PROCEDURE_NUMBER = pt.PROCEDURE_NUMBER " +
              "JOIN PATIENT p ON u.PATIENT_ID = p.PATIENT_ID " +
              "WHERE u.DOCTOR_ID = ? " +
              "ORDER BY u.PROCEDURE_DATE DESC";
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, doctorId);
        rs = pstmt.executeQuery();
        report.append(String.format("%-15s %-30s %-25s %-12s %s\n", 
            "Proc. Code", "Procedure Name", "Patient", "Date", "Time"));
        report.append("-".repeat(80)).append("\n");
        int count = 0;
        while (rs.next()) {
            count++;
            report.append(String.format("%-15s %-30s %-25s %-12s %s\n",
                rs.getString("PROCEDURE_NUMBER"),
                rs.getString("PROCEDURE_NAME"),
                rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME"),
                rs.getDate("PROCEDURE_DATE").toString(),
                rs.getString("PROCEDURE_TIME") != null ? rs.getString("PROCEDURE_TIME") : "N/A"));
        }
        if (count == 0) {
            report.append("No procedures found for this doctor.\n");
        } else {
            report.append("\n").append(String.format("Total Procedures: %d\n", count));
        }
        rs.close();
        pstmt.close();
    }
    private void loadDepartmentsCombo(JComboBox<String> combo) {
        try {
            String sql = "SELECT DEPARTMENT_CODE, DEPARTMENT_NAME FROM DEPARTMENT ORDER BY DEPARTMENT_NAME";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                combo.addItem(rs.getString("DEPARTMENT_CODE") + ": " + rs.getString("DEPARTMENT_NAME"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadPatientsCombo(JComboBox<String> combo) {
        try {
            String sql = "SELECT PATIENT_ID, FIRST_NAME, LAST_NAME FROM PATIENT ORDER BY LAST_NAME";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                combo.addItem(rs.getString("PATIENT_ID") + ": " + 
                    rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadDoctorsCombo(JComboBox<String> combo) {
        try {
            String sql = "SELECT DOCTOR_ID, FIRST_NAME, LAST_NAME FROM DOCTOR ORDER BY LAST_NAME";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                combo.addItem(rs.getString("DOCTOR_ID") + ": " + 
                    rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadMedicationsCombo(JComboBox<String> combo) {
        try {
            String sql = "SELECT MEDICATION_NAME FROM MEDICATION ORDER BY MEDICATION_NAME";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                combo.addItem(rs.getString("MEDICATION_NAME"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private int getLastInsertedPatientId() throws SQLException {
        String sql = "SELECT MAX(PATIENT_ID) as LAST_ID FROM PATIENT";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        int id = 0;
        if (rs.next()) {
            id = rs.getInt("LAST_ID");
        }
        rs.close();
        stmt.close();
        return id;
    }
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }
    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        return btn;
    }
    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setRowHeight(25);
    }
    private boolean validateFields(JTextField... fields) {
        for (JTextField f : fields) {
            if (f.getText().trim().isEmpty()) return false;
        }
        return true;
    }
    private String nullIfEmpty(String s) {
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
    private void showError(Component parent, String msg, Exception ex) {
        JOptionPane.showMessageDialog(parent, msg + "\nError: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    private String getString(JTable table, int row, int col) {
        Object val = table.getValueAt(row, col);
        return val == null ? "" : val.toString();
    }
    private void clearAllFields(JTextField... fields) {
        for (JTextField f : fields) {
            f.setText("");
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalManagementSystem::new);
    }

}
