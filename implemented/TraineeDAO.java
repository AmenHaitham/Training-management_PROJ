import java.sql.*;
import java.util.ArrayList;

public class TraineeDAO {

    public ArrayList<Material> getMaterialsByTrainee(int traineeId) {
        ArrayList<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE uploaded_by = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement gMate = conn.prepareStatement(sql)) {

            gMate.setInt(1, traineeId);
            ResultSet r = gMate.executeQuery();

            while (r.next()) {
                Material material = new Material(
                        r.getInt("material_id"),
                        r.getString("title"),
                        r.getBlob("file"),
                        r.getString("file_type"),
                        r.getInt("session_id"),
                        r.getInt("uploaded_by"),
                        r.getDate("uploaded_at")
                );
                materials.add(material);
            }

        } catch (SQLException gM) {
            gM.printStackTrace();
        }

        return materials;
    }

    public ArrayList<Feedback> getFeedbacksByTrainee(int traineeId) {
        ArrayList<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM feedbacks WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement gFeed = conn.prepareStatement(sql)) {

            gFeed.setInt(1, traineeId);
            ResultSet r = gFeed.executeQuery();

            while (r.next()) {
                Feedback feedback = new Feedback(
                        r.getInt("feedback_id"),
                        r.getInt("user_id"),
                        r.getInt("rating"),
                        r.getString("comments"),
                        r.getDate("submitted_at")
                );
                feedbacks.add(feedback);
            }

        } catch (SQLException gF) {
            gF.printStackTrace();
        }

        return feedbacks;
    }

    public boolean addFeedback(Feedback feedback) {
        String sql = "INSERT INTO feedbacks (user_id, rating, comments, submitted_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement AFeed = conn.prepareStatement(sql)) {

            AFeed .setInt(1, feedback.getUserID());
            AFeed .setInt(2, feedback.getRating());
            AFeed .setString(3, feedback.getComments());
            AFeed .setDate(4, new java.sql.Date(feedback.getSubmittedAt().getTime()));

            return AFeed .executeUpdate() > 0;

        } catch (SQLException aF) {
            aF.printStackTrace();
            return false;
        }
    }

    public ArrayList<Certificate> getCertificatesByTrainee(int traineeId) {
        ArrayList<Certificate> certificates = new ArrayList<>();
        String sql = "SELECT * FROM certificates WHERE trainee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement gCert = conn.prepareStatement(sql)) {

            gCert .setInt(1, traineeId);
            ResultSet r =  gCert .executeQuery();

            while (r.next()) {
                Certificate certificate = new Certificate(
                        r.getInt("certificate_id"),
                        r.getInt("training_id"),
                        r.getInt("trainee_id"),
                        r.getBlob("file"),
                        r.getDate("date_issued")
                );
                certificates.add(certificate);
            }

        } catch (SQLException gC) {
            gC.printStackTrace();
        }

        return certificates;
    }
}
