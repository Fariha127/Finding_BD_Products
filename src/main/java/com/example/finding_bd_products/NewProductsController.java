package com.example.finding_bd_products;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class NewProductsController {

    @FXML
    private GridPane productsGrid;

    @FXML
    private TextField searchField;

    @FXML
    private Button homeBtn;

    @FXML
    private Button allProductsBtn;

    @FXML
    private Button categoriesBtn;

    @FXML
    private Button newlyAddedBtn;

    @FXML
    private Button favouritesBtn;

    @FXML
    private Button favouriteCategoriesBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private Button signupBtn;
    
    @FXML
    private Button addProductBtn;
    
    @FXML
    private Button myProfileBtn;
    
    @FXML
    private Button logoutBtn;

    private DatabaseManager dbManager;

    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        if (productsGrid != null) {
            loadProducts();
        }
        
        // Show "Add Product" button only for logged-in vendors
        if (addProductBtn != null && VendorSession.getInstance().isLoggedIn()) {
            addProductBtn.setVisible(true);
            addProductBtn.setManaged(true);
        }
        
        // Show "My Profile" button only for logged-in users (not vendors)
        if (myProfileBtn != null && UserSession.getInstance().isLoggedIn()) {
            myProfileBtn.setVisible(true);
            myProfileBtn.setManaged(true);
        }
        
        // Hide login/signup buttons and show logout button if user or vendor is logged in
        if (UserSession.getInstance().isLoggedIn() || VendorSession.getInstance().isLoggedIn()) {
            if (loginBtn != null) {
                loginBtn.setVisible(false);
                loginBtn.setManaged(false);
            }
            if (signupBtn != null) {
                signupBtn.setVisible(false);
                signupBtn.setManaged(false);
            }
            if (logoutBtn != null) {
                logoutBtn.setVisible(true);
                logoutBtn.setManaged(true);
            }
        }
    }

    @FXML
    protected void goToLogin() {
        loadPage("Login.fxml");
    }

    @FXML
    protected void goToSignup() {
        loadPage("SignupUser.fxml");
    }

    @FXML
    protected void goBack() {
        loadPage("Home.fxml");
    }
    
    @FXML
    protected void goToAddProduct() {
        if (!VendorSession.getInstance().isLoggedIn()) {
            showLoginAlert();
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddProduct.fxml"));
            Parent root = loader.load();
            
            AddProductController controller = loader.getController();
            controller.setVendorInfo(
                VendorSession.getInstance().getCurrentVendorId(),
                VendorSession.getInstance().getVendorType()
            );
            
            Stage stage = (Stage) addProductBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Add Product");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    protected void goToMyProfile() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MyProfile.fxml"));
            Stage stage = (Stage) myProfileBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    protected void handleLogout() {
        try {
            // Clear both user and vendor sessions
            UserSession.getInstance().logout();
            VendorSession.getInstance().logout();
            
            // Navigate back to login page
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProducts() {
        productsGrid.getChildren().clear();

        // Get latest 12 products from database (reverse order to show newest first)
        java.util.List<Product> allProducts = dbManager.getAllProducts();
        
        if (allProducts.isEmpty()) {
            System.out.println("No products found in database");
            return;
        }
        
        // Reverse the list to show newest products first (assuming higher IDs are newer)
        java.util.Collections.reverse(allProducts);
        
        System.out.println("Loaded " + allProducts.size() + " products from database");

        int col = 0;
        int row = 0;
        
        // Display up to 12 products (latest)
        int maxProducts = Math.min(allProducts.size(), 12);
        
        for (int i = 0; i < maxProducts; i++) {
            Product product = allProducts.get(i);
            if (product != null) {
                VBox card = createProductCard(product);
                productsGrid.add(card, col, row);
                col++;
                if (col > 2) {
                    col = 0;
                    row++;
                }
            }
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setPrefSize(220, 280);
        card.setStyle("-fx-background-color: white; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-padding: 15; -fx-cursor: hand;");

        card.setOnMouseClicked(event -> navigateToProductDetails(product.getProductId()));

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(2.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.08));
        card.setEffect(shadow);

        // Product image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(190);
        imageView.setFitHeight(140);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        
        // Set image or placeholder
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                // Load from resources (local files)
                Image image = new Image(getClass().getResourceAsStream(product.getImageUrl()));
                imageView.setImage(image);
            } catch (Exception e) {
                // If image fails to load, show placeholder
                imageView.setStyle("-fx-background-color: #F5F5F5;");
            }
        } else {
            // Placeholder style
            imageView.setStyle("-fx-background-color: #F5F5F5;");
        }

        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 15));
        nameLabel.setStyle("-fx-text-fill: #333333;");

        Label descLabel = new Label(product.getDescription());
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");

        HBox priceBox = new HBox(5);
        priceBox.setStyle("-fx-border-width: 0;");
        Label priceLabel = new Label("৳ " + (int)product.getPrice());
        priceLabel.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 16));
        priceLabel.setStyle("-fx-text-fill: #D32F2F;");
        Label unitLabel = new Label("/" + product.getUnit());
        unitLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");
        priceBox.getChildren().addAll(priceLabel, unitLabel);

        Region spacer = new Region();
        spacer.setStyle("-fx-border-width: 0; -fx-background-color: transparent;");
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox buttonBox = new HBox(8);
        buttonBox.setStyle("-fx-border-width: 0; -fx-border-color: transparent;");
        Button favButton = new Button("♡");
        favButton.setPrefSize(45, 32);
        
        // Check if product is favourite and set initial style (only if logged in)
        boolean isFav = UserSession.getInstance().isLoggedIn() && dbManager.isFavourite(product.getProductId());
        if (isFav) {
            favButton.setText("♥");
            favButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; " +
                    "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px; -fx-border-width: 0;");
        } else {
            favButton.setStyle("-fx-background-color: #FFE5E5; -fx-text-fill: #D32F2F; " +
                    "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px; -fx-border-width: 0;");
        }
        
        favButton.setOnAction(e -> {
            e.consume();
            if (!UserSession.getInstance().isLoggedIn()) {
                showLoginAlert();
                return;
            }
            boolean currentlyFav = dbManager.isFavourite(product.getProductId());
            if (currentlyFav) {
                // Remove from favourites
                dbManager.removeFromFavourites(product.getProductId());
                favButton.setText("♡");
                favButton.setStyle("-fx-background-color: #FFE5E5; -fx-text-fill: #D32F2F; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px; -fx-border-width: 0;");
                System.out.println("Removed from favourites: " + product.getName());
            } else {
                // Add to favourites
                dbManager.addToFavourites(product.getProductId());
                favButton.setText("♥");
                favButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px; -fx-border-width: 0;");
                System.out.println("Added to favourites: " + product.getName());
            }
        });

        Button rateButton = new Button("⭐ Rate");
        rateButton.setPrefHeight(32);
        rateButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; " +
                "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12px; " +
                "-fx-font-weight: bold;");
        rateButton.setOnAction(e -> {
            e.consume();
            if (!UserSession.getInstance().isLoggedIn()) {
                showLoginAlert();
                return;
            }
            navigateToProductDetails(product.getProductId());
        });
        HBox.setHgrow(rateButton, javafx.scene.layout.Priority.ALWAYS);

        buttonBox.getChildren().addAll(favButton, rateButton);

        card.getChildren().addAll(imageView, nameLabel, descLabel, priceBox, spacer, buttonBox);

        return card;
    }

    private void navigateToProductDetails(String productId) {
        try {
            System.out.println("Navigating to product: " + productId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductDetails.fxml"));
            Parent root = loader.load();

            ProductDetailsController controller = loader.getController();
            if (controller != null) {
                controller.setProduct(productId);
            } else {
                System.err.println("Controller is null!");
            }

            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Error loading ProductDetails.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void showHome() {
        loadPage("Home.fxml");
    }

    @FXML
    protected void showAllProducts() {
        loadPage("AllProducts.fxml");
    }

    @FXML
    protected void showCategories() {
        loadPage("Categories.fxml");
    }

    @FXML
    protected void showNewlyAdded() {
        // Already on new products page
    }

    @FXML
    protected void showFavourites() {
        loadPage("MyFavouriteProducts.fxml");
    }

    @FXML
    protected void showFavouriteCategories() {
        loadPage("FavouriteCategories.fxml");
    }

    @FXML
    protected void onSearch() {
        String searchText = searchField.getText();
        System.out.println("Searching for: " + searchText);
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showLoginAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Login Required");
        alert.setHeaderText("You need to log in");
        alert.setContentText("Please log in to perform this action.");
        alert.showAndWait();
    }
}
