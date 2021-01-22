package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class QuantityController implements Initializable {

    JdbcDao jdbc;

    @FXML
    private TextField tfQuantity;

    @FXML
    void addQuantity(ActionEvent event) {
        if (tfQuantity.getText().equals("")) {
            showAlertInput1();
        }else if(Integer.parseInt(tfQuantity.getText()) < 0){
            showAlert("value less than 0 !");
        } else {
            try {
                Connection connection = jdbc.getConnection();
                if (jdbc.failed_connect_sever) {
                    showAlertConnection();
                }
                int quantity = Integer.parseInt(tfQuantity.getText());
                String query1 = "SELECT quantity FROM products WHERE description = '" + product.getDescription() + "'";
                Statement st1;
                ResultSet rs1;
                String quant = null;
                try {
                    st1 = connection.createStatement();
                    rs1 = st1.executeQuery(query1);
                    while (rs1.next()) {
                        quant = rs1.getString("quantity");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                if ((Integer.parseInt(quant) - quantity) < 0) {
                    showAlertOutOfStock(Integer.parseInt(quant), quantity);
                } else {
                    int price = Integer.parseInt(product.getPrice());
                    String query2 = "SELECT quantity, total FROM ordernow WHERE id = '" + transaction + "' AND description = '" + product.getDescription() + "' AND date = '" + date + "'";
                    Statement st2;
                    ResultSet rs2;
                    try {
                        st2 = connection.createStatement();
                        rs2 = st2.executeQuery(query2);
                        boolean check = true;
                        while (rs2.next()) {
                            String quantity1 = null;
                            String total1 = null;
                            quantity1 = rs2.getString("quantity");
                            total1 = rs2.getString("total");
                            PreparedStatement ps3 = connection.prepareStatement(
                                    "UPDATE ordernow SET total = ? , quantity = ? WHERE id = ? AND description = ? ");
                            ps3.setInt(1, Integer.parseInt(total1) + price * quantity);
                            ps3.setInt(2, Integer.parseInt(quantity1) + quantity);
                            ps3.setString(3, transaction);
                            ps3.setString(4, product.getDescription());
                            ps3.executeUpdate();
                            check = false;
                        }
                        if (check) {
                            int total = quantity * price;
                            PreparedStatement ps = connection.prepareStatement(
                                    "INSERT INTO ordernow (id, description, price, date, quantity, total) VALUES (?, ?, ?, ?, ?, ?) ");
                            ps.setString(1, transaction);
                            ps.setString(2, product.getDescription());
                            ps.setString(3, product.getPrice());
                            ps.setString(4, date);
                            ps.setInt(5, quantity);
                            ps.setInt(6, total);
                            ps.executeUpdate();
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    String query = "SELECT SUM(total) AS sum FROM ordernow WHERE id = '" + transaction + "'";
                    Statement st;
                    ResultSet rs;
                    String totalmoney = null;
                    try {
                        st = connection.createStatement();
                        rs = st.executeQuery(query);
                        while (rs.next()) {
                            totalmoney = rs.getString("sum");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    PreparedStatement ps1 = connection.prepareStatement(
                            "UPDATE orders SET total = ? WHERE id = ?");
                    ps1.setString(1, totalmoney);
                    ps1.setString(2, transaction.substring(0, transaction.indexOf("M")));
                    ps1.executeUpdate();
                    PreparedStatement ps2 = connection.prepareStatement(
                            "UPDATE products SET quantity = ? WHERE description = ?");
                    ps2.setString(1, String.valueOf(Integer.parseInt(quant) - quantity));
                    ps2.setString(2, product.getDescription());
                    ps2.executeUpdate();

                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                showAlertInput();
            }
        }

        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        jdbc = new JdbcDao();
    }

    private Products product;
    private String transaction;
    private String date;

    public void setData(Products product, String transaction, String date) {
        this.product = product;
        this.transaction = transaction;
        this.date = date;
    }

    private void showAlertOutOfStock(int quant, int quantity) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Stock Status:");
        alert.setContentText("There's only " + quant + ". You ordered " + quantity);
        Window window = tfQuantity.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

    private void showAlertConnection() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Connection Status:");
        alert.setContentText("Failed to connect to sever");
        Window window = tfQuantity.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

    private void showAlertInput() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Input status:");
        alert.setContentText("Invalid input");
        Window window = tfQuantity.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

    private void showAlertInput1() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Input status:");
        alert.setContentText("Please enter quantity");
        Window window = tfQuantity.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Input status:");
        alert.setContentText(message);
        Window window = tfQuantity.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }
}
