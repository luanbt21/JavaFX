package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lbMess;

    @FXML
    public void Login(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        JdbcDao jdbcDao = new JdbcDao();
        boolean flag = jdbcDao.validate(username, password);
        if(!flag){
            if(txtUsername.getText().isEmpty() && txtPassword.getText().isEmpty()) {
                lbMess.setText("Please enter username and password");
            }
            if(txtUsername.getText().isEmpty() && !txtPassword.getText().isEmpty()) {
                lbMess.setText("Please enter username");
            }
            if(!txtUsername.getText().isEmpty() && txtPassword.getText().isEmpty()) {
                lbMess.setText("Please enter password");
            }
            if(!txtUsername.getText().isEmpty() && !txtPassword.getText().isEmpty()) {
                lbMess.setText("Wrong username or password");
            }
            if(jdbcDao.failed_connect_sever){
                lbMess.setText("Sever connection error");
            }
        }
        else{
            try{
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                Scene dashboard_scene = new Scene(FXMLLoader.load(getClass().getResource("Dashboard.fxml")));
                stage.setScene(dashboard_scene);
                stage.setTitle("Order App | Dashboard");
                stage.show();
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
