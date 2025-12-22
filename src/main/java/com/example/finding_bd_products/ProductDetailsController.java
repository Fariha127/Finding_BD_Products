package com.example.finding_bd_products;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailsController {

    @FXML
    private Label productNameLabel;
    @FXML
    private Label productDescriptionLabel;
    @FXML
    private Label productPriceLabel;
    @FXML
    private Label productCategoryLabel;
    @FXML
    private Label recommendationCountLabel;
    @FXML
    private Label averageRatingLabel;
    @FXML
    private Button recommendButton;
    @FXML
    private TextArea reviewTextArea;
    @FXML
    private ComboBox<Integer> ratingComboBox;
    @FXML
    private TextField userNameField;
    @FXML
    private Button submitReviewButton;
    @FXML
    private VBox reviewsContainer;
    @FXML
    private Button homeBtn;
    @FXML
    private Button categoriesBtn;
    @FXML
    private Button newlyAddedBtn;
    @FXML
    private Button favouritesBtn;
    @FXML
    private Button backButton;

    @FXML
    protected void onBack() {
        loadPage("Home.fxml");
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
        loadPage("MyFavouriteProducts.fxml");
    }

    public static Product getProduct(String productId) {
        return productDatabase.get(productId);
    }

    public void setProduct(String productId) {
        this.currentProduct = productDatabase.get(productId);
        if (currentProduct != null) {
            displayProductDetails();
            loadReviews();
        }
    }

    public void setProduct(Product product) {
        this.currentProduct = product;
        displayProductDetails();
        loadReviews();
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
