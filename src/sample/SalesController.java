package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class SalesController implements Initializable {

    @FXML
    private TableView<NewOrder> tableBill;
    @FXML
    private TableColumn<NewOrder, String> colId;
    @FXML
    private TableColumn<NewOrder, String> colDate;
    @FXML
    private TableColumn<NewOrder, String> colMoney;
    @FXML
    private TextField tfSearch;
    @FXML
    private Label lbShow;
    @FXML
    private Label lbTotalMoney;
    @FXML
    private TextField tfMonth;
    @FXML
    private TextField tfDate;

    Scene fxmlFile;
    Parent root;
    Stage window;
    JdbcDao jdbc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        jdbc = new JdbcDao();
        showTableBillAll();
    }

    @FXML
    void btnShowAll() {
        showTableBillAll();
    }

    @FXML
    void search() {
        String billId = tfSearch.getText();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT id FROM sales WHERE id = '" + billId +"'";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            boolean check = rs.next();
            if(check == false){
                showAlertNoResult();
            }
            else {
                try{
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/sample/Bill.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();
                    BillController billController = fxmlLoader.getController();
                    billController.setData1(billId);
                    fxmlFile = new Scene(anchorPane);
                    window = new Stage();
                    window.setScene(fxmlFile);
                    window.initModality(Modality.APPLICATION_MODAL);
                    window.setAlwaysOnTop(true);
                    window.setIconified(false);
                    window.setResizable(false);
                    window.setTitle("Choose quantity");
                    window.showAndWait();
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    @FXML
    void showDate() {
        String date = tfDate.getText();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT date FROM sales WHERE date = '" + date +"'";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            boolean check = rs.next();
            if(check == false){
                showAlertNoResult();
            }
            else {
                showTableBillDate();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void showMonth() {
        String month = tfMonth.getText();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT date FROM sales WHERE MONTH(date) = '" + month +"'";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            boolean check = rs.next();
            if(check == false){
                showAlertNoResult();
            }
            else {
                showTableBillMonth();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showTableBillMonth() {
        ObservableList<NewOrder> list = getTableBillMonth();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colMoney.setCellValueFactory(new PropertyValueFactory<>("total"));
        tableBill.setItems(list);
        tfMonth.setText("");
        tfDate.setText("");
    }

    private ObservableList<NewOrder> getTableBillMonth(){
        String month = tfMonth.getText();
        ObservableList<NewOrder> billList = FXCollections.observableArrayList();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM sales WHERE MONTH(date) = '" + month +"'";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            NewOrder newOrder = null;
            while (rs.next()){
                newOrder = new NewOrder(rs.getString("id"), rs.getString("date"), rs.getString("total"));
                billList.add(newOrder);
            }
            lbShow.setText(newOrder.getDate().substring(0, 7));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        String query1= "SELECT SUM(total) AS total FROM sales WHERE MONTH(date) = '" + month +"'";
        Statement st1;
        ResultSet rs1;
        try{
            st1 = connection.createStatement();
            rs1 = st1.executeQuery(query1);
            String totalmoney = null;
            while (rs1.next()){
                totalmoney = rs1.getString("total");
            }
            lbTotalMoney.setText(totalmoney);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return billList;
    }

    private void showTableBillDate() {
        ObservableList<NewOrder> list = getTableBillDate();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colMoney.setCellValueFactory(new PropertyValueFactory<>("total"));
        tableBill.setItems(list);
        tfMonth.setText("");
        tfDate.setText("");
    }

    private ObservableList<NewOrder> getTableBillDate(){
        String date = tfDate.getText();
        ObservableList<NewOrder> billList = FXCollections.observableArrayList();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM sales WHERE date = '" + date +"'";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            NewOrder newOrder;
            while (rs.next()){
                newOrder = new NewOrder(rs.getString("id"), rs.getString("date"), rs.getString("total"));
                billList.add(newOrder);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        String query1= "SELECT SUM(total) AS total FROM sales WHERE date = '" + date +"'";
        Statement st1;
        ResultSet rs1;
        try{
            st1 = connection.createStatement();
            rs1 = st1.executeQuery(query1);
            String totalmoney = null;
            while (rs1.next()){
                totalmoney = rs1.getString("total");
            }
            lbShow.setText(date);
            lbTotalMoney.setText(totalmoney);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return billList;
    }

    public void showTableBillAll(){
        ObservableList<NewOrder> list = getTableBillAll();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colMoney.setCellValueFactory(new PropertyValueFactory<>("total"));
        tableBill.setItems(list);
        tfMonth.setText("");
        tfDate.setText("");
    }

    private ObservableList<NewOrder> getTableBillAll() {
        ObservableList<NewOrder> billList = FXCollections.observableArrayList();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM sales";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            NewOrder newOrder;
            while (rs.next()){
                newOrder = new NewOrder(rs.getString("id"), rs.getString("date"), rs.getString("total"));
                billList.add(newOrder);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        String query1= "SELECT SUM(total) AS total FROM sales";
        Statement st1;
        ResultSet rs1;
        try{
            st1 = connection.createStatement();
            rs1 = st1.executeQuery(query1);
            String totalmoney = null;
            while (rs1.next()){
                totalmoney = rs1.getString("total");
            }
            lbShow.setText("All");
            lbTotalMoney.setText(totalmoney);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return billList;
    }

    private void showAlertConnection() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Connection Status:");
        alert.setContentText("Failed to connect to sever");
        Window window = tableBill.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

    private void showAlertNoResult() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Result status:");
        alert.setContentText("There is no result");
        Window window = tfDate.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

}
