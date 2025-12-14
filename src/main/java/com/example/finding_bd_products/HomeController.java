package com.example.finding_bd_products;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
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
    protected void showHome() {
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
            System.out.println("Error loading page: " + fxmlFile);
            System.out.println("Error message: " + e.getMessage());
        }
    }
}