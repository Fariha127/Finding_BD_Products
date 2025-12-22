package com.example.finding_bd_products;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:bd_products.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Products table
            String createProductsTable = """
                CREATE TABLE IF NOT EXISTS products (
                    product_id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    description TEXT,
                    price REAL NOT NULL,
                    unit TEXT,
                    category TEXT,
                    recommendation_count INTEGER DEFAULT 0
                )
                """;
            stmt.execute(createProductsTable);

            // Create Reviews table
            String createReviewsTable = """
                CREATE TABLE IF NOT EXISTS reviews (
                    review_id TEXT PRIMARY KEY,
                    product_id TEXT NOT NULL,
                    user_name TEXT NOT NULL,
                    comment TEXT,
                    rating INTEGER NOT NULL,
                    FOREIGN KEY (product_id) REFERENCES products(product_id)
                )
                """;
            stmt.execute(createReviewsTable);

            String createFavouritesTable = """
                CREATE TABLE IF NOT EXISTS favourites (
                    product_id TEXT PRIMARY KEY,
                    FOREIGN KEY (product_id) REFERENCES products(product_id)
                )
                """;
            stmt.execute(createFavouritesTable);

            // Check if products table is empty and seed data
            if (isProductsTableEmpty()) {
                seedInitialData();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isProductsTableEmpty() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return true;
    }

    private void seedInitialData() {
        // Insert products matching Home.fxml
        insertProduct("mojo", "Mojo", "Soft Drink", 25, "250ml", "Beverages");
        insertProduct("mediplus", "Mediplus DS", "Toothpaste", 85, "100g", "Oral Care");
        insertProduct("spa-water", "Spa Drinking Water", "Water", 20, "500ml", "Beverages");
        insertProduct("meril-soap", "Meril Milk Soap", "Moisturizing Soap", 35, "75g", "Personal Care");
        insertProduct("shejan-juice", "Shejan Mango Juice", "Mango Juice", 35, "200ml", "Beverages");
        insertProduct("pran-potata", "Pran Potata Spicy", "Biscuit", 40, "pack", "Snacks");
        insertProduct("ruchi-chanachur", "Ruchi BBQ Chanachur", "Snack", 30, "150g", "Snacks");
        insertProduct("bashundhara-towel", "Bashundhara Towel", "Hand Towel", 80, "pack", "Home Care");
        insertProduct("revive-lotion", "Revive Perfect Skin", "Moisturizing Lotion", 150, "100ml", "Personal Care");
        insertProduct("jui-oil", "Jui HairCare Oil", "Hair Oil", 95, "200ml", "Personal Care");
        insertProduct("radhuni-tumeric", "Radhuni Tumeric", "Powder", 55, "100g", "Food & Grocery");
        insertProduct("pran-ghee", "Pran Premium Ghee", "Cooking Ghee", 250, "500g", "Food & Grocery");

        // Add sample reviews
        insertReview("r1", "mojo", "Ahmed Khan", "Great energy drink! Very refreshing.", 5);
        insertReview("r2", "mojo", "Fatima Rahman", "Good taste but a bit sweet.", 4);
        updateRecommendationCount("mojo", 15);

        insertReview("r3", "meril-soap", "Sadia Islam", "Makes my skin very soft!", 5);
        updateRecommendationCount("meril-soap", 23);

        insertReview("r4", "shejan-juice", "Karim Hossain", "Love the mango flavor!", 5);
        updateRecommendationCount("shejan-juice", 18);
    }


    public void insertProduct(String productId, String name, String description, double price, String unit, String category) {
        String sql = "INSERT OR REPLACE INTO products (product_id, name, description, price, unit, category) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            pstmt.setString(2, name);
            pstmt.setString(3, description);
            pstmt.setDouble(4, price);
            pstmt.setString(5, unit);
            pstmt.setString(6, category);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Product getProduct(String productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Product product = new Product(
                        rs.getString("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("unit"),
                        rs.getString("category")
                );
                product.setRecommendationCount(rs.getInt("recommendation_count"));

                // Load reviews
                List<Review> reviews = getReviewsForProduct(productId);
                for (Review review : reviews) {
                    product.addReview(review);
                }

                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product product = new Product(
                        rs.getString("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("unit"),
                        rs.getString("category")
                );
                product.setRecommendationCount(rs.getInt("recommendation_count"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getString("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("unit"),
                        rs.getString("category")
                );
                product.setRecommendationCount(rs.getInt("recommendation_count"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public void updateRecommendationCount(String productId, int count) {
        String sql = "UPDATE products SET recommendation_count = ? WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, count);
            pstmt.setString(2, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementRecommendationCount(String productId) {
        String sql = "UPDATE products SET recommendation_count = recommendation_count + 1 WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Review operations
    public void insertReview(String reviewId, String productId, String userName, String comment, int rating) {
        String sql = "INSERT OR REPLACE INTO reviews (review_id, product_id, user_name, comment, rating) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reviewId);
            pstmt.setString(2, productId);
            pstmt.setString(3, userName);
            pstmt.setString(4, comment);
            pstmt.setInt(5, rating);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Review> getReviewsForProduct(String productId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                reviews.add(new Review(
                        rs.getString("review_id"),
                        rs.getString("product_id"),
                        rs.getString("user_name"),
                        rs.getString("comment"),
                        rs.getInt("rating")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }


    public void addToFavourites(String productId) {
        String sql = "INSERT OR IGNORE INTO favourites (product_id) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFromFavourites(String productId) {
        String sql = "DELETE FROM favourites WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getFavouriteProducts() {
        List<Product> favourites = new ArrayList<>();
        String sql = "SELECT p.* FROM products p INNER JOIN favourites f ON p.product_id = f.product_id";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product product = new Product(
                        rs.getString("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("unit"),
                        rs.getString("category")
                );
                product.setRecommendationCount(rs.getInt("recommendation_count"));
                favourites.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favourites;
    }

}
