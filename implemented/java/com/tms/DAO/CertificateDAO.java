package com.tms.DAO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tms.DB.DatabaseConnection;
import com.tms.Model.Certificate;
import com.tms.Model.Training;
import com.tms.Model.User;

public class CertificateDAO implements AutoCloseable {
    private final Connection connection;

    public CertificateDAO() throws SQLException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    // =================== CRUD Operations ===================

    public Optional<Certificate> createCertificate(Certificate certificate) throws SQLException {
    validateCertificate(certificate);
    
    byte[] pdfContent = generateCertificatePDF(
        certificate.getTrainee(),
        certificate.getTraining(),
        new Timestamp(System.currentTimeMillis())
    );
    
    certificate.setCertificateFile(pdfContent);
    
    String sql = """ 
        INSERT INTO Certificates (trainee_id, training_id, certificate_file, issued_at)
        VALUES (?, ?, ?, ?)
        RETURNING id, trainee_id, training_id, certificate_file, issued_at
        """;
    
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, certificate.getTrainee().getId());
        stmt.setInt(2, certificate.getTraining().getId());
        stmt.setBytes(3, certificate.getCertificateFile());
        stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                // Now fetch the complete record with joins
                return getCertificateById(rs.getInt("id"));
            }
            return Optional.empty();
        }
    }
}

    // =================== Query Methods ===================

    public Optional<Certificate> getCertificateById(int id) throws SQLException {
        String sql = """
            SELECT c.id, c.trainee_id, c.training_id, c.certificate_file, c.issued_at,
                   u.first_name, u.last_name, t.title as training_title
            FROM Certificates c
            JOIN Users u ON c.trainee_id = u.id
            JOIN Trainings t ON c.training_id = t.id
            WHERE c.id = ?
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToCertificate(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Certificate> getCertificate(int traineeId, int trainingId) throws SQLException {
        String sql = """
            SELECT c.id, c.trainee_id, c.training_id, c.certificate_file, c.issued_at,
                   u.first_name, u.last_name, t.title as training_title
            FROM Certificates c
            JOIN Users u ON c.trainee_id = u.id
            JOIN Trainings t ON c.training_id = t.id
            WHERE c.trainee_id = ? AND c.training_id = ?
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            stmt.setInt(2, trainingId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToCertificate(rs)) : Optional.empty();
            }
        }
    }

    public List<Certificate> getAllCertificates() throws SQLException {
        List<Certificate> certificates = new ArrayList<>();
        String sql = """
            SELECT c.id, c.trainee_id, c.training_id, c.certificate_file, c.issued_at,
                   u.first_name, u.last_name, t.title as training_title
            FROM Certificates c
            JOIN Users u ON c.trainee_id = u.id
            JOIN Trainings t ON c.training_id = t.id
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

    public List<Certificate> getCertificatesByTrainee(int traineeId) throws SQLException {
        List<Certificate> certificates = new ArrayList<>();
        String sql = """
            SELECT c.id, c.trainee_id, c.training_id, c.certificate_file, c.issued_at,
                   u.first_name, u.last_name, t.title as training_title
            FROM Certificates c
            JOIN Users u ON c.trainee_id = u.id
            JOIN Trainings t ON c.training_id = t.id
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
            SELECT c.id, c.trainee_id, c.training_id, c.certificate_file, c.issued_at,
                   u.first_name, u.last_name, t.title as training_title
            FROM Certificates c
            JOIN Users u ON c.trainee_id = u.id
            JOIN Trainings t ON c.training_id = t.id
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

    // =================== Update/Delete Operations ===================

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
        
        // Create and populate trainee object
        User trainee = new User();
        trainee.setId(rs.getInt("trainee_id"));
        trainee.setFirstName(rs.getString("first_name"));
        trainee.setLastName(rs.getString("last_name"));
        certificate.setTrainee(trainee);
        
        // Create and populate training object
        Training training = new Training();
        training.setId(rs.getInt("training_id"));
        training.setTitle(rs.getString("training_title"));
        certificate.setTraining(training);
        
        certificate.setCertificateFile(rs.getBytes("certificate_file"));
        certificate.setIssuedAt(rs.getTimestamp("issued_at"));
        
        return certificate;
    }

 private byte[] generateCertificatePDF(User trainee, Training training, Timestamp issuedAt) {
    // Input validation
    if (trainee == null || training == null) {
        throw new IllegalArgumentException("Trainee and training cannot be null");
    }
    if (issuedAt == null) {
        issuedAt = new Timestamp(System.currentTimeMillis());
    }

    // Create document in landscape orientation
    Document document = new Document(PageSize.A4.rotate());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setCloseStream(false);
        document.open();

        // ========== DESIGN ELEMENTS ==========
        // Color palette (using your red #c0392b)
        BaseColor primaryColor = new BaseColor(192, 57, 43); // #c0392b
        BaseColor darkGray = new BaseColor(51, 51, 51);
        BaseColor lightGray = new BaseColor(238, 238, 238);

        // Fonts
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD, primaryColor);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.ITALIC, darkGray);
        Font nameFont = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD, darkGray);
        Font trainingFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, darkGray);
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, darkGray);
        Font smallPrintFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, darkGray);
        Font signatureFont = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL, darkGray);

        // ========== CERTIFICATE BORDER ==========
        PdfPTable borderTable = new PdfPTable(1);
        borderTable.setWidthPercentage(85);
        borderTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell borderCell = new PdfPCell();
        borderCell.setBorderColor(primaryColor);
        borderCell.setBorderWidth(4f);
        borderCell.setPadding(40f);
        borderCell.setBackgroundColor(new BaseColor(255, 255, 255));
        borderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        borderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        // Add top border decoration
        
        Chunk chunk = new Chunk(" ", new Font(Font.FontFamily.HELVETICA, 10));
        chunk.setBackground(primaryColor); 
        
        Paragraph topBorder = new Paragraph();
        topBorder.add(chunk);
        topBorder.setSpacingAfter(0);
        borderCell.addElement(topBorder);

        // ========== CERTIFICATE CONTENT ==========
        Paragraph content = new Paragraph();
        content.setAlignment(Element.ALIGN_CENTER);

        // Certificate title
        Paragraph title = new Paragraph("CERTIFICATE OF COMPLETION", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15);
        content.add(title);

        // Award text
        Paragraph awardText = new Paragraph("This certificate is awarded to", subtitleFont);
        awardText.setAlignment(Element.ALIGN_CENTER);
        awardText.setSpacingAfter(20);
        content.add(awardText);

        // Trainee name
        String fullName = (trainee.getFirstName() != null ? trainee.getFirstName() : "") + " " +
                         (trainee.getLastName() != null ? trainee.getLastName() : "");
        Paragraph name = new Paragraph(fullName, nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        name.setSpacingAfter(25);
        content.add(name);

        // Completion text
        Paragraph completionText = new Paragraph("for successful completion of the training program", subtitleFont);
        completionText.setAlignment(Element.ALIGN_CENTER);
        completionText.setSpacingAfter(20);
        content.add(completionText);

        // Training title
        Paragraph trainingTitle = new Paragraph(
            training.getTitle() != null ? training.getTitle() : "Training Program", 
            trainingFont);
        trainingTitle.setAlignment(Element.ALIGN_CENTER);
        trainingTitle.setSpacingAfter(25);
        content.add(trainingTitle);

        // Date issued
        Paragraph date = new Paragraph(
            "Issued on: " + issuedAt.toLocalDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")), 
            dateFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(30);
        content.add(date);

        // Signatures section
        PdfPTable signatureTable = new PdfPTable(2);
        signatureTable.setWidthPercentage(80);
        signatureTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        signatureTable.setSpacingBefore(40);

        // Left signature
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(PdfPCell.NO_BORDER);
        leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        leftCell.addElement(new Paragraph("________________________", signatureFont));
        leftCell.addElement(new Paragraph("Training Director", signatureFont));
        signatureTable.addCell(leftCell);

        // Right signature
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(PdfPCell.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(new Paragraph("________________________", signatureFont));
        rightCell.addElement(new Paragraph("Program Coordinator", signatureFont));
        signatureTable.addCell(rightCell);

        content.add(signatureTable);

        // Certificate ID (small print)
        String certId = "Certificate ID: CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Paragraph smallPrint = new Paragraph(certId, smallPrintFont);
        smallPrint.setAlignment(Element.ALIGN_CENTER);
        smallPrint.setSpacingBefore(30);
        content.add(smallPrint);

        // Add bottom border decoration
        Paragraph bottomBorder = new Paragraph();
        bottomBorder.add(chunk);
        bottomBorder.setSpacingBefore(10);
        content.add(bottomBorder);

        // ========== FINAL ASSEMBLY ==========
        borderCell.addElement(content);
        borderTable.addCell(borderCell);
        document.add(borderTable);

        // Add subtle watermark
        PdfContentByte canvas = writer.getDirectContentUnder();
        canvas.saveState();
        canvas.setColorFill(new BaseColor(240, 240, 240));
        canvas.beginText();
        canvas.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false), 60);
        canvas.showTextAligned(Element.ALIGN_CENTER, "OFFICIAL CERTIFICATE", 
            PageSize.A4.rotate().getWidth()/2, 
            PageSize.A4.rotate().getHeight()/2, 
            45);
        canvas.endText();
        canvas.restoreState();

    } catch (DocumentException | IOException e) {
        throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
    } finally {
        if (document != null && document.isOpen()) {
            document.close();
        }
    }

    return baos.toByteArray();
}


    private void validateCertificate(Certificate certificate) {
        if (certificate == null) {
            throw new IllegalArgumentException("Certificate cannot be null");
        }
        if (certificate.getTrainee() == null || certificate.getTrainee().getId() <= 0) {
            throw new IllegalArgumentException("Invalid trainee");
        }
        if (certificate.getTraining() == null || certificate.getTraining().getId() <= 0) {
            throw new IllegalArgumentException("Invalid training");
        }
    }

    // =================== Business Logic Methods ===================

    public boolean hasCertificate(int traineeId, int trainingId) throws SQLException {
        String sql = "SELECT 1 FROM Certificates WHERE trainee_id = ? AND training_id = ? LIMIT 1";
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
                return rs.next() ? Optional.ofNullable(rs.getBytes("certificate_file")) : Optional.empty();
            }
        }
    }

    public int countCertificatesForTraining(int trainingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Certificates WHERE training_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainingId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                DatabaseConnection.releaseConnection(connection);
            } catch (Exception e) {
                System.err.println("Error releasing connection: " + e.getMessage());
            }
        }
    }

    // =================== Main Method for Testing ===================

    public static void main(String[] args) {
    try (CertificateDAO certificateDAO = new CertificateDAO()) {
        // Create test data
        User testTrainee = new User();
        testTrainee.setId(5); // Replace with valid trainee ID
        testTrainee.setFirstName("John");
        testTrainee.setLastName("Doe");
        
        Training testTraining = new Training();
        testTraining.setId(1); // Replace with valid training ID
        testTraining.setTitle("Advanced Java Programming");
        
        Certificate testCertificate = new Certificate();
        testCertificate.setTrainee(testTrainee);
        testCertificate.setTraining(testTraining);

        // Test 1: Create Certificate
        System.out.println("=== Testing createCertificate ===");
        Optional<Certificate> createdCert = certificateDAO.createCertificate(testCertificate);
        if (createdCert.isPresent()) {
            Certificate cert = createdCert.get();
            System.out.println("Created certificate ID: " + cert.getId());
            System.out.println("Issued at: " + cert.getIssuedAt());
            System.out.println("PDF size: " + (cert.getCertificateFile() != null ? cert.getCertificateFile().length + " bytes" : "null"));
            
            // Update our test certificate with the generated ID
            testCertificate.setId(cert.getId());
        } else {
            System.out.println("Failed to create certificate");
        }

        // Test 2: Get Certificate by ID
        System.out.println("\n=== Testing getCertificateById ===");
        Optional<Certificate> fetchedCert = certificateDAO.getCertificateById(testCertificate.getId());
        fetchedCert.ifPresentOrElse(
            cert -> System.out.println("Fetched certificate for: " + cert.getTrainee().getFirstName() + " " + cert.getTrainee().getLastName()),
            () -> System.out.println("Certificate not found")
        );

        // Test 3: Get Certificate by Trainee and Training
        System.out.println("\n=== Testing getCertificate(traineeId, trainingId) ===");
        Optional<Certificate> comboCert = certificateDAO.getCertificate(
            testTrainee.getId(), 
            testTraining.getId()
        );
        comboCert.ifPresentOrElse(
            cert -> System.out.println("Found certificate via trainee/training combo"),
            () -> System.out.println("Certificate not found via trainee/training combo")
        );

        // Test 4: Check if certificate exists
        System.out.println("\n=== Testing hasCertificate ===");
        boolean hasCert = certificateDAO.hasCertificate(
            testTrainee.getId(), 
            testTraining.getId()
        );
        System.out.println("Trainee has certificate for this training: " + hasCert);

        // Test 5: Get all certificates
        System.out.println("\n=== Testing getAllCertificates ===");
        List<Certificate> allCerts = certificateDAO.getAllCertificates();
        System.out.println("Total certificates: " + allCerts.size());
        allCerts.forEach(c -> System.out.println(
            "Cert #" + c.getId() + " - " + 
            c.getTrainee().getFirstName() + " - " + 
            c.getTraining().getTitle()
        ));

        // Test 6: Get certificates by trainee
        System.out.println("\n=== Testing getCertificatesByTrainee ===");
        List<Certificate> traineeCerts = certificateDAO.getCertificatesByTrainee(testTrainee.getId());
        System.out.println("Certificates for trainee: " + traineeCerts.size());

        // Test 7: Get certificates by training
        System.out.println("\n=== Testing getCertificatesByTraining ===");
        List<Certificate> trainingCerts = certificateDAO.getCertificatesByTraining(testTraining.getId());
        System.out.println("Certificates for training: " + trainingCerts.size());

        // Test 8: Count certificates for training
        System.out.println("\n=== Testing countCertificatesForTraining ===");
        int certCount = certificateDAO.countCertificatesForTraining(testTraining.getId());
        System.out.println("Certificate count for training: " + certCount);

        // Test 9: Get certificate file
        System.out.println("\n=== Testing getCertificateFile ===");
        Optional<byte[]> certFile = certificateDAO.getCertificateFile(testCertificate.getId());
        certFile.ifPresentOrElse(
            file -> System.out.println("Certificate file size: " + file.length + " bytes"),
            () -> System.out.println("No certificate file found")
        );

        // Test 10: Update certificate file
        System.out.println("\n=== Testing updateCertificateFile ===");
        // Generate new PDF content
        byte[] newPdf = certificateDAO.generateCertificatePDF(
            testTrainee, 
            testTraining, 
            new Timestamp(System.currentTimeMillis())
        );
        boolean updated = certificateDAO.updateCertificateFile(testCertificate.getId(), newPdf);
        System.out.println("Certificate file updated: " + updated);

        // Test 11: Delete certificate
        System.out.println("\n=== Testing deleteCertificate ===");
        boolean deleted = certificateDAO.deleteCertificate(testCertificate.getId());
        System.out.println("Certificate deleted: " + deleted);

        // Verify deletion
        System.out.println("\n=== Verifying deletion ===");
        Optional<Certificate> deletedCert = certificateDAO.getCertificateById(testCertificate.getId());
        System.out.println("Certificate still exists: " + deletedCert.isPresent());

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}