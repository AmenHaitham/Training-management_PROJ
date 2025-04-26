import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class AssessmentService {

    public boolean insertAssessment(Assessment assessment) {
        String sql = "INSERT INTO assessments (title, description, total_marks, due_date, course_id, created_by) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement inser = conn.prepareStatement(sql)) {

            inser .setString(1, assessment.getTitle());
            inser .setString(2, assessment.getDescription());
            inser .setInt(3, assessment.getTotalMarks());
            inser .setDate(4, new Date(assessment.getDueDate().getTime()));
            inser .setInt(5, assessment.getCourseId());
            inser .setInt(6, assessment.getCreatedBy());

            return  inser .executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
 }
}
}