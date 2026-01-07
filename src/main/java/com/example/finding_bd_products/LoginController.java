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

public class LoginController {

    @FXML
    private ComboBox<String> userTypeCombo;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private TextField visiblePasswordField;
    
    @FXML
    private Button togglePasswordBtn;

    @FXML
    private Label errorLabel;

    private DatabaseManager dbManager;
    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        
        // Populate user type combo box
        userTypeCombo.getItems().addAll("Admin", "Company Vendor", "Retail Vendor", "User");
        userTypeCombo.setValue("User"); // Default selection
        
        // Bind password fields to keep them synchronized
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!isPasswordVisible) {
                visiblePasswordField.setText(newVal);
            }
        });
        
        visiblePasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isPasswordVisible) {
                passwordField.setText(newVal);
            }
        });
    }
    
    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        
        if (isPasswordVisible) {
            // Show password
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordBtn.setText("👁");
        } else {
            // Hide password
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            togglePasswordBtn.setText("👁");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String userType = userTypeCombo.getValue();
        String email = emailField.getText().trim();
        String password = isPasswordVisible ? visiblePasswordField.getText() : passwordField.getText();

        // Validation
        if (userType == null || userType.isEmpty()) {
            errorLabel.setText("Please select a user type!");
            return;
        }

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password!");
            return;
        }

        try {
            boolean loginSuccess = false;
            String redirectPage = "";

            switch (userType) {
                case "Admin":
                    if (dbManager.authenticateAdmin(email, password)) {
                        loginSuccess = true;
                        redirectPage = "AdminDashboard.fxml";
                    } else {
                        errorLabel.setText("Incorrect email or password");
                    }
                    break;

                case "Company Vendor":
                    CompanyVendor companyVendor = dbManager.authenticateCompanyVendor(email, password);
                    if (companyVendor != null) {
                        VendorSession.getInstance().loginCompanyVendor(companyVendor);
                        loginSuccess = true;
                        redirectPage = "Home.fxml";
                    } else {
                        errorLabel.setText("Incorrect email or password");
                    }
                    break;

                case "Retail Vendor":
                    RetailVendor retailVendor = dbManager.authenticateRetailVendor(email, password);
                    if (retailVendor != null) {
                        VendorSession.getInstance().loginRetailVendor(retailVendor);
                        loginSuccess = true;
                        redirectPage = "Home.fxml";
                    } else {
                        errorLabel.setText("Incorrect email or password");
                    }
                    break;

                case "User":
                    User user = dbManager.authenticateUser(email, password);
                    if (user != null) {
                        UserSession.getInstance().login(user);
                        loginSuccess = true;
                        redirectPage = "Home.fxml";
                    } else {
                        errorLabel.setText("Incorrect email or password");
                    }
                    break;

                default:
                    errorLabel.setText("Invalid user type!");
            }

            if (loginSuccess) {
                // Navigate to appropriate dashboard
                navigateToPage(event, redirectPage);
            }

        } catch (Exception e) {
            errorLabel.setText("An error occurred during login!");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToCompanyVendorSignup(ActionEvent event) {
        navigateToPage(event, "SignupCompanyVendor.fxml");
    }

    @FXML
    private void goToRetailVendorSignup(ActionEvent event) {
        navigateToPage(event, "SignupRetailVendor.fxml");
    }

    @FXML
    private void goToUserSignup(ActionEvent event) {
        navigateToPage(event, "SignupUser.fxml");
    }

    @FXML
    private void goBack(ActionEvent event) {
        navigateToPage(event, "Home.fxml");
    }

    private void navigateToPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            errorLabel.setText("Unable to load page: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
