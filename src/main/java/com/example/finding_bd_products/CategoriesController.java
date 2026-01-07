package com.example.finding_bd_products;

import com.example.finding_bd_products.CategoryProductsController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CategoriesController {
    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

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
    private Button addProductBtn;

    @FXML
    private Button myProfileBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private Button signupBtn;

    private DatabaseManager dbManager;

    @FXML
    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        addFavoriteButtons();
        
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

    private void addFavoriteButtons() {
        // Find all category VBox cards and add favorite buttons to them
        Platform.runLater(() -> {
            try {
                javafx.scene.layout.GridPane gridPane = (javafx.scene.layout.GridPane) categoriesBtn.getScene().lookup("#categoriesGrid");
                if (gridPane != null) {
                    for (javafx.scene.Node node : gridPane.getChildren()) {
                        if (node instanceof VBox) {
                            VBox categoryCard = (VBox) node;
                            addFavoriteButtonToCard(categoryCard);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void addFavoriteButtonToCard(VBox categoryCard) {
        try {
            // Get the category name from the card
            Label nameLabel = (Label) categoryCard.getChildren().get(1);
            String categoryName = nameLabel.getText();

            // Remove the last label (product count) and add buttons instead
            categoryCard.getChildren().remove(categoryCard.getChildren().size() - 1);

            // Create buttons container
            HBox buttonBox = new HBox(8);
            buttonBox.setAlignment(Pos.CENTER);
            
            // Create favorite button
            Button favButton = new Button("♡");
            favButton.setPrefSize(35, 32);
            
            // Check if category is favourite and set initial style (only if logged in)
            boolean isFav = UserSession.getInstance().isLoggedIn() && dbManager.isFavouriteCategory(categoryName);
            if (isFav) {
                favButton.setText("♥");
                favButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px;");
            } else {
                favButton.setStyle("-fx-background-color: #FFE5E5; -fx-text-fill: #D32F2F; " +
                        "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px;");
            }
            
            favButton.setOnAction(e -> {
                e.consume();
                if (!UserSession.getInstance().isLoggedIn()) {
                    showLoginAlert();
                    return;
                }
                boolean currentlyFav = dbManager.isFavouriteCategory(categoryName);
                if (currentlyFav) {
                    dbManager.removeFromFavouriteCategories(categoryName);
                    favButton.setText("♡");
                    favButton.setStyle("-fx-background-color: #FFE5E5; -fx-text-fill: #D32F2F; " +
                            "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px;");
                } else {
                    dbManager.addToFavouriteCategories(categoryName);
                    favButton.setText("♥");
                    favButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; " +
                            "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 14px;");
                }
            });

            // Create view button
            Button viewButton = new Button("View");
            viewButton.setPrefHeight(32);
            viewButton.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; " +
                    "-fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12px; " +
                    "-fx-font-weight: bold;");
            HBox.setHgrow(viewButton, javafx.scene.layout.Priority.ALWAYS);
            viewButton.setMaxWidth(Double.MAX_VALUE);
            viewButton.setOnAction(e -> {
                e.consume();
                loadCategoryProducts(categoryName);
            });

            buttonBox.getChildren().addAll(favButton, viewButton);
            categoryCard.getChildren().add(buttonBox);
            
        } catch (Exception e) {
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
    protected void goToLogin() {
        loadPage("Login.fxml");
    }

    @FXML
    protected void goToSignup() {
        loadPage("SignupUser.fxml");
    }

    @FXML
    protected void showCategories() {
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
        loadPage("FavouriteCategories.fxml");
    }

    @FXML
    protected void goToAddProduct() {
        if (!VendorSession.getInstance().isLoggedIn()) {
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
        loadPage("MyProfile.fxml");
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

    @FXML
    protected void onSearch() {
        String searchText = searchField.getText();
    }

    @FXML
    public void showCategoryProducts(javafx.event.ActionEvent event) {
        VBox categoryCard = (VBox) ((javafx.scene.Node) event.getSource()).getParent();
        String categoryName = getCategoryNameFromCard(categoryCard);

        loadCategoryProducts(categoryName);
    }

    public void onCategoryClick(javafx.scene.input.MouseEvent event) {
        VBox categoryCard = (VBox) event.getSource();
        String categoryName = getCategoryNameFromCard(categoryCard);
        loadCategoryProducts(categoryName);
    }

    private String getCategoryNameFromCard(VBox categoryCard) {

        try {
            javafx.scene.control.Label nameLabel = (javafx.scene.control.Label) categoryCard.getChildren().get(1);
            return nameLabel.getText();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private void loadCategoryProducts(String categoryName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CategoryProducts.fxml"));
            Parent root = loader.load();

            CategoryProductsController controller = loader.getController();
            controller.setCategory(categoryName);

            Stage stage = (Stage) categoriesBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) categoriesBtn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showLoginAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Login Required");
        alert.setHeaderText("You need to log in");
        alert.setContentText("Please log in to add categories to favorites.");
        alert.showAndWait();
    }
}
