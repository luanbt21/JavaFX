package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class NewOrderController  implements Initializable {

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
        String query = "SELECT * FROM tables ORDER BY name ";
        Statement st;
        ResultSet rs;
        int column = 0;
        int row = 0;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Button tableName;
            while (rs.next()){
                tableName = new Button(rs.getString("name"));
                tableName.setPrefSize(180, 40);

                String query1 = "SELECT tableName from orders WHERE tableName = '" + tableName.getText() +"'";
                Statement st1;
                ResultSet rs1;
                try {
                    st1 = connection.createStatement();
                    rs1 = st1.executeQuery(query1);
                    while (rs1.next()){
                        tableName.setDisable(true);
                    }
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }

                if(column == 4) {
                    column = 0;
                    row++;
                }
                gridPane.add(tableName, column++, row);
                gridPane.setHgap(10);
                gridPane.setVgap(10);

                tableName.setOnAction(event ->
                        {
                            int id = 0;
                            int total = 0;
                            Object node = event.getSource();
                            Button b = (Button)node;

                            String query2 = "SELECT MAX(id) FROM orders ";
                            Statement st2;
                            ResultSet rs2;
                            try {
                                st2 = connection.createStatement();
                                rs2 = st2.executeQuery(query2);
                                while (rs2.next()){
                                    id = rs2.getInt(1);
                                }
                                String query3 = "UPDATE orders SET tableName = '" + b.getText() + "', total = '" + total +"', edit = 'no' WHERE id = '" + id +"'";
                                executeQuery(query3);
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }
                            Stage stage = (Stage) gridPane.getScene().getWindow();
                            stage.close();
                        });
            }
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


}
