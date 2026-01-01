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
import java.time.LocalDate;
import java.util.UUID;

public class SignupUserController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField phoneField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private ComboBox<String> genderCombo;

    @FXML
    private TextField cityField;

    @FXML
    private Label messageLabel;

    private DatabaseManager dbManager;

    @FXML
    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        
        // Populate gender combo box
        genderCombo.getItems().addAll("Male", "Female", "Other");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Clear previous messages
        messageLabel.setText("");
        messageLabel.setStyle("-fx-text-fill: black;");

        // Get field values
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String phone = phoneField.getText().trim();
        LocalDate dob = dobPicker.getValue();
        String gender = genderCombo.getValue();
        String city = cityField.getText().trim();

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || 
            confirmPassword.isEmpty() || phone.isEmpty() || dob == null || gender == null) {
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

        // Age validation (must be at least 13 years old)
        LocalDate minDate = LocalDate.now().minusYears(13);
        if (dob.isAfter(minDate)) {
            messageLabel.setText("You must be at least 13 years old to register!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            // Create User object
            String userId = "U-" + UUID.randomUUID().toString().substring(0, 8);
            User user = new User(
                userId, fullName, email, password, phone,
                dob.toString(), gender, city, "user", "pending"
            );

            // Register in database
            boolean success = dbManager.registerUser(user);

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
        fullNameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        phoneField.clear();
        dobPicker.setValue(null);
        genderCombo.setValue(null);
        cityField.clear();
    }
}
