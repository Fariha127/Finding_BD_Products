package com.example.finding_bd_products;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AdminDashboardController {

    @FXML private TabPane mainTabPane;

    // Users Table
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> userIdColumn;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userPhoneColumn;
    @FXML private TableColumn<User, String> userCityColumn;

    // Company Vendors Table
    @FXML private TableView<CompanyVendor> companyVendorsTable;
    @FXML private TableColumn<CompanyVendor, String> companyIdColumn;
    @FXML private TableColumn<CompanyVendor, String> companyNameColumn;
    @FXML private TableColumn<CompanyVendor, String> companyContactColumn;
    @FXML private TableColumn<CompanyVendor, String> companyEmailColumn;
    @FXML private TableColumn<CompanyVendor, String> companyPhoneColumn;
    @FXML private TableColumn<CompanyVendor, String> companyStatusColumn;
    @FXML private TableColumn<CompanyVendor, Void> companyActionColumn;

    // Retail Vendors Table
    @FXML private TableView<RetailVendor> retailVendorsTable;
    @FXML private TableColumn<RetailVendor, String> retailIdColumn;
    @FXML private TableColumn<RetailVendor, String> retailShopColumn;
    @FXML private TableColumn<RetailVendor, String> retailOwnerColumn;
    @FXML private TableColumn<RetailVendor, String> retailEmailColumn;
    @FXML private TableColumn<RetailVendor, String> retailPhoneColumn;
    @FXML private TableColumn<RetailVendor, String> retailStatusColumn;
    @FXML private TableColumn<RetailVendor, Void> retailActionColumn;

    // Products Table
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> productIdColumn;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, String> productDescColumn;
    @FXML private TableColumn<Product, String> productPriceColumn;
    @FXML private TableColumn<Product, String> productUnitColumn;
    @FXML private TableColumn<Product, String> productCategoryColumn;
    @FXML private TableColumn<Product, String> productVendorColumn;
    @FXML private TableColumn<Product, Void> productActionColumn;

    private DatabaseManager dbManager;

    @FXML
    public void initialize() {
        dbManager = DatabaseManager.getInstance();
        
        setupUsersTable();
        setupCompanyVendorsTable();
        setupRetailVendorsTable();
        setupProductsTable();
        
        loadAllData();
    }

    private void setupUsersTable() {
        userIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserId()));
        userNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        userEmailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        userPhoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhoneNumber()));
        userCityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCity()));
    }

    private void setupCompanyVendorsTable() {
        companyIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVendorId()));
        companyNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCompanyName()));
        companyContactColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        companyEmailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        companyPhoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhoneNumber()));
        companyStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccountStatus()));
        
        companyActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox buttons = new HBox(5, approveBtn, rejectBtn);

            {
                approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 10;");
                rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 5 10;");
                
                approveBtn.setOnAction(event -> {
                    CompanyVendor vendor = getTableView().getItems().get(getIndex());
                    handleApproveCompanyVendor(vendor);
                });
                
                rejectBtn.setOnAction(event -> {
                    CompanyVendor vendor = getTableView().getItems().get(getIndex());
                    handleRejectCompanyVendor(vendor);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CompanyVendor vendor = getTableView().getItems().get(getIndex());
                    if ("pending".equals(vendor.getAccountStatus())) {
                        setGraphic(buttons);
                    } else {
                        Label statusLabel = new Label(vendor.getAccountStatus().toUpperCase());
                        statusLabel.setStyle("-fx-font-weight: bold;");
                        setGraphic(statusLabel);
                    }
                }
            }
        });
    }

    private void setupRetailVendorsTable() {
        retailIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVendorId()));
        retailShopColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getShopName()));
        retailOwnerColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOwnerName()));
        retailEmailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        retailPhoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhoneNumber()));
        retailStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccountStatus()));
        
        retailActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox buttons = new HBox(5, approveBtn, rejectBtn);

            {
                approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 10;");
                rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 5 10;");
                
                approveBtn.setOnAction(event -> {
                    RetailVendor vendor = getTableView().getItems().get(getIndex());
                    handleApproveRetailVendor(vendor);
                });
                
                rejectBtn.setOnAction(event -> {
                    RetailVendor vendor = getTableView().getItems().get(getIndex());
                    handleRejectRetailVendor(vendor);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RetailVendor vendor = getTableView().getItems().get(getIndex());
                    if ("pending".equals(vendor.getAccountStatus())) {
                        setGraphic(buttons);
                    } else {
                        Label statusLabel = new Label(vendor.getAccountStatus().toUpperCase());
                        statusLabel.setStyle("-fx-font-weight: bold;");
                        setGraphic(statusLabel);
                    }
                }
            }
        });
    }

    private void setupProductsTable() {
        productIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductId()));
        productNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        productDescColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        productPriceColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getPrice())));
        productUnitColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUnit()));
        productCategoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        productVendorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVendorId()));
        
        productActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox buttons = new HBox(5, approveBtn, rejectBtn);

            {
                approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 10;");
                rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 5 10;");
                
                approveBtn.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    handleApproveProduct(product);
                });
                
                rejectBtn.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    handleRejectProduct(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadAllData() {
        refreshUsers();
        refreshCompanyVendors();
        refreshRetailVendors();
        refreshProducts();
    }

    @FXML
    private void refreshUsers() {
        ObservableList<User> users = FXCollections.observableArrayList(dbManager.getAllUsers());
        usersTable.setItems(users);
    }

    @FXML
    private void refreshCompanyVendors() {
        ObservableList<CompanyVendor> vendors = FXCollections.observableArrayList(dbManager.getAllCompanyVendors());
        companyVendorsTable.setItems(vendors);
    }

    @FXML
    private void refreshRetailVendors() {
        ObservableList<RetailVendor> vendors = FXCollections.observableArrayList(dbManager.getAllRetailVendors());
        retailVendorsTable.setItems(vendors);
    }

    @FXML
    private void refreshProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList(dbManager.getPendingProducts());
        productsTable.setItems(products);
    }

    private void handleApproveCompanyVendor(CompanyVendor vendor) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Approval");
        confirmAlert.setHeaderText("Approve Company Vendor");
        confirmAlert.setContentText("Are you sure you want to approve " + vendor.getCompanyName() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dbManager.approveCompanyVendor(vendor.getVendorId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Company vendor approved successfully!");
                    refreshCompanyVendors();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve vendor.");
                }
            }
        });
    }

    private void handleRejectCompanyVendor(CompanyVendor vendor) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Rejection");
        confirmAlert.setHeaderText("Reject Company Vendor");
        confirmAlert.setContentText("Are you sure you want to reject " + vendor.getCompanyName() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dbManager.rejectCompanyVendor(vendor.getVendorId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Company vendor rejected.");
                    refreshCompanyVendors();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject vendor.");
                }
            }
        });
    }

    private void handleApproveRetailVendor(RetailVendor vendor) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Approval");
        confirmAlert.setHeaderText("Approve Retail Vendor");
        confirmAlert.setContentText("Are you sure you want to approve " + vendor.getShopName() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dbManager.approveRetailVendor(vendor.getVendorId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Retail vendor approved successfully!");
                    refreshRetailVendors();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve vendor.");
                }
            }
        });
    }

    private void handleRejectRetailVendor(RetailVendor vendor) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Rejection");
        confirmAlert.setHeaderText("Reject Retail Vendor");
        confirmAlert.setContentText("Are you sure you want to reject " + vendor.getShopName() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dbManager.rejectRetailVendor(vendor.getVendorId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Retail vendor rejected.");
                    refreshRetailVendors();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject vendor.");
                }
            }
        });
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 700));
            stage.setTitle("Deshi Store - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to logout: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
