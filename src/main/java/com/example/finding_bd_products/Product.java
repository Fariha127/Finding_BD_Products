package com.example.finding_bd_products;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private String productId;
    private String name;
    private String description;
    private double price;
    private String unit;
    private String category;
    private int recommendationCount;
    private List<Review> reviews;
    private double averageRating;

    public Product(String productId, String name, String description, double price, String unit, String category) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.unit = unit;
        this.category = category;
        this.recommendationCount = 0;
        this.reviews = new ArrayList<>();
        this.averageRating = 0.0;
    }

    public void addRecommendation() {
        this.recommendationCount++;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
        calculateAverageRating();
    }

    private void calculateAverageRating() {
        if (reviews.isEmpty()) {
            averageRating = 0.0;
            return;
        }
        double sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        averageRating = sum / reviews.size();
    }

    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getRecommendationCount() { return recommendationCount; }
    public void setRecommendationCount(int recommendationCount) { this.recommendationCount = recommendationCount; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public double getAverageRating() { return averageRating; }
}
