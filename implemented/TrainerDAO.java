import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Trainer {

    public boolean uploadMaterial(Material mat) {
        String sql = "INSERT INTO materials (title, description, file_data) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement upload = conn.prepareStatement(sql)) {

            upload.setString(1, mat.getTitle());
            upload.setString(2, mat.getDescription());
            upload.setBytes(3, mat.getFileData());

            return upload.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean uploadAssessment(Assessment assessment) {
        String sql = "INSERT INTO assessments (title, material_id, due_date) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement upload = conn.prepareStatement(sql)) {

            upload.setString(1, assessment.getTitle());
            upload.setInt(2, assessment.getMaterialId());
            upload.setDate(3, assessment.getDueDate());

            return upload.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean giveFeedBack(Feedback feedback) {
        String sql = "INSERT INTO feedback (trainee_id, message, created_at) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement giveFeed = conn.prepareStatement(sql)) {

            giveFeed.setInt(1, feedback.getTraineeId());
            giveFeed.setString(2, feedback.getMessage());
            giveFeed.setTimestamp(3, feedback.getCreatedAt());

            return giveFeed.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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