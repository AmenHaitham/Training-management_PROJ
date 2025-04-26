package org.example.hana;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Date;

public class TrainingDAO {
    public static boolean addTraining(Training training) {
        try {
            Connection conn = DriverManager.getConnection("", "username", "password");

            String sql = " (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, training.getTitle());
            stmt.setString(2, training.getDescription());
            stmt.setDate(3, new Date(training.getDate().getTime())
            );
            int rows = stmt.executeUpdate();
            conn.close();
            return rows > 0;
        } catch (Exception e) {e.printStackTrace();
            return false;
        }
    }
}
