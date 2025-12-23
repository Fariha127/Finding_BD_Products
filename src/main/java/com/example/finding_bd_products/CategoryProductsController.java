package com.example.finding_bd_products;

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
import java.util.List;

public class CategoryProductsController {
    @FXML
    private Label categoryTitle;

    @FXML
    private Label categoryDescription;

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

    private String currentCategory;
    private DatabaseManager dbManager;

    public void initialize() {
        dbManager = DatabaseManager.getInstance();
    }

    public void setCategory(String categoryName) {
        this.currentCategory = categoryName;
        categoryTitle.setText(categoryName);
        categoryDescription.setText("Showing all products in " + categoryName);
        loadProducts(categoryName);
    }

    private void loadProducts(String categoryName) {
        productsGrid.getChildren().clear();

        List<Product> products = dbManager.getProductsByCategory(categoryName);

        if (products == null || products.isEmpty()) {
            Label noProducts = new Label("No products available in this category");
            noProducts.setStyle("-fx-font-size: 16px; -fx-text-fill: #888888;");
            productsGrid.add(noProducts, 0, 0);
            return;
        }

        int col = 0;
        int row = 0;

        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productsGrid.add(productCard, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setPrefSize(220, 280);
        card.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; " +
                "-fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-padding: 15; -fx-cursor: hand;");

        // Make card clickable
        card.setOnMouseClicked(event -> navigateToProductDetails(product.getProductId()));

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(2.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.08));
        card.setEffect(shadow);

        // Product image placeholder
        Region imagePlaceholder = new Region();
        imagePlaceholder.setPrefSize(190, 140);
        imagePlaceholder.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 8;");

        // Product name
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 15));
        nameLabel.setStyle("-fx-text-fill: #333333;");

        // Product description
        Label descLabel = new Label(product.getDescription());
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");

        // Price
        HBox priceBox = new HBox(5);
        Label priceLabel = new Label("৳ " + (int)product.getPrice());
        priceLabel.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 16));
        priceLabel.setStyle("-fx-text-fill: #D32F2F;");
        Label unitLabel = new Label("/" + product.getUnit());
        unitLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");
        priceBox.getChildren().addAll(priceLabel, unitLabel);

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Buttons
        HBox buttonBox = new HBox(8);
        Button favButton = new Button("♡");
        favButton.setPrefSize(45, 32);
        favButton.setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #D32F2F; " +
                "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px;");
        favButton.setOnAction(e -> {
            e.consume();
            System.out.println("Added to favourites: " + product.getName());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductDetails.fxml"));
            Parent root = loader.load();

            ProductDetailsController controller = loader.getController();
            controller.setProduct(productId);

            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
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
        loadPage("NewlyAdded.fxml");
    }

    @FXML
    protected void showFavourites() {
        loadPage("Favourites.fxml");
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
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
