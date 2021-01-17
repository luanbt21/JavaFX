package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ManageOrderController implements Initializable {

    Scene fxmlFile;
    Parent root;
    Stage window;
    JdbcDao jdbc;

    @FXML
    private GridPane gridPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        jdbc = new JdbcDao();
        showTableList();
    }

    private void showTableList() {
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM orders ORDER BY tableName ";
        Statement st;
        ResultSet rs;
        int column = 0;
        int row = 0;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Button tableName;
            String table;
            String total;
            while (rs.next()){
                table = rs.getString("tableName");
                total = rs.getString("total");

                tableName = new Button(table + " - " + total);
                tableName.setPrefSize(180, 40);
                if(column == 3) {
                    column = 0;
                    row++;
                }
                gridPane.add(tableName, column++, row);
                gridPane.setHgap(10);
                gridPane.setVgap(10);

                tableName.setOnAction(event ->
                {
                    Object node = event.getSource();
                    Button b = (Button)node;
                    String tablename = b.getText().substring(0, b.getText().indexOf("-"));
                    String query1 = "UPDATE orders SET edit = 'yes' WHERE tableName = '" + tablename +"'";
                    executeQuery(query1);
                    Stage stage = (Stage) gridPane.getScene().getWindow();
                    stage.close();
                });
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
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

    private void showAlertConnection() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Connection Status:");
        alert.setContentText("Failed to connect to sever");
        Window window = gridPane.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

}
