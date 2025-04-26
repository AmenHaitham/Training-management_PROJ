package org.example.hana;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Date;

public class FeedbackDAO {

    public static boolean addFeedback
            (Feedback feedback) {
        try {
            Connection conn = DriverManager.getConnection
                    ("؟؟", "username", "password");

            String sql = " (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, feedback.getUserID());
            stmt.setInt(2, feedback.getRating());
            stmt.setString(3, feedback.getComments());
            stmt.setDate(4, new Date(feedback.getSubmittedAt().getTime()));

            int rows = stmt.executeUpdate();
            conn.close();
            return rows > 0;

        } catch (Exception h) {h.printStackTrace();
            return false;
        }
    }
}
