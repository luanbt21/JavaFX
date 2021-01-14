package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

public class CategoryController implements Initializable {

    @FXML
    private TextField tfCategory;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private TableView<Category> tableCategory;
    @FXML
    private TableColumn<Category, Integer> colId;
    @FXML
    private TableColumn<Category, String> colCategory;

    JdbcDao jdbc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListenerForCategory();
        jdbc = new JdbcDao();
        showCategory();
    }

    @FXML
    void deleteEntry(ActionEvent event) {
        try{
            Category category = tableCategory.getSelectionModel().getSelectedItem();
            String categoryName = category.getName();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete category");
            alert.setHeaderText("Are you sure want to delete this category?");
            alert.setContentText(categoryName);
            Window window = tableCategory.getScene().getWindow();
            alert.initOwner(window);
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == null) {
            } else if (option.get() == ButtonType.OK) {
                String query = "DELETE FROM categories WHERE id = '" + category.getId() + "'";
                executeQuery(query);
                showAlertDelete(event);
                showCategory();
            } else if (option.get() == ButtonType.CANCEL) {
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void editEntry(ActionEvent event) {
        try{
            Category category = tableCategory.getSelectionModel().getSelectedItem();
            String query = "UPDATE products SET category = '" + tfCategory.getText()  +"' WHERE category ='" + category.getName() +"'";
            executeQuery(query);
            query = "UPDATE categories SET name = '" + tfCategory.getText() + "' WHERE id = '" + category.getId() + "'";
            executeQuery(query);
            showAlertUpdate(event);
            showCategory();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void saveTable(ActionEvent event) {
        insertRecord(event);
    }

    public void showCategory(){
        ObservableList<Category> list = getCategoryList();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableCategory.setItems(list);
    }

    private void insertRecord(ActionEvent event){
        String name = tfCategory.getText();
        if(!name.isEmpty()){
            String query = "INSERT INTO categories (name) VALUES ('" + name + "')";
            executeQuery(query);
            showAlertInsert(event);
            tfCategory.setText("");
            showCategory();
        }
    }

    private ObservableList<Category> getCategoryList() {
        ObservableList<Category> categoryList = FXCollections.observableArrayList();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM categories";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Category category;
            while (rs.next()){
                category = new Category(rs.getInt("id"), rs.getString("name"));
                categoryList.add(category);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return categoryList;
    }

    private void executeQuery(String query) {
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        Statement st;
        try{
            st = connection.createStatement();
            st.executeUpdate(query);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void addListenerForCategory(){
        tableCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection , newSelection) -> {
            if(newSelection != null){
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                tfCategory.setText(newSelection.getName());
                btnSave.setDisable(true);
            }
            else{
                tfCategory.setText("");
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
                btnSave.setDisable(false);
            }
        });
    }

    private void showAlertConnection() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Connection Status:");
        alert.setContentText("Failed to connect to sever");
        Window window = tableCategory.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

    private void showAlertInsert(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Insert category");
        alert.setHeaderText(null);
        alert.setContentText("Insert category sucessfully");
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void showAlertUpdate(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update category");
        alert.setHeaderText(null);
        alert.setContentText("Update category sucessfully");
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void showAlertDelete(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete category");
        alert.setHeaderText(null);
        alert.setContentText("Delete category sucessfully");
        alert.initOwner(stage);
        alert.showAndWait();
    }

}
