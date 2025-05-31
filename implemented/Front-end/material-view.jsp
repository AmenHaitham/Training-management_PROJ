<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Material Details</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        body {
            background-color: #f5f5f5;
            color: #333;
            line-height: 1.6;
        }
        
        .container {
            max-width: 1000px;
            margin: 30px auto;
            padding: 20px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
        }
        
        .header h1 {
            color: #2c3e50;
        }
        
        .back-btn {
            background-color: #3498db;
            color: white;
            border: none;
            padding: 8px 15px;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
        }
        
        .back-btn:hover {
            background-color: #2980b9;
        }
        
        .back-btn i {
            margin-right: 5px;
        }
        
        .material-details {
            margin-bottom: 30px;
        }
        
        .detail-row {
            display: flex;
            margin-bottom: 15px;
        }
        
        .detail-label {
            font-weight: bold;
            width: 150px;
            color: #7f8c8d;
        }
        
        .detail-value {
            flex: 1;
        }
        
        .file-section {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 6px;
            margin-top: 30px;
        }
        
        .file-info {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .file-icon {
            font-size: 40px;
            color: #3498db;
            margin-right: 15px;
        }
        
        .file-meta {
            flex: 1;
        }
        
        .file-name {
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .file-size {
            color: #7f8c8d;
            font-size: 0.9em;
        }
        
        .download-btn {
            background-color: #2ecc71;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
        }
        
        .download-btn:hover {
            background-color: #27ae60;
        }
        
        .download-btn i {
            margin-right: 8px;
        }
        
        .no-file {
            color: #e74c3c;
            font-style: italic;
        }
        
        @media (max-width: 768px) {
            .container {
                margin: 15px;
                padding: 15px;
            }
            
            .detail-row {
                flex-direction: column;
            }
            
            .detail-label {
                width: 100%;
                margin-bottom: 5px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Material Details</h1>
            <a href="javascript:history.back()" class="back-btn">
                <i class="fas fa-arrow-left"></i> Back to List
            </a>
        </div>
        
        <div class="material-details">
            <div class="detail-row">
                <div class="detail-label">Title:</div>
                <div class="detail-value">${material.title}</div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Description:</div>
                <div class="detail-value">${not empty material.description ? material.description : 'N/A'}</div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Session:</div>
                <div class="detail-value">Session #${material.session.id}</div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Created At:</div>
                <div class="detail-value">${material.createdAt}</div>
            </div>
            
            <div class="detail-row">
                <div class="detail-label">Last Updated:</div>
                <div class="detail-value">${not empty material.updatedAt ? material.updatedAt : 'N/A'}</div>
            </div>
        </div>
        
        <div class="file-section">
            <h2><i class="fas fa-file-alt"></i> Attached File</h2>
            
            <c:choose>
                <c:when test="${not empty material.fileData}">
                    <div class="file-info">
                        <div class="file-icon">
                            <i class="fas fa-file-pdf"></i>
                        </div>
                        <div class="file-meta">
                            <div class="file-name">${material.title}</div>
                            <div class="file-size">${material.fileSize} bytes</div>
                        </div>
                        <a href="${pageContext.request.contextPath}/materials/${material.id}/file" class="download-btn">
                            <i class="fas fa-download"></i> Download File
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="no-file">No file attached to this material</div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>