package com.example.finding_bd_products;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import java.util.ArrayList;
import java.util.List;

public class MyFavouriteProductsController {
    @FXML
    private GridPane productsGrid;

    @FXML
    private TextField searchField;

    @FXML
    private Button homeBtn;

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

    private List<Product> favouriteProducts = new ArrayList<>();
    private DatabaseManager dbManager;
    private Stage stage;

    @FXML
    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        loadFavouriteProducts();
    }

    private void loadFavouriteProducts() {
        productsGrid.getChildren().clear();
        favouriteProducts = dbManager.getFavouriteProducts();
        
        if (favouriteProducts.isEmpty()) {
            Label emptyLabel = new Label("No favourite products yet. Start adding products to your favourites!");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888888; -fx-padding: 50;");
            VBox emptyBox = new VBox(emptyLabel);
            emptyBox.setStyle("-fx-alignment: center;");
            productsGrid.add(emptyBox, 0, 0, 3, 1);
            return;
        }
        
        int col = 0;
        int row = 0;
        for (Product product : favouriteProducts) {
            VBox card = createProductCard(product);
            productsGrid.add(card, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; " +
                "-fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10; " +
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
        Label priceLabel = new Label("৳ " + (int)product.getPrice());
        priceLabel.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 16));
        priceLabel.setStyle("-fx-text-fill: #D32F2F;");
        Label unitLabel = new Label("/" + product.getUnit());
        unitLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");
        priceBox.getChildren().addAll(priceLabel, unitLabel);

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox buttonBox = new HBox(8);
        Button favButton = new Button("♥");
        favButton.setPrefSize(45, 32);
        favButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; " +
                "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px;");
        favButton.setOnAction(e -> {
            e.consume();
            // Remove from favourites
            dbManager.removeFromFavourites(product.getProductId());
            loadFavouriteProducts(); // Refresh the grid
            System.out.println("Removed from favourites: " + product.getName());
        });

        Button rateButton = new Button("⭐ Rate");
        rateButton.setPrefHeight(32);
        rateButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; " +
                "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12px; " +
                "-fx-font-weight: bold;");
        rateButton.setOnAction(e -> {
            e.consume();
            System.out.println("Rate product: " + product.getName());
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
                System.err.println("ProductDetailsController is null");
            }
            
            stage = (Stage) homeBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Error loading ProductDetails.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    protected void showHome() {
        loadPage("Home.fxml");
    }

    @FXML
    protected void showCategories() {
        loadPage("Categories.fxml");
    }

    @FXML
    protected void showNewlyAdded() {
        loadPage("NewProducts.fxml");
    }

    @FXML
    protected void showFavourites() {
        loadFavouriteProducts();
    }

    @FXML
    protected void showFavouriteCategories() {
        loadPage("FavouriteCategories.fxml");
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
}

