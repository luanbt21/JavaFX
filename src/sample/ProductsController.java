package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductsController implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<Products> tableProducts;
    @FXML
    private TableColumn<Products, Integer> colId;
    @FXML
    private TableColumn<Products, String> colDescription;
    @FXML
    private TableColumn<Products, String> colPrice;
    @FXML
    private TableColumn<Products, String> colCategory;
    @FXML
    private TableColumn<Products, String> colQuantity;
    @FXML
    private TableColumn<Products, String> colWeight;
    @FXML
    private ImageView imageView;
    @FXML
    private ComboBox<String> cbWeight;
    @FXML
    private TextField tfDescription;
    @FXML
    private TextField tfPrice;
    @FXML
    private ComboBox<String> cbCategory;
    @FXML
    private TextField tfQuantity;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnSave;
    @FXML
    private TableColumn<Products, Blob> colImage;

    JdbcDao jdbc;
    Scene fxmlFile;
    Parent root;
    Stage window;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        jdbc = new JdbcDao();
        addListenerForTable();
        showProducts();
        showCbCategory();
        showCbWeight();
    }

    @FXML
    void addImage() {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose an image");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*png");
        fc.getExtensionFilters().add(imageFilter);
        File file = fc.showOpenDialog(stage);
        if (file != null){
            Image image = new Image(file.toURI().toString(),200, 200, false, true);
            imageView.setImage(image);
        }
    }

    @FXML
    void addCategory() {
        try {
            openModalWindow("Category.fxml", "Manage Category");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        showCbCategory();
        showProducts();
    }

    @FXML
    void saveProduct(ActionEvent event) {
        String description = tfDescription.getText();
        String price = tfPrice.getText();
        String category = cbCategory.getValue();
        String quantity = tfQuantity.getText();
        String weight = cbWeight.getValue();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }

        if(description == null || price == null || category == null || quantity == null || weight == null || imageView.getImage() == null ){
            showAlertMissingInformation();
        }
        else{
            byte[] blob = imagenToByte(imageView.getImage());
            try{
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO products (description, price, category, quantity, weight, image) VALUES (?, ?, ?, ?, ?, ?) ");
                ps.setString(1, description);
                ps.setString(2, price);
                ps.setString(3, category);
                ps.setString(4, quantity);
                ps.setString(5, weight);
                ps.setBlob(6, new SerialBlob(blob));
                ps.executeUpdate();
                showAlertInsert(event);

            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        tfPrice.setText("");
        tfDescription.setText("");
        cbWeight.getSelectionModel().clearSelection();
        tfQuantity.setText("");
        cbCategory.getSelectionModel().clearSelection();
        imageView.setImage(null);
        showProducts();
    }

    private byte[] imagenToByte(Image imagen) {
        BufferedImage bufferimage = SwingFXUtils.fromFXImage(imagen, null);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferimage, "jpg", output );
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte [] data = output.toByteArray();
        return data;
    }

    @FXML
    void deleteEntry(ActionEvent event) {
        try{
            Products product = tableProducts.getSelectionModel().getSelectedItem();
            String productDescription = product.getDescription();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete product");
            alert.setHeaderText("Are you sure want to delete this product?");
            alert.setContentText(productDescription);
            Window window = tableProducts.getScene().getWindow();
            alert.initOwner(window);
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == null) {
            } else if (option.get() == ButtonType.OK) {
                String query = "DELETE FROM products WHERE id = '" + product.getId() + "'";
                executeQuery(query);
                showAlertDelete(event);

            } else if (option.get() == ButtonType.CANCEL) {
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        imageView.setImage(null);
        showProducts();
    }

    @FXML
    void editEntry(ActionEvent event) {
        String description = tfDescription.getText();
        String price = tfPrice.getText();
        String category = cbCategory.getValue();
        String quantity = tfQuantity.getText();
        String weight = cbWeight.getValue();
        byte[] blob = imagenToByte(imageView.getImage());
        Products product = tableProducts.getSelectionModel().getSelectedItem();
        try {
            Connection connection = jdbc.getConnection();
            if(jdbc.failed_connect_sever){
                showAlertConnection();
            }
            try{
                int pric = Integer.parseInt(price);
                int quanti = Integer.parseInt(quantity);
            }catch (Exception e){
                showAlertInput();
            }
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE products SET description = ?, price = ?, category = ?, quantity = ?, weight = ?, image = ? WHERE id = ?");
            ps.setString(1, description);
            ps.setString(2, price);
            ps.setString(3, category);
            ps.setString(4, quantity);
            ps.setString(5, weight);
            ps.setBlob(6, new SerialBlob(blob));
            ps.setInt(7, product.getId());
            ps.executeUpdate();
            showAlertUpdate(event);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        imageView.setImage(null);
        showProducts();
    }

    private void addListenerForTable(){
        tableProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection , newSelection) -> {
            if(newSelection != null){
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
                btnSave.setDisable(true);
                tfDescription.setText(newSelection.getDescription());
                tfPrice.setText(newSelection.getPrice());
                cbWeight.getSelectionModel().select(newSelection.getWeight());
                tfQuantity.setText(newSelection.getQuantity());
                cbCategory.getSelectionModel().select(newSelection.getCategory());
                try {
                    InputStream is = newSelection.getImage().getBinaryStream();
                    OutputStream os  = new FileOutputStream("photo.jpg");
                    byte[] content  = new byte[1024];
                    int size = 0;
                    while ((size = is.read(content)) != -1) {
                        os.write(content, 0, size);
                    }
                    Image image = new Image("file:photo.jpg", 200, 200, true, true);
                    imageView.setImage(image);
                } catch (SQLException | IOException throwables) {
                    throwables.printStackTrace();
                }
            }
            else{
                tfDescription.setText("");
                tfPrice.setText("");
                cbWeight.getSelectionModel().clearSelection();
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
                btnSave.setDisable(false);
                tfQuantity.setText("");
                cbCategory.getSelectionModel().clearSelection();
            }
        });
    }

    public void showProducts(){
        ObservableList<Products> list = getProductList();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colImage.setCellFactory(param -> new imageCell<>());
        tableProducts.setItems(list);
    }

    private void showCbCategory(){
        ObservableList<String> categorieslist = FXCollections.observableArrayList();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM categories";
        Statement st;
        ResultSet rs;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            String category;
            while (rs.next()){
                category = rs.getString("name");
                categorieslist.add(category);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        cbCategory.setItems(categorieslist);
    }

    private void showCbWeight(){
        ObservableList<String> statuslist = FXCollections.observableArrayList("Yes", "No");
        cbWeight.setItems(statuslist);
    }

    private ObservableList<Products> getProductList() {
        ObservableList<Products> productList = FXCollections.observableArrayList();
        Connection connection = jdbc.getConnection();
        if(jdbc.failed_connect_sever){
            showAlertConnection();
        }
        String query = "SELECT * FROM products";
        Statement st;
        ResultSet rs;
        try{
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Products product;
            while (rs.next()){
                product = new Products(rs.getInt("id"), rs.getString("description"),
                        rs.getString("price"), rs.getString("category"),
                        rs.getString("quantity"), rs.getString("weight"), rs.getBlob("image"));
                productList.add(product);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return productList;
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
        Window window = tableProducts.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

    private void showAlertInsert(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Insert product");
        alert.setHeaderText(null);
        alert.setContentText("Insert product sucessfully");
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void showAlertUpdate(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update product");
        alert.setHeaderText(null);
        alert.setContentText("Update product sucessfully");
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void showAlertDelete(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete product");
        alert.setHeaderText(null);
        alert.setContentText("Delete product sucessfully");
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void showAlertMissingInformation() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Missing information:");
        alert.setContentText("some information is missing");
        Window window = tableProducts.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

    private void showAlertInput() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText("Input status:");
        alert.setContentText("Invalid input");
        Window window = tableProducts.getScene().getWindow();
        alert.initOwner(window);
        alert.showAndWait();
    }

}
