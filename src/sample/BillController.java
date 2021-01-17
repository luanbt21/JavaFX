package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class BillController  {

    @FXML
    private Label lbId;
    @FXML
    private Label lbDate;
    @FXML
    private Label lbMoney;
    @FXML
    private TableView<NewOrder> tableBill;
    @FXML
    private TableColumn<NewOrder, String> colDescription;
    @FXML
    private TableColumn<NewOrder, String> colPrice;
    @FXML
    private TableColumn<NewOrder, Integer> colQuantity;
    @FXML
    private TableColumn<NewOrder, String> colTotal;

    JdbcDao jdbc;

    public void setData(String id, String date, String total){
        lbDate.setText(date);
        lbId.setText(id);
        lbMoney.setText(total);
    }

    public void setTableBill(String id){
        jdbc = new JdbcDao();
        ObservableList<NewOrder> orderList = FXCollections.observableArrayList();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM showbill WHERE id = '" + id + "'";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            NewOrder newOrder;
            while (rs.next()){
                newOrder = new NewOrder(rs.getString("description"), rs.getString("price"), rs.getInt("quantity"), rs.getString("total"));
                orderList.add(newOrder);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        ObservableList<NewOrder> list = orderList;
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        tableBill.setItems(list);
    }


    public void setData1(String billId) {
        lbId.setText(billId);
        jdbc = new JdbcDao();
        ObservableList<NewOrder> orderList = FXCollections.observableArrayList();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM sales WHERE id = '" + billId + "'";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            NewOrder newOrder = null;
            while (rs.next()){
                newOrder = new NewOrder(rs.getString("id"), rs.getString("date"), rs.getString("total"));
            }
            lbDate.setText(newOrder.getDate());
            lbMoney.setText(newOrder.getTotal());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        String query1 = "SELECT * FROM showbill WHERE id = '" + billId + "'";
        Statement st1;
        ResultSet rs1;
        try{
            st1 = connection.createStatement();
            rs1 = st1.executeQuery(query1);
            NewOrder newOrder;
            while (rs1.next()){
                newOrder = new NewOrder(rs1.getString("description"), rs1.getString("price"), rs1.getInt("quantity"), rs1.getString("total"));
                orderList.add(newOrder);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        ObservableList<NewOrder> list = orderList;
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        tableBill.setItems(list);
    }

    private void showAlertConnection() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Connection Status:");
        alert.setContentText("Failed to connect to sever");
        Window window = lbId.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }


}
