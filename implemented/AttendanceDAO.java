import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AttendanceService {

    public boolean recordAttendance(Attendance attendance) {
        String sql = "INSERT INTO attendance (trainee_id, session_id, attended) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement record = conn.prepareStatement(sql)) {

            record.setInt(1, attendance.getTraineeId());
            record.setInt(2, attendance.getSessionId());
            record.setBoolean(3, attendance.isAttended());

            return record.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
 }
}
}