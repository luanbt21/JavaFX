package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ItemProductController implements Initializable {

    JdbcDao jdbc;
    DashboardController dash;

    @FXML
    private Label lbName;
    @FXML
    private Label lbPrice;
    @FXML
    private ImageView imageView;
    @FXML
    private Label lbQuantity;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        jdbc = new JdbcDao();
        dash = new DashboardController();
    }

    private Products product;
    private String transaction;
    private String date;

    public void setData(Products product, String transaction, String date){
        this.product = product;
        this.transaction = transaction;
        lbPrice.setText(product.getPrice());
        this.date = date;
        lbName.setText(product.getDescription());
        lbQuantity.setText(product.getQuantity());
        try {
            InputStream is = product.getImage().getBinaryStream();
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

    public void setLableQuantity(String quantity){
        lbQuantity.setText(quantity);
    }

}
