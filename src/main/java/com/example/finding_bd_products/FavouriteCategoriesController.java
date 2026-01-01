package com.example.finding_bd_products;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class FavouriteCategoriesController {
    @FXML
    private GridPane categoriesGrid;

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
    private Button loginBtn;

    @FXML
    private Button signupBtn;

    private DatabaseManager dbManager;

    @FXML
    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        if (UserSession.getInstance().isLoggedIn()) {
            loadFavouriteCategories();
        } else {
            showEmptyState();
        }
        
        // Hide login/signup buttons if user or vendor is logged in
        if (UserSession.getInstance().isLoggedIn() || VendorSession.getInstance().isLoggedIn()) {
            if (loginBtn != null) {
                loginBtn.setVisible(false);
                loginBtn.setManaged(false);
            }
            if (signupBtn != null) {
                signupBtn.setVisible(false);
                signupBtn.setManaged(false);
            }
        }
    }

    private void showEmptyState() {
        categoriesGrid.getChildren().clear();
        Label emptyLabel = new Label("Please log in to view your favourite categories.");
        emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888888; -fx-padding: 50;");
        VBox emptyBox = new VBox(emptyLabel);
        emptyBox.setStyle("-fx-alignment: center;");
        categoriesGrid.add(emptyBox, 0, 0, 3, 1);
    }

    private void loadFavouriteCategories() {
        categoriesGrid.getChildren().clear();
        List<String> favouriteCategories = dbManager.getFavouriteCategories();

        if (favouriteCategories.isEmpty()) {
            Label emptyLabel = new Label("No favourite categories yet. Start adding categories to your favourites!");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888888; -fx-padding: 50;");
            VBox emptyBox = new VBox(emptyLabel);
            emptyBox.setStyle("-fx-alignment: center;");
            categoriesGrid.add(emptyBox, 0, 0, 3, 1);
            return;
        }

        int col = 0;
        int row = 0;
        for (String categoryName : favouriteCategories) {
            VBox card = createCategoryCard(categoryName);
            categoriesGrid.add(card, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createCategoryCard(String categoryName) {
        VBox card = new VBox(12);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefHeight(240);
        card.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; " +
                "-fx-border-width: 1; -fx-border-radius: 12; -fx-background-radius: 12; " +
                "-fx-padding: 20; -fx-cursor: hand;");

        card.setOnMouseClicked(event -> {
            if (event.getTarget() instanceof Button) {
                return; // Don't navigate if button was clicked
            }
            navigateToCategoryProducts(categoryName);
        });

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.12));
        card.setEffect(shadow);

        // Load category image
        ImageView imageView = new ImageView();
        imageView.setFitHeight(120);
        imageView.setFitWidth(180);
        imageView.setPreserveRatio(true);
        
        String imageUrl = getCategoryImageUrl(categoryName);
        if (imageUrl != null) {
            try {
                Image image = new Image(getClass().getResourceAsStream(imageUrl));
                imageView.setImage(image);
            } catch (Exception e) {
                imageView.setStyle("-fx-background-color: #F5F5F5;");
            }
        } else {
            imageView.setStyle("-fx-background-color: #F5F5F5;");
        }

        Label nameLabel = new Label(categoryName);
        nameLabel.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 18));
        nameLabel.setStyle("-fx-text-fill: #333333;");

        Label descLabel = new Label(getCategoryDescription(categoryName));
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
        descLabel.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label viewLabel = new Label("View Products →");
        viewLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");
        viewLabel.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 12));

        Button removeButton = new Button("♥ Remove");
        removeButton.setPrefHeight(35);
        removeButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; " +
                "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12px; " +
                "-fx-font-weight: bold;");
        removeButton.setOnAction(e -> {
            e.consume();
            dbManager.removeFromFavouriteCategories(categoryName);
            loadFavouriteCategories();
        });

        card.getChildren().addAll(imageView, nameLabel, descLabel, spacer, viewLabel, removeButton);

        return card;
    }

    private String getCategoryImageUrl(String categoryName) {
        return switch (categoryName) {
            case "Beverages" -> "/images/beverages-category.jpg";
            case "Hair Care" -> "/images/haircare-category.jpg";
            case "Oral Care" -> "/images/oralcare-category.jpg";
            case "Snacks" -> "/images/snacks-category.jpg";
            case "Food & Grocery" -> "/images/foodgrocery-category.jpg";
            case "Home Care" -> "/images/homecare-category.jpg";
            case "Skin Care" -> "/images/skincare-category.jpg";
            case "Baby Care" -> "/images/babycare-category.jpg";
            case "Dairy Products" -> "/images/dairy-category.jpg";
            default -> null;
        };
    }

    private String getCategoryDescription(String categoryName) {
        return switch (categoryName) {
            case "Beverages" -> "Energy Drinks, Soft Drinks, Juices";
            case "Hair Care" -> "Hair Shampoo, Hair Oil";
            case "Oral Care" -> "Toothpaste, Mouthwash";
            case "Snacks" -> "Biscuits, Chips, Chanachur";
            case "Food & Grocery" -> "Spices, Rice, Oil";
            case "Home Care" -> "Cleaning Products, Tissues";
            case "Skin Care" -> "Soap, Lotion, Face Wash";
            case "Baby Care" -> "Baby Products, Diapers";
            case "Dairy Products" -> "Milk, Ghee, Butter";
            default -> "Quality Bangladeshi Products";
        };
    }

    private void navigateToCategoryProducts(String categoryName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CategoryProducts.fxml"));
            Parent root = loader.load();

            CategoryProductsController controller = loader.getController();
            controller.setCategory(categoryName);

            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
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
        loadPage("NewProducts.fxml");
    }

    @FXML
    protected void showFavourites() {
        loadPage("MyFavouriteProducts.fxml");
    }

    @FXML
    protected void showFavouriteCategories() {
        // Already on this page, do nothing
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
        loadPage("Categories.fxml");
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
