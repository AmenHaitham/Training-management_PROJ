package com.tms.Model.DAO;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.tms.DB.DatabaseConnection;
import com.tms.Model.Certificate;
import com.tms.Model.Training;
import com.tms.Model.User;

public class CertificateDAO implements AutoCloseable {
    private final Connection connection;

    public CertificateDAO() throws SQLException, ClassNotFoundException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    // =================== CRUD Operations ===================

public Optional<Integer> createCertificate(Certificate certificate) throws SQLException {
    validateCertificate(certificate);
    
    // Generate PDF content for the certificate
    byte[] pdfContent = generateCertificatePDF(
        certificate.getTrainee(),
        certificate.getTraining(),
        certificate.getIssuedAt()
    );
    
    // Set the generated PDF content
    certificate.setCertificateFile(pdfContent);
    
    String sql = """
        INSERT INTO Certificates (trainee_id, training_id, certificate_file)
        VALUES (?, ?, ?)
        """;
    
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setInt(1, certificate.getTrainee().getId());
        stmt.setInt(2, certificate.getTraining().getId());
        stmt.setBytes(3, certificate.getCertificateFile());

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            return Optional.empty();
        }

        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return Optional.of(generatedKeys.getInt(1));
            }
            return Optional.empty();
        }
    }
}

private byte[] generateCertificatePDF(User trainee, Training training, Timestamp issuedAt) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        Document document = new Document(PageSize.A4.rotate()); // Landscape orientation
        PdfWriter.getInstance(document, baos);
        document.open();
        
        // Add certificate content
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Certificate of Completion", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30);
        document.add(title);
        
        // Add trainee information
        Font contentFont = new Font(Font.FontFamily.HELVETICA, 18, Font.NORMAL);
        Paragraph traineeInfo = new Paragraph();
        traineeInfo.add(new Chunk("This certificate is awarded to\n", contentFont));
        traineeInfo.add(new Chunk(trainee.getFirstName() + " " + trainee.getLastName(), 
            new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD)));
        traineeInfo.setAlignment(Element.ALIGN_CENTER);
        traineeInfo.setSpacingAfter(20);
        document.add(traineeInfo);
        
        // Add training information
        Paragraph trainingInfo = new Paragraph();
        trainingInfo.add(new Chunk("for successfully completing\n", contentFont));
        trainingInfo.add(new Chunk(training.getTitle(), 
            new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE)));
        trainingInfo.setAlignment(Element.ALIGN_CENTER);
        trainingInfo.setSpacingAfter(30);
        document.add(trainingInfo);
        
        // Add issue date
        Paragraph date = new Paragraph();
        date.add(new Chunk("Issued on: " + issuedAt.toLocalDateTime().toLocalDate(), contentFont));
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(40);
        document.add(date);
        
        // Add signature line
        Paragraph signature = new Paragraph();
        signature.add(new Chunk("________________________\n", contentFont));
        signature.add(new Chunk("Training Director", contentFont));
        signature.setAlignment(Element.ALIGN_CENTER);
        document.add(signature);
        
        document.close();
        return baos.toByteArray();
    } catch (Exception e) {
        throw new RuntimeException("Failed to generate certificate PDF", e);
    }
}
    public Optional<Certificate> getCertificateById(int id) throws SQLException {
        String sql = """
            SELECT c.*, u.first_name, u.last_name, t.title as training_title
            FROM Certificates c
            LEFT JOIN Users u ON c.trainee_id = u.id
            LEFT JOIN Trainings t ON c.training_id = t.id
            WHERE c.id = ?
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCertificate(rs));
                }
                return Optional.empty();
            }
        }
    }

    public Optional<Certificate> getCertificate(int traineeId, int trainingId) throws SQLException {
        String sql = """
            SELECT c.*, u.first_name, u.last_name, t.title as training_title
            FROM Certificates c
            LEFT JOIN Users u ON c.trainee_id = u.id
            LEFT JOIN Trainings t ON c.training_id = t.id
            WHERE c.trainee_id = ? AND c.training_id = ?
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            stmt.setInt(2, trainingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCertificate(rs));
                }
                return Optional.empty();
            }
        }
    }

    public List<Certificate> getAllCertificates() throws SQLException {
        List<Certificate> certificates = new ArrayList<>();
        String sql = """
            SELECT c.*, u.first_name, u.last_name, t.title as training_title
            FROM Certificates c
            LEFT JOIN Users u ON c.trainee_id = u.id
            LEFT JOIN Trainings t ON c.training_id = t.id
            ORDER BY c.issued_at DESC
            """;
            
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                certificates.add(mapResultSetToCertificate(rs));
            }
        }
        return certificates;
    }

    // =================== Specialized Queries ===================

    public List<Certificate> getCertificatesByTrainee(int traineeId) throws SQLException {
        List<Certificate> certificates = new ArrayList<>();
        String sql = """
            SELECT c.*, u.first_name, u.last_name, t.title as training_title
            FROM Certificates c
            LEFT JOIN Users u ON c.trainee_id = u.id
            LEFT JOIN Trainings t ON c.training_id = t.id
            WHERE c.trainee_id = ?
            ORDER BY c.issued_at DESC
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    certificates.add(mapResultSetToCertificate(rs));
                }
            }
        }
        return certificates;
    }

    public List<Certificate> getCertificatesByTraining(int trainingId) throws SQLException {
        List<Certificate> certificates = new ArrayList<>();
        String sql = """
            SELECT c.*, u.first_name, u.last_name, t.title as training_title
            FROM Certificates c
            LEFT JOIN Users u ON c.trainee_id = u.id
            LEFT JOIN Trainings t ON c.training_id = t.id
            WHERE c.training_id = ?
            ORDER BY c.issued_at DESC
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainingId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    certificates.add(mapResultSetToCertificate(rs));
                }
            }
        }
        return certificates;
    }

    public boolean updateCertificateFile(int certificateId, byte[] fileData) throws SQLException {
        String sql = "UPDATE Certificates SET certificate_file = ?, issued_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBytes(1, fileData);
            stmt.setInt(2, certificateId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCertificate(int id) throws SQLException {
        String sql = "DELETE FROM Certificates WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // =================== Helper Methods ===================

    private Certificate mapResultSetToCertificate(ResultSet rs) throws SQLException {
        Certificate certificate = new Certificate();
        certificate.setId(rs.getInt("id"));
        
        // Create minimal trainee object
        User trainee = new User();
        trainee.setId(rs.getInt("trainee_id"));
        trainee.setFirstName(rs.getString("first_name"));
        trainee.setLastName(rs.getString("last_name"));
        certificate.setTrainee(trainee);
        
        // Create minimal training object
        Training training = new Training();
        training.setId(rs.getInt("training_id"));
        training.setTitle(rs.getString("training_title"));
        certificate.setTraining(training);
        
        certificate.setCertificateFile(rs.getBytes("certificate_file"));
        
        // Handle timestamps
        Timestamp issuedAt = rs.getTimestamp("issued_at");
        certificate.setIssuedAt(issuedAt != null ? issuedAt : null);
        
        return certificate;
    }

    private void validateCertificate(Certificate certificate) {
        if (certificate == null) {
            throw new IllegalArgumentException("Certificate cannot be null");
        }
        if (certificate.getTraining().getId() <= 0) {
            throw new IllegalArgumentException("Invalid trainee ID");
        }
        if (certificate.getTraining().getId() <= 0) {
            throw new IllegalArgumentException("Invalid training ID");
        }
        
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // =================== Business Logic Methods ===================

    public boolean hasCertificate(int traineeId, int trainingId) throws SQLException {
        String sql = "SELECT 1 FROM Certificates WHERE trainee_id = ? AND training_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            stmt.setInt(2, trainingId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Optional<byte[]> getCertificateFile(int certificateId) throws SQLException {
        String sql = "SELECT certificate_file FROM Certificates WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, certificateId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getBytes("certificate_file"));
                }
                return Optional.empty();
            }
        }
    }

    public int countCertificatesForTraining(int trainingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Certificates WHERE training_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
}