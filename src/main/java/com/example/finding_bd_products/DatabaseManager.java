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


            String createProductsTable = """
                CREATE TABLE IF NOT EXISTS products (
                    product_id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    description TEXT,
                    price REAL NOT NULL,
                    unit TEXT,
                    category TEXT,
                    image_url TEXT,
                    recommendation_count INTEGER DEFAULT 0
                )
                """;
            stmt.execute(createProductsTable);


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

            String createFavouriteCategoriesTable = """
                CREATE TABLE IF NOT EXISTS favourite_categories (
                    category_name TEXT PRIMARY KEY
                )
                """;
            stmt.execute(createFavouriteCategoriesTable);

            // Users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id TEXT PRIMARY KEY,
                    full_name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    phone_number TEXT NOT NULL,
                    date_of_birth TEXT,
                    gender TEXT,
                    city TEXT,
                    profile_picture TEXT,
                    account_status TEXT DEFAULT 'pending',
                    user_type TEXT DEFAULT 'user'
                )
                """;
            stmt.execute(createUsersTable);

            // Company Vendors table
            String createCompanyVendorsTable = """
                CREATE TABLE IF NOT EXISTS company_vendors (
                    vendor_id TEXT PRIMARY KEY,
                    full_name TEXT NOT NULL,
                    designation TEXT NOT NULL,
                    company_name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    phone_number TEXT NOT NULL,
                    company_registration_number TEXT,
                    bsti_certificate_number TEXT,
                    company_address TEXT NOT NULL,
                    tin_number TEXT,
                    company_logo TEXT,
                    account_status TEXT DEFAULT 'pending'
                )
                """;
            stmt.execute(createCompanyVendorsTable);

            // Retail Vendors table
            String createRetailVendorsTable = """
                CREATE TABLE IF NOT EXISTS retail_vendors (
                    vendor_id TEXT PRIMARY KEY,
                    owner_name TEXT NOT NULL,
                    shop_name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    phone_number TEXT NOT NULL,
                    business_registration_number TEXT,
                    trade_license_number TEXT,
                    shop_address TEXT NOT NULL,
                    tin_number TEXT,
                    shop_logo TEXT,
                    account_status TEXT DEFAULT 'pending'
                )
                """;
            stmt.execute(createRetailVendorsTable);

            // Admin table
            String createAdminTable = """
                CREATE TABLE IF NOT EXISTS admins (
                    admin_id TEXT PRIMARY KEY,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
                """;
            stmt.execute(createAdminTable);

            // Create default admin if not exists
            createDefaultAdmin();

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
        
        insertProduct("mojo", "Mojo", "Soft Drink", 25, "250ml", "Beverages", "/images/mojo.jpg");
        insertProduct("mediplus", "Mediplus DS", "Toothpaste", 85, "100g", "Oral Care", "/images/mediplus.jpg");
        insertProduct("spa-water", "Spa Drinking Water", "Water", 20, "500ml", "Beverages", "/images/spa-water.jpg");
        insertProduct("meril-soap", "Meril Milk Soap", "Moisturizing Soap", 35, "75g", "Skin Care", "/images/meril-soap.jpg");
        insertProduct("shezan-juice", "Shezan Mango Juice", "Mango Juice", 35, "200ml", "Beverages", "/images/shezan-juice.jpg");
        insertProduct("pran-potata", "Pran Potata Spicy", "Biscuit", 40, "pack", "Snacks", "/images/pran-potata.jpg");
        insertProduct("ruchi-chanachur", "Ruchi BBQ Chanachur", "Snack", 30, "150g", "Snacks", "/images/ruchi-chanachur.jpg");
        insertProduct("bashundhara-towel", "Bashundhara Towel", "Hand Towel", 80, "pack", "Home Care", "/images/bashundhara-towel.jpg");
        insertProduct("revive-lotion", "Revive Perfect Skin", "Moisturizing Lotion", 150, "100ml", "Skin Care", "/images/revive-lotion.jpg");
        insertProduct("jui-oil", "Jui HairCare Oil", "Hair Oil", 95, "200ml", "Hair Care", "/images/jui-oil.jpg");
        insertProduct("radhuni-tumeric", "Radhuni Tumeric", "Powder", 55, "100g", "Food & Grocery", "/images/radhuni-tumeric.jpg");
        insertProduct("pran-ghee", "Pran Premium Ghee", "Cooking Ghee", 250, "500g", "Food & Grocery", "/images/pran-ghee.jpg");


        insertReview("r1", "mojo", "Ahmed Khan", "Great energy drink! Very refreshing.", 5);
        insertReview("r2", "mojo", "Fatima Rahman", "Good taste but a bit sweet.", 4);
        updateRecommendationCount("mojo", 15);

        insertReview("r3", "meril-soap", "Sadia Islam", "Makes my skin very soft!", 5);
        updateRecommendationCount("meril-soap", 23);

        insertReview("r4", "shezan-juice", "Karim Hossain", "Love the mango flavor!", 5);
        updateRecommendationCount("shezan-juice", 18);
    }


    public void insertProduct(String productId, String name, String description, double price, String unit, String category, String imageUrl) {
        String sql = "INSERT OR REPLACE INTO products (product_id, name, description, price, unit, category, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            pstmt.setString(2, name);
            pstmt.setString(3, description);
            pstmt.setDouble(4, price);
            pstmt.setString(5, unit);
            pstmt.setString(6, category);
            pstmt.setString(7, imageUrl);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Backward compatibility method without imageUrl
    public void insertProduct(String productId, String name, String description, double price, String unit, String category) {
        insertProduct(productId, name, description, price, unit, category, null);
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
                        rs.getString("category"),
                        rs.getString("image_url")
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
                        rs.getString("category"),
                        rs.getString("image_url")
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
                        rs.getString("category"),
                        rs.getString("image_url")
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

    public void decrementRecommendationCount(String productId) {
        String sql = "UPDATE products SET recommendation_count = recommendation_count - 1 WHERE product_id = ? AND recommendation_count > 0";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Review related methods
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

    public boolean isFavourite(String productId) {
        String sql = "SELECT COUNT(*) FROM favourites WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
                        rs.getString("category"),
                        rs.getString("image_url")
                );
                product.setRecommendationCount(rs.getInt("recommendation_count"));
                favourites.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favourites;
    }

    // ============ Favourite Categories Methods ============

    public void addToFavouriteCategories(String categoryName) {
        String sql = "INSERT OR IGNORE INTO favourite_categories (category_name) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFromFavouriteCategories(String categoryName) {
        String sql = "DELETE FROM favourite_categories WHERE category_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isFavouriteCategory(String categoryName) {
        String sql = "SELECT COUNT(*) FROM favourite_categories WHERE category_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getFavouriteCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT category_name FROM favourite_categories ORDER BY category_name";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // ============ Authentication Methods ============

    private void createDefaultAdmin() {
        String checkSql = "SELECT COUNT(*) FROM admins";
        String insertSql = "INSERT INTO admins (admin_id, email, password) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                // No admin exists, create default one
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, "admin001");
                    pstmt.setString(2, "admin@findingbd.com");
                    pstmt.setString(3, "admin123"); // In production, this should be hashed
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Admin login
    public boolean authenticateAdmin(String email, String password) {
        String sql = "SELECT * FROM admins WHERE email = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // User Registration
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (user_id, full_name, email, password, phone_number, date_of_birth, gender, city, account_status, user_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getPhoneNumber());
            pstmt.setString(6, user.getDateOfBirth());
            pstmt.setString(7, user.getGender());
            pstmt.setString(8, user.getCity());
            pstmt.setString(9, "pending");
            pstmt.setString(10, "user");
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // User Login
    public User authenticateUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND account_status = 'approved'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("user_id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("phone_number"),
                    rs.getString("date_of_birth"),
                    rs.getString("gender"),
                    rs.getString("user_type"),
                    rs.getString("account_status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Company Vendor Registration
    public boolean registerCompanyVendor(CompanyVendor vendor) {
        String sql = "INSERT INTO company_vendors (vendor_id, full_name, designation, company_name, email, password, phone_number, company_registration_number, bsti_certificate_number, company_address, tin_number, account_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vendor.getVendorId());
            pstmt.setString(2, vendor.getFullName());
            pstmt.setString(3, vendor.getDesignation());
            pstmt.setString(4, vendor.getCompanyName());
            pstmt.setString(5, vendor.getEmail());
            pstmt.setString(6, vendor.getPassword());
            pstmt.setString(7, vendor.getPhoneNumber());
            pstmt.setString(8, vendor.getCompanyRegistrationNumber());
            pstmt.setString(9, vendor.getBstiCertificateNumber());
            pstmt.setString(10, vendor.getCompanyAddress());
            pstmt.setString(11, vendor.getTinNumber());
            pstmt.setString(12, "pending");
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Company Vendor Login
    public CompanyVendor authenticateCompanyVendor(String email, String password) {
        String sql = "SELECT * FROM company_vendors WHERE email = ? AND password = ? AND account_status = 'approved'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new CompanyVendor(
                    rs.getString("vendor_id"),
                    rs.getString("full_name"),
                    rs.getString("designation"),
                    rs.getString("company_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("phone_number"),
                    rs.getString("company_registration_number"),
                    rs.getString("bsti_certificate_number"),
                    rs.getString("company_address"),
                    rs.getString("tin_number"),
                    rs.getString("account_status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retail Vendor Registration
    public boolean registerRetailVendor(RetailVendor vendor) {
        String sql = "INSERT INTO retail_vendors (vendor_id, owner_name, shop_name, email, password, phone_number, business_registration_number, trade_license_number, shop_address, tin_number, account_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vendor.getVendorId());
            pstmt.setString(2, vendor.getOwnerName());
            pstmt.setString(3, vendor.getShopName());
            pstmt.setString(4, vendor.getEmail());
            pstmt.setString(5, vendor.getPassword());
            pstmt.setString(6, vendor.getPhoneNumber());
            pstmt.setString(7, vendor.getBusinessRegistrationNumber());
            pstmt.setString(8, vendor.getTradeLicenseNumber());
            pstmt.setString(9, vendor.getShopAddress());
            pstmt.setString(10, vendor.getTinNumber());
            pstmt.setString(11, "pending");
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Retail Vendor Login
    public RetailVendor authenticateRetailVendor(String email, String password) {
        String sql = "SELECT * FROM retail_vendors WHERE email = ? AND password = ? AND account_status = 'approved'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new RetailVendor(
                    rs.getString("vendor_id"),
                    rs.getString("owner_name"),
                    rs.getString("shop_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("phone_number"),
                    rs.getString("business_registration_number"),
                    rs.getString("trade_license_number"),
                    rs.getString("shop_address"),
                    rs.getString("tin_number"),
                    rs.getString("account_status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
