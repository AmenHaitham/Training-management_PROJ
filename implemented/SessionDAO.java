import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class SessionService {

    public boolean insertSession(Session session) {
        String sql = "INSERT INTO sessions (title, notes, course_id, scheduled_date, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement inser = conn.prepareStatement(sql)) {

            inser.setString(1, session.getTitle());
            inser.setString(2, session.getNotes());
            inser.setInt(3, session.getCourseId());
            inser.setDate(4, new Date(session.getScheduledDate().getTime()));
            inser.setString(5, session.getStatus());

            return inser.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
 }
}
}