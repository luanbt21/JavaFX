package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

public class CancelOrderController implements Initializable {

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
            NewOrder newOrder;
            Button tableName;
            while (rs.next()){
                newOrder = new NewOrder(rs.getString("id"), rs.getString("tableName"), rs.getString("date"), rs.getString("total"));
                tableName = new Button(newOrder.getTableName() + " - " + newOrder.getTotal());
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
                    try{
                        Object node = event.getSource();
                        Button b = (Button)node;
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Cancel Order");
                        alert.setHeaderText("Are you sure want to cancel this order?");
                        alert.setContentText(b.getText());
                        Window window = gridPane.getScene().getWindow();
                        alert.initOwner(window);
                        Optional<ButtonType> option = alert.showAndWait();
                        if (option.get() == null) {
                        } else if (option.get() == ButtonType.OK) {
                            String tablename = b.getText().substring(0, b.getText().indexOf("-"));

                            String query1 = "DELETE FROM orders WHERE tableName = '" + tablename +"'";
                            executeQuery(query1);
                            showAlertCancel(event);
                            Node node1 = (Node) event.getSource();
                            Stage stage = (Stage) node1.getScene().getWindow();
                            stage.close();
                        } else if (option.get() == ButtonType.CANCEL) {
                        }
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
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

    private void showAlertCancel(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cancel Order");
        alert.setHeaderText(null);
        alert.setContentText("Cancel order sucessfully");
        alert.initOwner(stage);
        alert.showAndWait();
    }

}
