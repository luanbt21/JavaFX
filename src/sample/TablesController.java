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

public class TablesController implements Initializable {

    @FXML
    private TextField tfTableName;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private TableView<Tables> tableTable;
    @FXML
    private TableColumn<Tables, Integer> colID;
    @FXML
    private TableColumn<Tables, String> colTable;

    JdbcDao jdbc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListenerForTable();
        jdbc = new JdbcDao();
        showTable();
    }

    @FXML
    void saveTable(ActionEvent event) {
        insertRecord(event);
    }

    @FXML
    void editEntry(ActionEvent event) {
        try{
            Tables table = tableTable.getSelectionModel().getSelectedItem();
            String query = "UPDATE tables SET name = '" + tfTableName.getText() + "' WHERE id = '" + table.getId() + "'";
            executeQuery(query);
            String query1 = "UPDATE orders SET tableName = '" + tfTableName.getText() +"' WHERE tableName = '" + table.getName() + "'";
            executeQuery(query1);
            showAlertUpdate(event);
            showTable();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void deleteEntry(ActionEvent event) {
        try{
            Tables table = tableTable.getSelectionModel().getSelectedItem();
            String tableName = table.getName();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete table");
            alert.setHeaderText("Are you sure want to delete this table?");
            alert.setContentText(tableName);
            Window window = tableTable.getScene().getWindow();
            alert.initOwner(window);
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == null) {
                System.out.println("null");
            } else if (option.get() == ButtonType.OK) {
                String query = "DELETE FROM tables WHERE id = '" + table.getId() + "'";
                executeQuery(query);
                String query1 = "DELETE FROM orders WHERE tableName = '" + table.getName() + "'";
                executeQuery(query1);
                showAlertDelete(event);
                showTable();
            } else if (option.get() == ButtonType.CANCEL) {
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void showTable(){
        ObservableList<Tables> list = getTableList();
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTable.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableTable.setItems(list);
    }

    private ObservableList<Tables> getTableList() {
        ObservableList<Tables> tableList = FXCollections.observableArrayList();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM tables";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Tables table;
            while (rs.next()){
                table = new Tables(rs.getInt("id"), rs.getString("name"));
                tableList.add(table);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return tableList;
    }

    private void insertRecord(ActionEvent event){
        String name = tfTableName.getText();
        if(!name.isEmpty()){
            String query = "INSERT INTO tables (name) VALUES ('" + name + "')";
            executeQuery(query);
            showAlertInsert(event);
            tfTableName.setText("");
            showTable();
        }
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

    private void addListenerForTable(){
        tableTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection , newSelection) -> {
            if(newSelection != null){
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                tfTableName.setText(newSelection.getName());
                btnSave.setDisable(true);
            }
            else{
                tfTableName.setText("");
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
        Window window = tableTable.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

    private void showAlertInsert(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Insert table");
        alert.setHeaderText(null);
        alert.setContentText("Insert table sucessfully");
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void showAlertUpdate(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update table");
        alert.setHeaderText(null);
        alert.setContentText("Update table sucessfully");
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void showAlertDelete(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete table");
        alert.setHeaderText(null);
        alert.setContentText("Delete table sucessfully");
        alert.initOwner(stage);
        alert.showAndWait();
    }

}