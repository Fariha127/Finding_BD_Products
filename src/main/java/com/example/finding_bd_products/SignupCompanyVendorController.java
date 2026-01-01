package com.example.finding_bd_products;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

public class SignupCompanyVendorController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField designationField;

    @FXML
    private TextField companyNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField registrationNumberField;

    @FXML
    private TextField bstiCertificateField;

    @FXML
    private TextArea addressField;

    @FXML
    private TextField tinField;

    @FXML
    private Label messageLabel;

    private DatabaseManager dbManager;

    @FXML
    public void initialize() {
        dbManager = DatabaseManager.getInstance();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Clear previous messages
        messageLabel.setText("");
        messageLabel.setStyle("-fx-text-fill: black;");

        // Get field values
        String fullName = fullNameField.getText().trim();
        String designation = designationField.getText().trim();
        String companyName = companyNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String phone = phoneField.getText().trim();
        String registrationNumber = registrationNumberField.getText().trim();
        String bstiCertificate = bstiCertificateField.getText().trim();
        String address = addressField.getText().trim();
        String tin = tinField.getText().trim();

        // Validation
        if (fullName.isEmpty() || designation.isEmpty() || companyName.isEmpty() || 
            email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || 
            phone.isEmpty() || address.isEmpty()) {
            messageLabel.setText("Please fill in all required fields (marked with *)!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (password.length() < 6) {
            messageLabel.setText("Password must be at least 6 characters long!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            messageLabel.setText("Please enter a valid email address!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!phone.matches("\\d{11}")) {
            messageLabel.setText("Please enter a valid 11-digit phone number!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            // Create CompanyVendor object
            String vendorId = "CV-" + UUID.randomUUID().toString().substring(0, 8);
            CompanyVendor vendor = new CompanyVendor(
                vendorId, fullName, designation, companyName, email, password,
                phone, registrationNumber, bstiCertificate, address, tin, "pending"
            );

            // Register in database
            boolean success = dbManager.registerCompanyVendor(vendor);

            if (success) {
                messageLabel.setText("Registration successful! Your account is pending admin approval. You will be notified once approved.");
                messageLabel.setStyle("-fx-text-fill: green;");
                
                // Clear all fields
                clearFields();

                // Optionally redirect to login after a delay
                // For now, user can click "Back to Login" manually
            } else {
                messageLabel.setText("Registration failed! Email may already be registered.");
                messageLabel.setStyle("-fx-text-fill: red;");
            }

        } catch (Exception e) {
            messageLabel.setText("An error occurred during registration!");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        fullNameField.clear();
        designationField.clear();
        companyNameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        phoneField.clear();
        registrationNumberField.clear();
        bstiCertificateField.clear();
        addressField.clear();
        tinField.clear();
    }
}
