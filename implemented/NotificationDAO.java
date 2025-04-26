package org.example.hana;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Date;
public class NotificationDAO {

    public static boolean addNotification(Notification notification)
    {
        try {
            Connection conn = DriverManager.getConnection
                    ("", "username", "password");
            String sql = " ( ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, notification.getMessage());
            stmt.setInt(2, notification.getUserID());
            stmt.setDate(3, new Date(notification.getTimestamp().getTime()
            ));
            int rows = stmt.executeUpdate();
            conn.close();
            return rows > 0;
        } catch (Exception e) {e.printStackTrace();
            return false;
        }
    }
}
