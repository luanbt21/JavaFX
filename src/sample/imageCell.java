package sample;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class imageCell<Products> extends TableCell<Products, Blob> {

    private final ImageView imageView;

    public imageCell() {
        imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        setGraphic(imageView);
        setMinHeight(70);
    }

    @Override
    protected void updateItem(Blob item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            imageView.setImage(null);
        } else {
            try {
                InputStream is = item.getBinaryStream();
                OutputStream os  = new FileOutputStream("photo.jpg");
                byte[] content  = new byte[1024];
                int size = 0;
                while ((size = is.read(content)) != -1) {
                    os.write(content, 0, size);
                }
                Image image = new Image("file:photo.jpg", 100, 100 , true, true);
                imageView.setImage(image);
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
