package com.example.finding_bd_products;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class AddProductController {

    @FXML private TextField productNameField;
    @FXML private TextField manufacturerField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private TextField volumeField;
    @FXML private TextField imagePathField;
    @FXML private ImageView imagePreview;
    @FXML private Label messageLabel;
    @FXML private Button backButton;

    private DatabaseManager dbManager;
    private String currentVendorId;
    private String vendorType; // "company" or "retail"

    @FXML
    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        
        // Populate categories
        categoryComboBox.getItems().addAll(
            "Grocery",
            "Dairy Products",
            "Beverages",
            "Snacks",
            "Personal Care",
            "Household Items",
            "Frozen Foods",
            "Bakery",
            "Health & Wellness",
            "Baby Care"
        );
    }

    public void setVendorInfo(String vendorId, String vendorType) {
        this.currentVendorId = vendorId;
        this.vendorType = vendorType;
        
        // Check if vendor is approved
        if (!dbManager.isVendorApproved(vendorId, vendorType)) {
            showAlert(Alert.AlertType.ERROR, "Access Denied", 
                "Your vendor account is not yet approved by the admin. You cannot add products until approved.");
            goBack();
            return;
        }
        
        // Auto-fill manufacturer name
        String manufacturerName = dbManager.getManufacturerNamePublic(vendorId);
        if (manufacturerName != null && !manufacturerName.isEmpty()) {
            manufacturerField.setText(manufacturerName);
        }
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        Stage stage = (Stage) imagePathField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            try {
                // Get file extension
                String fileName = selectedFile.getName();
                String extension = fileName.substring(fileName.lastIndexOf("."));
                
                // Generate unique filename
                String uniqueFileName = UUID.randomUUID().toString().substring(0, 8) + extension;
                
                // Get the resources/images directory path
                String resourcesPath = "src/main/resources/images/";
                Path targetPath = Paths.get(resourcesPath + uniqueFileName);
                
                // Create directory if it doesn't exist
                Files.createDirectories(targetPath.getParent());
                
                // Copy file to resources/images folder
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                
                // Store the relative path that will work from resources
                String resourceImagePath = "/images/" + uniqueFileName;
                imagePathField.setText(resourceImagePath);
                
                // Show preview using the original file
                Image image = new Image(selectedFile.toURI().toString());
                imagePreview.setImage(image);
                
                messageLabel.setText("Image uploaded successfully!");
                messageLabel.setStyle("-fx-text-fill: green;");
                
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to copy image: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load image preview.");
            }
        }
    }

    @FXML
    private void handleAddProduct() {
        messageLabel.setText("");
        
        // Validate inputs
        String name = productNameField.getText().trim();
        String category = categoryComboBox.getValue();
        String description = descriptionArea.getText().trim();
        String priceText = priceField.getText().trim();
        String volume = volumeField.getText().trim();
        String imagePath = imagePathField.getText().trim();
        
        if (name.isEmpty() || category == null || description.isEmpty() || 
            priceText.isEmpty() || volume.isEmpty() || imagePath.isEmpty()) {
            messageLabel.setText("Please fill in all required fields.");
            return;
        }
        
        // Validate price
        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                messageLabel.setText("Price must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Please enter a valid price.");
            return;
        }
        
        // Generate product ID
        String productId = "PROD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Add product to database
        if (dbManager.addProductByVendor(productId, name, description, price, volume, category, imagePath, currentVendorId)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Product '" + name + "' has been added successfully!\nProduct ID: " + productId);
            handleClearForm();
        } else {
            messageLabel.setText("Failed to add product. Please try again.");
        }
    }

    @FXML
    private void handleClearForm() {
        productNameField.clear();
        categoryComboBox.setValue(null);
        descriptionArea.clear();
        priceField.clear();
        volumeField.clear();
        imagePathField.clear();
        imagePreview.setImage(null);
        messageLabel.setText("");
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
            stage.setTitle("Deshi Store - Home");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to go back: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) productNameField.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Deshi Store - Home");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to go back to home: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
