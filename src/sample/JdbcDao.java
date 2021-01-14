package sample;

import java.sql.*;

public class JdbcDao {
    private static final String DATABASE_URL = "jdbc:mysql://localhost/order";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "";
    private static final String SELECT_QUERY = "SELECT * FROM users WHERE username = ? AND password = ?";
    public boolean failed_connect_sever ;


    public boolean validate(String username, String password){
        try{
            Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return true;
            }
        }
        catch(SQLException e){
            failed_connect_sever = true;
        }
        return false;
    }

    public Connection getConnection(){
        try{
            failed_connect_sever = false;
            Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
            return connection;
        }catch (SQLException e){
            failed_connect_sever = true;
        }
        return null;
    }
}
