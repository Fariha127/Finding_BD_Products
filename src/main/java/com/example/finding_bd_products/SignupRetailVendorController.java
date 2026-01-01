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

public class SignupRetailVendorController {

    @FXML
    private TextField ownerNameField;

    @FXML
    private TextField shopNameField;

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
    private TextField tradeLicenseField;

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
        String ownerName = ownerNameField.getText().trim();
        String shopName = shopNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String phone = phoneField.getText().trim();
        String registrationNumber = registrationNumberField.getText().trim();
        String tradeLicense = tradeLicenseField.getText().trim();
        String address = addressField.getText().trim();
        String tin = tinField.getText().trim();

        // Validation
        if (ownerName.isEmpty() || shopName.isEmpty() || email.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty() || address.isEmpty()) {
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
            // Create RetailVendor object
            String vendorId = "RV-" + UUID.randomUUID().toString().substring(0, 8);
            RetailVendor vendor = new RetailVendor(
                vendorId, ownerName, shopName, email, password, phone,
                registrationNumber, tradeLicense, address, tin, "pending"
            );

            // Register in database
            boolean success = dbManager.registerRetailVendor(vendor);

            if (success) {
                messageLabel.setText("Registration successful! Your account is pending admin approval. You will be notified once approved.");
                messageLabel.setStyle("-fx-text-fill: green;");
                
                // Clear all fields
                clearFields();

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
        ownerNameField.clear();
        shopNameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        phoneField.clear();
        registrationNumberField.clear();
        tradeLicenseField.clear();
        addressField.clear();
        tinField.clear();
    }
}
