package com.tms.Controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.itextpdf.text.DocumentException;
import com.tms.DAO.CertificateDAO;
import com.tms.Model.Certificate;
import com.tms.Model.Training;
import com.tms.Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/tms/certificates/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 10, // 10MB
    maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class CertificateServlet extends HttpServlet {

    private CertificateDAO certificateDAO;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context)
                    -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDate.class,
                    (com.google.gson.JsonSerializer<LocalDate>) (src, typeOfSrc, context)
                    -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(Timestamp.class,
                    (com.google.gson.JsonSerializer<Timestamp>) (src, typeOfSrc, context)
                    -> new com.google.gson.JsonPrimitive(src.toInstant().toString()))
            .registerTypeAdapter(byte[].class,
                    (com.google.gson.JsonSerializer<byte[]>) (src, typeOfSrc, context)
                    -> new com.google.gson.JsonPrimitive(Base64.getEncoder().encodeToString(src)))
            .setPrettyPrinting()
            .create();

    @Override
    public void init() throws ServletException {
        try {
            certificateDAO = new CertificateDAO();
        } catch (SQLException e) {
            throw new ServletException("Failed to initialize CertificateDAO", e);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllCertificates(request, response);
            } else {
                String[] parts = pathInfo.split("/");
                if (parts.length == 2) {
                    handleGetCertificateById(parts[1], response);
                } else if (parts.length == 3) {
                    handleSpecialRequests(parts, response);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                }
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (SQLException e) {
            logError("Database error in doGet", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database operation failed");
        } catch (Exception e) {
            logError("Unexpected error in doGet", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private void handleGetAllCertificates(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        String trainingId = request.getParameter("trainingId");
        String traineeId = request.getParameter("traineeId");

        List<Certificate> certificates;
        if (trainingId != null && !trainingId.isEmpty()) {
            int tId = Integer.parseInt(trainingId);
            certificates = certificateDAO.getCertificatesByTraining(tId);
        } else if (traineeId != null && !traineeId.isEmpty()) {
            int uId = Integer.parseInt(traineeId);
            certificates = certificateDAO.getCertificatesByTrainee(uId);
        } else {
            certificates = certificateDAO.getAllCertificates();
        }
        
        // Convert to JSON with proper error handling
        try {
            String jsonResponse = gson.toJson(certificates);
            response.getWriter().write(jsonResponse);
        } catch (JsonSyntaxException e) {
            logError("JSON serialization error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Data formatting error");
        }
    }

    private void handleGetCertificateById(String idStr, HttpServletResponse response) 
            throws SQLException, IOException, NumberFormatException {
        int certificateId = Integer.parseInt(idStr);
        Optional<Certificate> certificate = certificateDAO.getCertificateById(certificateId);
        
        if (certificate.isPresent()) {
            try {
                // Create a safe DTO version without the PDF bytes
                CertificateDTO certDTO = new CertificateDTO(certificate.get());
                String jsonResponse = gson.toJson(certDTO);
                response.getWriter().write(jsonResponse);
            } catch (Exception e) {
                logError("Error serializing certificate", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error formatting certificate data");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found");
        }
    }

    private static class CertificateDTO {
        private int id;
        private String traineeName;
        private String trainingTitle;
        private String issuedDate;
        
        public CertificateDTO(Certificate cert) {
            this.id = cert.getId();
            this.traineeName = cert.getTrainee().getFirstName() + " " + cert.getTrainee().getLastName();
            this.trainingTitle = cert.getTraining().getTitle();
            this.issuedDate = cert.getIssuedAt().toLocalDateTime().format(DATE_FORMATTER);
        }
        
        // Getters
        public int getId() { return id; }
        public String getTraineeName() { return traineeName; }
        public String getTrainingTitle() { return trainingTitle; }
        public String getIssuedDate() { return issuedDate; }
    }

    private void handleSpecialRequests(String[] parts, HttpServletResponse response) 
            throws SQLException, IOException, NumberFormatException, ServletException {
        int certificateId = Integer.parseInt(parts[1]);
        String action = parts[2];
        
        switch (action) {
            case "file":
                downloadCertificate(certificateId, response);
                break;
            case "view":
                handleViewRequest(certificateId, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // 1. Read and parse JSON request body
            JsonObject jsonObject;
            try (BufferedReader reader = request.getReader()) {
                jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (JsonParseException | IllegalStateException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
                return;
            }

            // 2. Validate required fields
            if (!jsonObject.has("trainee") || !jsonObject.has("training")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "Missing required fields: trainee and training");
                return;
            }

            JsonObject traineeObj = jsonObject.getAsJsonObject("trainee");
            JsonObject trainingObj = jsonObject.getAsJsonObject("training");

            if (!traineeObj.has("id") || !trainingObj.has("id")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "Missing required ID fields");
                return;
            }

            // 3. Extract and validate IDs
            int traineeId, trainingId;
            try {
                traineeId = traineeObj.get("id").getAsInt();
                trainingId = trainingObj.get("id").getAsInt();
                
                if (traineeId <= 0 || trainingId <= 0) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                        "IDs must be positive integers");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "Invalid ID format - must be integers");
                return;
            }

            // 4. Check for existing certificate
            try {
                if (certificateDAO.hasCertificate(traineeId, trainingId)) {
                    response.sendError(HttpServletResponse.SC_CONFLICT, 
                        "Certificate already exists for this trainee and training");
                    return;
                }
            } catch (SQLException e) {
                logError("Database error checking certificate existence", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error checking certificate existence");
                return;
            }

            // 5. Create certificate object
            User trainee = new User();
            trainee.setId(traineeId);

            Training training = new Training();
            training.setId(trainingId);

            Certificate certificate = new Certificate();
            certificate.setTrainee(trainee);
            certificate.setTraining(training);
            certificate.setIssuedAt(new Timestamp(System.currentTimeMillis()));

            // 6. Save certificate (includes PDF generation)
            try {
                Optional<Certificate> createdCertificate = certificateDAO.createCertificate(certificate);
                
                if (createdCertificate.isPresent()) {
                    // Return the DTO version without PDF bytes
                    CertificateDTO certDTO = new CertificateDTO(createdCertificate.get());
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.getWriter().write(gson.toJson(certDTO));
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Failed to persist certificate");
                }
            } catch (RuntimeException e) {
                if (e.getCause() instanceof DocumentException) {
                    logError("PDF generation failed", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Certificate PDF generation failed");
                } else {
                    logError("Unexpected error creating certificate", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Certificate creation failed");
                }
            } catch (SQLException e) {
                logError("Database error saving certificate", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Database error saving certificate");
            }

        } catch (Exception e) {
            logError("Unexpected error in doPost", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Unexpected server error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Certificate ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                handleCertificateDeletion(parts[1], response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid certificate ID");
        } catch (SQLException e) {
            logError("Database error in doDelete", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database operation failed");
        } catch (Exception e) {
            logError("Unexpected error in doDelete", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete certificate");
        }
    }

    private void handleCertificateDeletion(String idStr, HttpServletResponse response) 
            throws SQLException, IOException, NumberFormatException {
        int certificateId = Integer.parseInt(idStr);
        boolean deleted = certificateDAO.deleteCertificate(certificateId);
        if (deleted) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(
                new SimpleResponse(true, "Certificate deleted successfully")));
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found");
        }
    }

    private static class SimpleResponse {
        private boolean success;
        private String message;
        
        public SimpleResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    private void handleViewRequest(int certificateId, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Optional<Certificate> certificate = certificateDAO.getCertificateById(certificateId);
            if (certificate.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found");
                return;
            }

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(generateCertificateViewHtml(certificate.get()));
        } catch (Exception e) {
            logError("Error in handleViewRequest", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate certificate view");
        }
    }

    private String generateCertificateViewHtml(Certificate certificate) {
    return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <title>Certificate - %s</title>
            <style>
                body { 
                    font-family: 'Segoe UI', Arial, sans-serif; 
                    background-color: #f9f9f9;
                    margin: 0;
                    padding: 20px;
                }
                .container { 
                    max-width: 800px; 
                    margin: 40px auto;
                    text-align: center;
                }
                .certificate-header {
                    color: #c0392b;
                    margin-bottom: 10px;
                    font-size: 28px;
                    font-weight: bold;
                    text-transform: uppercase;
                    letter-spacing: 2px;
                }
                .certificate-border {
                    border: 4px solid #c0392b;
                    padding: 40px;
                    margin: 30px 0;
                    background-color: white;
                    box-shadow: 0 0 20px rgba(0,0,0,0.1);
                    position: relative;
                    overflow: hidden;
                }
                .certificate-border:before {
                    content: "";
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    height: 10px;
                    background-color: #c0392b;
                }
                .certificate-border:after {
                    content: "";
                    position: absolute;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    height: 10px;
                    background-color: #c0392b;
                }
                .training-title {
                    color: #333;
                    font-size: 24px;
                    margin: 20px 0;
                    font-weight: bold;
                }
                .award-text {
                    font-size: 18px;
                    color: #555;
                    margin: 15px 0;
                }
                .trainee-name {
                    color: #222;
                    font-size: 32px;
                    font-weight: bold;
                    margin: 25px 0;
                    padding: 10px 0;
                    border-top: 1px solid #eee;
                    border-bottom: 1px solid #eee;
                }
                .completion-text {
                    font-size: 18px;
                    color: #555;
                    margin: 15px 0;
                }
                .issue-date {
                    font-size: 16px;
                    color: #777;
                    margin-top: 30px;
                }
                .signatures {
                    display: flex;
                    justify-content: space-around;
                    margin-top: 50px;
                }
                .signature {
                    width: 40%%;
                }
                .signature-line {
                    border-top: 1px solid #c0392b;
                    width: 80%%;
                    margin: 5px auto;
                    padding-top: 10px;
                }
                .download-link { 
                    display: inline-block; 
                    margin-top: 30px; 
                    padding: 12px 30px; 
                    background-color: #c0392b; 
                    color: white; 
                    text-decoration: none; 
                    border-radius: 4px;
                    font-weight: bold;
                    transition: all 0.3s ease;
                }
                .download-link:hover {
                    background-color: #a5281b;
                    transform: translateY(-2px);
                    box-shadow: 0 5px 15px rgba(192, 57, 43, 0.3);
                }
                .certificate-id {
                    font-size: 12px;
                    color: #999;
                    margin-top: 20px;
                }
            </style>
        </head>
        <body>
            <div class='container'>
                <div class='certificate-header'>Certificate of Achievement</div>
                <div class='certificate-border'>
                    <div class='award-text'>This certificate is proudly awarded to</div>
                    <div class='trainee-name'>%s %s</div>
                    <div class='completion-text'>for successful completion of</div>
                    <div class='training-title'>%s</div>
                    <div class='issue-date'>Issued on: %s</div>
                    
                    <div class='signatures'>
                        <div class='signature'>
                            <div class='signature-line'></div>
                            <div>Training Director</div>
                        </div>
                        <div class='signature'>
                            <div class='signature-line'></div>
                            <div>Program Coordinator</div>
                        </div>
                    </div>
                    
                    <div class='certificate-id'>Certificate ID: CERT-%d</div>
                </div>
                <a class='download-link' href='/tms/certificates/%d/file'>Download Official Certificate</a>
            </div>
        </body>
        </html>
        """,
        escapeHtml(certificate.getTraining().getTitle()),
        escapeHtml(certificate.getTrainee().getFirstName()),
        escapeHtml(certificate.getTrainee().getLastName()),
        escapeHtml(certificate.getTraining().getTitle()),
        escapeHtml(certificate.getIssuedAt().toLocalDateTime().format(DATE_FORMATTER)),
        certificate.getId(),
        certificate.getId()
    );
}

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    private void downloadCertificate(int certificateId, HttpServletResponse response) 
            throws IOException, SQLException {
        try {
            Optional<byte[]> certificateFileOpt = certificateDAO.getCertificateFile(certificateId);
            if (certificateFileOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate file not found");
                return;
            }

            byte[] fileData = certificateFileOpt.get();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", 
                "attachment; filename=certificate_" + certificateId + ".pdf");
            response.setContentLength(fileData.length);

            try (OutputStream out = response.getOutputStream()) {
                out.write(fileData);
            }
        } catch (Exception e) {
            logError("Error in downloadCertificate", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to download certificate");
        }
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    private void logError(String message, Exception e) {
        getServletContext().log(message, e);
        e.printStackTrace();
    }

    private void logError(Exception e) {
        logError("Error in CertificateServlet", e);
    }

    @Override
    public void destroy() {
        try {
            if (certificateDAO != null) {
                certificateDAO.close();
            }
        } catch (Exception e) {
            logError(e);
        }
    }
}