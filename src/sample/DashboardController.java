package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    Scene fxmlFile;
    Parent root;
    Stage window;
    JdbcDao jdbc;

    @FXML
    private Label lbDate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void Logout(ActionEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
        Scene login_scene = new Scene(FXMLLoader.load(getClass().getResource("Login.fxml")));
        stage.setTitle("Order App | Login");
        stage.setScene(login_scene);
        stage.show();
    }

    @FXML
    void cancelOrder() {

    }

    @FXML
    void drinkProducts(ActionEvent event) {

    }

    @FXML
    void dessertProducts(ActionEvent event) {

    }

    @FXML
    void mealProducts(ActionEvent event) {

    }

    @FXML
    void snackProducts(ActionEvent event) {

    }

    @FXML
    void otherProducts(ActionEvent event) {

    }

    @FXML
    void manageTable() {
        try {
            openModalWindow("Tables.fxml", "Manage Tables");
        } catch (Exception e) {
            showAlertConnection();
        }
    }

    @FXML
    void manageProduct() {

    }

    @FXML
    void manageOrder() {

    }

    @FXML
    void payment() {

    }


    @FXML
    void newOrder() {

    }

    @FXML
    void salesReport() {

    }


    private void openModalWindow(String resource, String title) throws IOException {
        root = FXMLLoader.load(getClass().getResource(resource));
        fxmlFile = new Scene(root);
        window = new Stage();
        window.setScene(fxmlFile);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setAlwaysOnTop(true);
        window.setIconified(false);
        window.setResizable(false);
        window.setTitle(title);
        window.showAndWait();
    }

    private void showAlertConnection() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Connection Status:");
        alert.setContentText("Failed to connect to sever");
        Window window = lbDate.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }



}
