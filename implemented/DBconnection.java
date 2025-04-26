package org.example.hana;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnection {
    public static Connection getConnection() {
        try {
            String user_name = "";
            String url = "";
            String password = "";
            Connection conn = DriverManager.getConnection(user_name, url, password);
            return conn;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
