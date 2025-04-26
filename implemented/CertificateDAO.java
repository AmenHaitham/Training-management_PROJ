import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CertificateDAO {

    public boolean addCertificate(Certificate cert) {
        String sql = "INSERT INTO certificates (trainee_id, training_id, file, date_issued) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement Certi = conn.prepareStatement(sql)) {

            Certi.setInt(1, cert.getTraineeId());
            Certi.setInt(2, cert.getTrainingId());
            Certi.setBlob(3, cert.getFile());
            Certi.setTimestamp(4, new Timestamp(cert.getDateIssued().getTime()));

            return Certi.executeUpdate() > 0;

        } catch (SQLException C) {
            System.out.println("Error inserting certificate: " + C.getMessage());
            return false;
        }
    }

    public Certificate getCertificateById(int certificateId) {
        String sql = "SELECT * FROM certificates WHERE certificate_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement geCert = conn.prepareStatement(sql)) {

            geCert.setInt(1, certificateId);
            ResultSet r = geCert.executeQuery();

            if (r.next()) {
                return new Certificate(
                        r.getInt("certificate_id"),
                        r.getInt("training_id"),
                        r.getInt("trainee_id"),
                        r.getBlob("file"),
                        r.getTimestamp("date_issued")
                );
            }

        } catch (SQLException C) {
            System.out.println("Error fetching certificate: " + C.getMessage());
        }
        return null;
    }

    public List<Certificate> getAllCertificates() {
        List<Certificate> list = new ArrayList<>();
        String sql = "SELECT * FROM certificates";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement lCert = conn.createStatement();
             ResultSet r = lCert.executeQuery(sql)) {

            while (r.next()) {
                Certificate cert = new Certificate(
                        r.getInt("certificate_id"),
                        r.getInt("training_id"),
                        r.getInt("trainee_id"),
                        r.getBlob("file"),
                        r.getTimestamp("date_issued")
                );
                list.add(cert);
            }

        } catch (SQLException C) {
            System.out.println("Error fetching certificates: " + C.getMessage());
        }
        return list;
    }

    public boolean deleteCertificate(int certificateId) {
        String sql = "DELETE FROM certificates WHERE certificate_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement dCert = conn.prepareStatement(sql)) {

            dCert.setInt(1, certificateId);
            return dCert.executeUpdate() > 0;

        } catch (SQLException C) {
            System.out.println("Error deleting certificate: " + C.getMessage());
            return false;
        }
    }
}
