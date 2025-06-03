package com.tms.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tms.DAO.MaterialDAO;
import com.tms.DAO.SessionDAO;
import com.tms.Model.Material;
import com.tms.Model.Session;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/api/materials/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 10,  // 10MB
    maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class MaterialServlet extends HttpServlet {

    private MaterialDAO materialDAO;
    private SessionDAO sessionDAO;

    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class,
            (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context)
                -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
        .registerTypeAdapter(LocalDate.class,
            (com.google.gson.JsonSerializer<LocalDate>) (src, typeOfSrc, context)
                -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
        .setPrettyPrinting()
        .create();

    @Override
    public void init() throws ServletException {
        try {
            materialDAO = new MaterialDAO();
            sessionDAO = new SessionDAO();
        } catch (SQLException e) {
            throw new ServletException("Failed to initialize DAOs", e);
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
                String sessionId = request.getParameter("sessionId");
                String searchTerm = request.getParameter("search");

                List<Material> materials;
                if (sessionId != null && !sessionId.isEmpty()) {
                    materials = materialDAO.getMaterialsBySessionId(Integer.parseInt(sessionId));
                } else if (searchTerm != null && !searchTerm.isEmpty()) {
                    materials = materialDAO.searchMaterials(searchTerm);
                } else {
                    materials = materialDAO.getAllMaterials();
                }
                response.getWriter().write(gson.toJson(materials));
            } else {
                String[] parts = pathInfo.split("/");
                if (parts.length == 2) {
                    int materialId = Integer.parseInt(parts[1]);
                    Optional<Material> material = materialDAO.getMaterialById(materialId);
                    if (material.isPresent()) {
                        response.getWriter().write(gson.toJson(material.get()));
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Material not found");
                    }
                } else if (parts.length == 3) {
                    int materialId = Integer.parseInt(parts[1]);
                    switch (parts[2]) {
                        case "file":
                            downloadMaterial(materialId, response);
                            break;
                        case "view":
                            Optional<Material> material = materialDAO.getMaterialById(materialId);
                            if (material.isPresent()) {
                                request.setAttribute("material", material.get());
                                request.getRequestDispatcher("/material-view.jsp")
       .forward(request, response);

                            } else {
                                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Material not found");
                            }
                            break;
                        default:
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action: " + parts[2]);
                            break;
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                }
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            int sessionId = Integer.parseInt(request.getParameter("sessionId"));
            Part filePart = request.getPart("file");

            Optional<Session> session = sessionDAO.getSessionById(sessionId);
            if (session.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid session ID");
                return;
            }

            Material material = new Material();
            material.setTitle(title);
            material.setDescription(description);
            material.setSession(session.get());
            material.setFileData(filePart.getInputStream().readAllBytes());

            Optional<Integer> materialId = materialDAO.createMaterial(material);
            if (materialId.isPresent()) {
                material.setId(materialId.get());
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(gson.toJson(material));
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create material");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Material ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                int materialId = Integer.parseInt(parts[1]);
                Optional<Material> existingMaterial = materialDAO.getMaterialById(materialId);
                if (existingMaterial.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Material not found");
                    return;
                }

                Material material = existingMaterial.get();
                material.setTitle(request.getParameter("title"));
                material.setDescription(request.getParameter("description"));

                int sessionId = Integer.parseInt(request.getParameter("sessionId"));
                Optional<Session> session = sessionDAO.getSessionById(sessionId);
                if (session.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid session ID");
                    return;
                }
                material.setSession(session.get());

                Part filePart = request.getPart("file");
                if (filePart != null && filePart.getSize() > 0) {
                    material.setFileData(filePart.getInputStream().readAllBytes());
                }

                if (materialDAO.updateMaterial(material)) {
                    response.getWriter().write(gson.toJson(material));
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update material");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Material ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                int materialId = Integer.parseInt(parts[1]);
                if (materialDAO.deleteMaterial(materialId)) {
                    response.getWriter().write("{\"success\":true,\"message\":\"Material deleted successfully\"}");
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Material not found");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void downloadMaterial(int materialId, HttpServletResponse response) throws IOException {
        try {
            Optional<byte[]> fileData = materialDAO.getMaterialFileData(materialId);
            if (fileData.isPresent()) {
                Optional<Material> material = materialDAO.getMaterialById(materialId);
                if (material.isPresent()) {
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition",
                            "attachment; filename=\"" + material.get().getTitle() + "\"");
                    response.setContentLength(fileData.get().length);
                    response.getOutputStream().write(fileData.get());
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Material not found");
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File data not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    public void destroy() {
        try {
            if (materialDAO != null) {
                materialDAO.close();
            }
            if (sessionDAO != null) {
                sessionDAO.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
