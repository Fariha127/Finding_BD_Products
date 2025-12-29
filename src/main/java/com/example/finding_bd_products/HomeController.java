package com.example.finding_bd_products;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;


public class HomeController {
    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button homeBtn;

    @FXML
    private Button categoriesBtn;

    @FXML
    private Button newlyAddedBtn;

    @FXML
    private Button favouritesBtn;

    @FXML
    private GridPane recommendedGrid;

    @FXML
    private Button loginBtn;

    @FXML
    private Button signupBtn;

    private DatabaseManager dbManager;

    @FXML
    protected void showHome() {
    }

    @FXML
    protected void goToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void goToSignup() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("SignupUser.fxml"));
            Stage stage = (Stage) signupBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        loadPage("MyFavouriteProducts.fxml");
    }

    @FXML
    protected void onSearch() {
        String searchText = searchField.getText();
    }

    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        if (recommendedGrid != null) {
            loadRecommendedProducts();
        }
    }

    private void loadRecommendedProducts() {
        recommendedGrid.getChildren().clear();

        Product[] recommended = {
                dbManager.getProduct("mojo"),
                dbManager.getProduct("mediplus"),
                dbManager.getProduct("spa-water"),
                dbManager.getProduct("meril-soap"),
                dbManager.getProduct("shejan-juice"),
                dbManager.getProduct("pran-potata"),
                dbManager.getProduct("ruchi-chanachur"),
                dbManager.getProduct("bashundhara-towel"),
                dbManager.getProduct("revive-lotion"),
                dbManager.getProduct("jui-oil"),
                dbManager.getProduct("radhuni-tumeric"),
                dbManager.getProduct("pran-ghee")
        };

        int col = 0;
        int row = 0;
        for (Product product : recommended) {
            if (product != null) {
                VBox card = createProductCard(product);
                recommendedGrid.add(card, col, row);
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

        Region imagePlaceholder = new Region();
        imagePlaceholder.setPrefSize(190, 140);
        imagePlaceholder.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 8;");

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
        
        // Check if product is favourite and set initial style
        boolean isFav = dbManager.isFavourite(product.getProductId());
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
            System.out.println("Rate product: " + product.getName());
        });
        HBox.setHgrow(rateButton, javafx.scene.layout.Priority.ALWAYS);

        buttonBox.getChildren().addAll(favButton, rateButton);


        card.getChildren().addAll(imagePlaceholder, nameLabel, descLabel, priceBox, spacer, buttonBox);

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