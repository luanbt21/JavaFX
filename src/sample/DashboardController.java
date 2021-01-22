package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class DashboardController implements Initializable {

    Scene fxmlFile, fxmlFile1;
    Parent root;
    Stage window;
    JdbcDao jdbc;

    @FXML
    private Label lbDate;
    @FXML
    private Label lbTotalMoney;
    @FXML
    private Label lbTransaction;
    @FXML
    private Label lbTable;
    @FXML
    private GridPane gridPane;
    @FXML
    private TableView<NewOrder> tableOrder;
    @FXML
    private TableColumn<NewOrder, String> colDescription;
    @FXML
    private TableColumn<NewOrder, String> colPrice;
    @FXML
    private TableColumn<NewOrder, String> colQuantity;
    @FXML
    private TableColumn<NewOrder, String> colTotal;
    @FXML
    private TableColumn<NewOrder, Void> colBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        jdbc = new JdbcDao();
        showTableOrder(lbTransaction.getText());
        showDate();
        setedit();
    }

    private void showDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        lbDate.setText(dtf.format(now));
    }

    @FXML
    void cancelOrder() {
        setEditToNo(lbTransaction.getText());
        gridPane.getChildren().clear();
        tableOrder.getItems().clear();
        lbTotalMoney.setText("");
        lbTable.setText("");
        lbTransaction.setText("");
        try{
            openModalWindow("CancelOrder.fxml", "Select Table | Cancel Order");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void drinkProducts(ActionEvent event) {
//        Object node = event.getSource();
//        Button b = (Button)node;
//        String category = b.getText();
        showListProducts("Drinks");
    }

    @FXML
    void dessertProducts(ActionEvent event) {
//        Object node = event.getSource();
//        Button b = (Button)node;
//        String category = b.getText();
        showListProducts("Desserts");
    }

    @FXML
    void mealProducts(ActionEvent event) {
//        Object node = event.getSource();
//        Button b = (Button)node;
//        String category = b.getText();
        showListProducts("Meal");
    }

    @FXML
    void snackProducts(ActionEvent event) {
//        Object node = event.getSource();
//        Button b = (Button)node;
//        String category = b.getText();
        showListProducts("Snack");
    }

    @FXML
    void otherProducts(ActionEvent event) {
//        Object node = event.getSource();
//        Button b = (Button)node;
//        String category = b.getText();
        showListProducts("Others");
    }

    @FXML
    void Logout(ActionEvent event) throws IOException {
        setEditToNo(lbTransaction.getText());
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
        Scene login_scene = new Scene(FXMLLoader.load(getClass().getResource("Login.fxml")));
        stage.setTitle("Order App | Login");
        stage.setScene(login_scene);
        stage.show();
    }

    @FXML
    void manageTable() {
        setEditToNo(lbTransaction.getText());
        lbTransaction.setText("");
        lbTable.setText("");
        lbTotalMoney.setText("");
        tableOrder.getItems().clear();
        gridPane.getChildren().clear();
        try{
            openModalWindow("Tables.fxml", "Manage Tables");
        }catch (Exception e){
            showAlertConnection();
        }
    }

    @FXML
    void manageProduct() {
        setEditToNo(lbTransaction.getText());
        lbTransaction.setText("");
        lbTable.setText("");
        lbTotalMoney.setText("");
        tableOrder.getItems().clear();
        gridPane.getChildren().clear();
        try{
            openModalWindow("Products.fxml", "Manage Products");
        }catch (Exception e){
            showAlertConnection();
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void manageOrder() {
        setEditToNo(lbTransaction.getText());
        lbTransaction.setText("");
        lbTable.setText("");
        lbTotalMoney.setText("");
        tableOrder.getItems().clear();
        gridPane.getChildren().clear();
        try{
            openModalWindow("ManageOrder.fxml", "Select Table | Manage Order");

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        getEditTable();
        showTableOrder(lbTransaction.getText());
        showLableMoney(lbTransaction.getText());
    }

    @FXML
    void payment() {
        setEditToNo(lbTransaction.getText());
        lbTransaction.setText("");
        lbTable.setText("");
        lbTotalMoney.setText("");
        tableOrder.getItems().clear();
        gridPane.getChildren().clear();
        try{
            openModalWindow("Payment.fxml", "Select Table | Payment");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        lbTransaction.setText("");
        lbTable.setText("");
        lbTotalMoney.setText("");
        tableOrder.getItems().clear();
    }


    void showLableMoney(String transaction){
        if(transaction.equals("")){
        }else{
            Connection connection = jdbc.getConnection();
            if(jdbc.failed_connect_sever){
                showAlertConnection();
            }
            String query = "SELECT total FROM orders WHERE id = '" + transaction.substring(0, transaction.indexOf("M")) +"'";
            Statement st;
            ResultSet rs;
            String totalmoney = null;
            try{
                st = connection.createStatement();
                rs = st.executeQuery(query);
                while (rs.next()){
                    totalmoney = rs.getString("total");
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            lbTotalMoney.setText(totalmoney);
        }
    }

    @FXML
    void newOrder() {
        gridPane.getChildren().clear();
        tableOrder.getItems().clear();
        setEditToNo(lbTransaction.getText());
        lbTotalMoney.setText("");
        String query = "Insert Into orders (date) VALUES ( '" + lbDate.getText() +  "')";
        executeQuery(query);
        showLableTransaction();
        lbTable.setText("");
        lbTotalMoney.setText("0");
        try{
            openModalWindow("NewOrder.fxml", "Select Table | New order");
            showTableOrder(lbTransaction.getText());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query1 = "SELECT MAX(id) FROM orders ";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query1);
            int id = 0;
            while (rs.next()){
                id = rs.getInt(1);
            }
            String query2 = "SELECT tableName, total FROM orders WHERE id = '" + id +"'";
            Statement st1;
            ResultSet rs1;
            st1 = connection.createStatement();
            rs1 = st1.executeQuery(query2);
            while(rs1.next()){
                lbTable.setText(rs1.getString("tableName"));
                lbTotalMoney.setText(rs1.getString("total"));
                if(rs1.wasNull()){
                    lbTransaction.setText("");
                    lbTotalMoney.setText("0.00");
                    String query3 = "DELETE FROM orders WHERE id = '" + id + "'";
                    executeQuery(query3);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        showTableOrder(lbTransaction.getText());
        showLableMoney(lbTransaction.getText());
    }

    @FXML
    void salesReport() {
        setEditToNo(lbTransaction.getText());
        lbTotalMoney.setText("");
        lbTransaction.setText("");
        lbTable.setText("");
        gridPane.getChildren().clear();
        tableOrder.getItems().clear();
        try{
            openModalWindow("Sales.fxml", "Sales Report");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showLableTransaction() {
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT MAX(id) FROM orders ";
        Statement st;
        ResultSet rs;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime now = LocalDateTime.now();
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            String transactionNo;
            while (rs.next()){
                transactionNo = String.valueOf(rs.getInt(1)) ;
                transactionNo = transactionNo + "M" + dtf.format(now);
                lbTransaction.setText(transactionNo);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void showListProducts(String category){
            if(isCheck_order()){
                gridPane.getChildren().clear();
                ObservableList<Products> list = getProductsList(category);
                var ref = new Object() {
                    int column = 0;
                    int row = 1;
                };
                list.forEach((product) -> {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/sample/ItemProduct.fxml"));
                        AnchorPane anchorPane = fxmlLoader.load();
                        ItemProductController itemProductController = fxmlLoader.getController();
                        itemProductController.setData(product, lbTransaction.getText(), lbDate.getText());
                        anchorPane.setOnMouseClicked(mouseEvent -> {
                            try{
                                FXMLLoader fxmlLoader1 = new FXMLLoader();
                                fxmlLoader1.setLocation(getClass().getResource("/sample/Quantity.fxml"));
                                AnchorPane anchorPane1 = fxmlLoader1.load();
                                QuantityController quantityController = fxmlLoader1.getController();
                                quantityController.setData(product, lbTransaction.getText(), lbDate.getText());
                                fxmlFile1 = new Scene(anchorPane1);
                                window = new Stage();
                                window.setScene(fxmlFile1);
                                window.initModality(Modality.APPLICATION_MODAL);
                                window.setAlwaysOnTop(true);
                                window.setIconified(false);
                                window.setResizable(false);
                                window.setTitle("Choose quantity");
                                window.showAndWait();

                                Connection connection = jdbc.getConnection();
                                if (jdbc.failed_connect_sever) {
                                    showAlertConnection();
                                }
                                String query = "SELECT quantity FROM products WHERE description = '" + product.getDescription() + "'";
                                Statement st;
                                ResultSet rs;
                                String quantity = null;
                                try {
                                    st = connection.createStatement();
                                    rs = st.executeQuery(query);
                                    while (rs.next()) {
                                        quantity = rs.getString("quantity");
                                    }
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                                itemProductController.setLableQuantity(quantity);

                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }
                            showTableOrder(lbTransaction.getText());
                            showLableMoney(lbTransaction.getText());
                        });


                        if(ref.column == 3){
                            ref.column = 0;
                            ref.row ++;
                        }
                        gridPane.add(anchorPane, ref.column++, ref.row);
                        GridPane.setMargin(anchorPane, new Insets(10));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            else {
                showAlertNewOrder();
            }

    }

    public void showTableOrder(String transaction){
        ObservableList<NewOrder> list = getOrderList(transaction);
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        Callback<TableColumn<NewOrder, Void>, TableCell<NewOrder, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<NewOrder, Void> call(final TableColumn<NewOrder, Void> param) {
                final TableCell<NewOrder, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Delete");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            try {
                                NewOrder selected_order = getTableView().getItems().get(getIndex());
                                Connection connection = jdbc.getConnection();
                                if(jdbc.failed_connect_sever){
                                    showAlertConnection();
                                }
                                PreparedStatement ps ;
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Delete");
                                alert.setHeaderText("Are you sure delete ?");
                                alert.setContentText(selected_order.getDescription());
                                Window window = gridPane.getScene().getWindow();
                                alert.initOwner(window);
                                Optional<ButtonType> option = alert.showAndWait();
                                if (option.get() == null) {
                                } else if (option.get() == ButtonType.OK) {
                                    ps = connection.prepareStatement(
                                            "DELETE FROM ordernow WHERE id = ? AND description = ? AND price = ? AND date = ? AND quantity = ? AND total = ? ");
                                    ps.setString(1, selected_order.getId());
                                    ps.setString(2, selected_order.getDescription());
                                    ps.setString(3, selected_order.getPrice());
                                    ps.setString(4, lbDate.getText());
                                    ps.setInt(5, selected_order.getQuantity());
                                    ps.setString(6, selected_order.getTotal());
                                    ps.executeUpdate();
                                    int price1 = Integer.parseInt(selected_order.getPrice());
                                    int total = Integer.parseInt(lbTotalMoney.getText());
                                    int totalMoney = total -  selected_order.getQuantity() * price1;
                                    PreparedStatement ps1;
                                    ps1 = connection.prepareStatement("UPDATE orders SET total = ? WHERE id = ?");
                                    ps1.setString(1, String.valueOf(totalMoney));
                                    ps1.setString(2, selected_order.getId().substring(0, selected_order.getId().indexOf("M")));
                                    ps1.executeUpdate();


                                    String query1 = "SELECT quantity FROM products WHERE description = '" + selected_order.getDescription() +"'";
                                    Statement st1;
                                    ResultSet rs1;
                                    String quant = null;
                                    try{
                                        st1 = connection.createStatement();
                                        rs1 = st1.executeQuery(query1);
                                        while (rs1.next()){
                                            quant = rs1.getString("quantity");
                                        }
                                    }catch (Exception e){
                                        System.out.println(e.getMessage());
                                    }
                                    PreparedStatement ps2 = connection.prepareStatement(
                                            "UPDATE products SET quantity = ? WHERE description = ?");
                                    ps2.setString(1, String.valueOf(Integer.parseInt(quant) + selected_order.getQuantity()) );
                                    ps2.setString(2, selected_order.getDescription());
                                    ps2.executeUpdate();

                                    showAlertDelete(event);
                                } else if (option.get() == ButtonType.CANCEL) {
                                }
                                tableOrder.getItems().remove(selected_order);
                                showLableMoney(lbTransaction.getText());
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            btn.setPrefSize(60, 15);
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colBtn.setCellFactory(cellFactory);

        tableOrder.setItems(list);
    }

    private ObservableList<NewOrder> getOrderList(String transaction) {
        ObservableList<NewOrder> orderList = FXCollections.observableArrayList();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM ordernow WHERE id = '" + transaction + "'";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            NewOrder newOrder;
            while (rs.next()){
                newOrder = new NewOrder(rs.getString("id"), rs.getString("description"),
                        rs.getString("price"), rs.getString("date"), rs.getInt("quantity"),
                        rs.getString("total")
                        );
                orderList.add(newOrder);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return orderList;
    }

    private void getEditTable(){
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM orders WHERE edit = 'yes'";
        Statement st;
        ResultSet rs;
        String transaction;
        String tableName;
        String total;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()){
                transaction = rs.getString("id") + "M" + rs.getString("date").replace("-", "");
                lbTransaction.setText(transaction);
                tableName = rs.getString("tableName");
                lbTable.setText(tableName);
                total = rs.getString("total");
                lbTotalMoney.setText(total);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void setEditToNo(String transaction){
        if(transaction.equals("")){
        }else {
            String query = "UPDATE orders SET edit = 'no' WHERE id = '" + transaction.substring(0, transaction.indexOf("M")) + "'";
            executeQuery(query);
        }
    }

    private ObservableList<Products> getProductsList(String category) {
        ObservableList<Products> productList = FXCollections.observableArrayList();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM products WHERE category = '" + category +"'";
        if(category.equals("Others")){
            query = "SELECT * FROM products WHERE NOT category = 'Drinks' " +
                    "AND NOT category = 'Meals'  AND NOT category = 'Snacks' AND NOT category = 'Desserts'" +
                    " GROUP BY category";
        }
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Products product;
            while (rs.next()){
                product = new Products(rs.getString("description"), rs.getString("price"), rs.getString("quantity"), rs.getBlob("image"));
                productList.add(product);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return productList;

    }

    private void setedit() {
        String query = "UPDATE orders SET edit = 'no'";
        executeQuery(query);
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
        Window window = gridPane.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

    private void showAlertNewOrder() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Select a table");
        Window window = gridPane.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

    public boolean isCheck_order(){
        if(!lbTransaction.getText().equals("")){
            return true;
        }else {
            return false;
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

    private void showAlertDelete(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete ");
        alert.setHeaderText(null);
        alert.setContentText("Delete sucessfully");
        alert.initOwner(stage);
        alert.showAndWait();
    }

}
