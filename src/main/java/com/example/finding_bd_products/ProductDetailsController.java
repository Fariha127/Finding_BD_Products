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

import java.io.IOException;import java.time.format.DateTimeFormatter;import java.util.UUID;

public class ProductDetailsController {

    @FXML private Label productNameLabel;
    @FXML private Label productDescriptionLabel;
    @FXML private Label productPriceLabel;
    @FXML private Label productCategoryLabel;
    @FXML private Label recommendationCountLabel;
    @FXML private Label averageRatingLabel;
    @FXML private Button recommendButton;
    @FXML private TextArea reviewTextArea;
    @FXML private ComboBox<Integer> ratingComboBox;
    @FXML private TextField userNameField;
    @FXML private Button submitReviewButton;
    @FXML private VBox reviewsContainer;
    @FXML private Button homeBtn;
    @FXML private Button categoriesBtn;
    @FXML private Button newlyAddedBtn;
    @FXML private Button favouritesBtn;
    @FXML private Button backButton;

    private Product currentProduct;
    private boolean hasRecommended = false;
    private DatabaseManager dbManager;

    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        ratingComboBox.getItems().addAll(5, 4, 3, 2, 1);
        ratingComboBox.setValue(5);
    }

    public void setProduct(String productId) {
        this.currentProduct = dbManager.getProduct(productId);
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

    public static Product getProduct(String productId) {
        return DatabaseManager.getInstance().getProduct(productId);
    }

    private void displayProductDetails() {
        productNameLabel.setText(currentProduct.getName());
        productDescriptionLabel.setText(currentProduct.getDescription());
        productPriceLabel.setText("৳ " + (int)currentProduct.getPrice() + "/" + currentProduct.getUnit());
        productCategoryLabel.setText(currentProduct.getCategory());
        recommendationCountLabel.setText(currentProduct.getRecommendationCount() + " Recommendations");

        if (currentProduct.getReviews().isEmpty()) {
            averageRatingLabel.setText("No ratings yet");
        } else {
            averageRatingLabel.setText(String.format("%.1f ★ (%d reviews)",
                    currentProduct.getAverageRating(), currentProduct.getReviews().size()));
        }
    }

    @FXML
    protected void onRecommendClick() {
        if (!hasRecommended) {
            currentProduct.addRecommendation();

            dbManager.incrementRecommendationCount(currentProduct.getProductId());

            recommendationCountLabel.setText(currentProduct.getRecommendationCount() + " Recommendations");
            recommendButton.setText("✓ Recommended");
            recommendButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                    "-fx-background-radius: 20; -fx-font-weight: bold;");
            hasRecommended = true;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thank you!");
            alert.setHeaderText(null);
            alert.setContentText("You recommended this product!");
            alert.showAndWait();
        }
    }

    @FXML
    protected void onSubmitReview() {
        String userName = userNameField.getText().trim();
        String comment = reviewTextArea.getText().trim();
        Integer rating = ratingComboBox.getValue();

        if (userName.isEmpty() || comment.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete Review");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your name and review comment.");
            alert.showAndWait();
            return;
        }

        String reviewId = "r" + UUID.randomUUID().toString();
        Review review = new Review(reviewId, currentProduct.getProductId(), userName, comment, rating);

        // Save to database
        dbManager.insertReview(reviewId, currentProduct.getProductId(), userName, comment, rating);
        currentProduct.addReview(review);

        displayProductDetails();
        addReviewToUI(review);

        userNameField.clear();
        reviewTextArea.clear();
        ratingComboBox.setValue(5);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Review Submitted");
        alert.setHeaderText(null);
        alert.setContentText("Thank you for your review!");
        alert.showAndWait();
    }

    private void loadReviews() {
        reviewsContainer.getChildren().clear();

        if (currentProduct.getReviews().isEmpty()) {
            Label noReviews = new Label("No reviews yet. Be the first to review this product!");
            noReviews.setStyle("-fx-font-size: 14px; -fx-text-fill: #888888; -fx-padding: 20;");
            reviewsContainer.getChildren().add(noReviews);
        } else {
            for (Review review : currentProduct.getReviews()) {
                addReviewToUI(review);
            }
        }
    }

    private void addReviewToUI(Review review) {
        VBox reviewCard = new VBox(8);
        reviewCard.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; " +
                "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; " +
                "-fx-padding: 15;");
        reviewCard.setPrefWidth(700);


        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label nameLabel = new Label(review.getUserName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setStyle("-fx-text-fill: #333333;");


        Label ratingLabel = new Label(getStars(review.getRating()));
        ratingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFA000;");

        header.getChildren().addAll(nameLabel, ratingLabel);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        Label dateLabel = new Label(review.getDatePosted().format(formatter));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");


        Label commentLabel = new Label(review.getComment());
        commentLabel.setWrapText(true);
        commentLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        reviewCard.getChildren().addAll(header, dateLabel, commentLabel);
        reviewsContainer.getChildren().add(reviewCard);


        VBox.setMargin(reviewCard, new Insets(0, 0, 10, 0));
    }

    private String getStars(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("★");
        }
        for (int i = rating; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }

    @FXML
    protected void onAddToFavourites() {
        // Add to database
        dbManager.addToFavourites(currentProduct.getProductId());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Added to Favourites");
        alert.setHeaderText(null);
        alert.setContentText(currentProduct.getName() + " added to favourites!");
        alert.showAndWait();
    }

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