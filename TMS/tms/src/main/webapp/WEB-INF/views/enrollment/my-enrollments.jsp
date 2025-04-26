<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Enrollments - Training Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/shared/header.jsp" />
    
    <main class="container my-4">
        <div class="row mb-4">
            <div class="col-md-6">
                <h1>My Enrollments</h1>
                <p class="text-muted">Training programs you are currently enrolled in or have completed</p>
            </div>
            <div class="col-md-6 text-end">
                <a href="${pageContext.request.contextPath}/enrollment/enroll" class="btn btn-primary">
                    <i class="bi bi-plus-circle"></i> Enroll in New Program
                </a>
            </div>
        </div>
        
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="successMessage" scope="session" />
        </c:if>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        
        <c:choose>
            <c:when test="${empty enrollments}">
                <div class="card shadow-sm">
                    <div class="card-body text-center p-5">
                        <i class="bi bi-journal-bookmark-fill fs-1 text-muted mb-3"></i>
                        <h3>You're not enrolled in any training programs yet</h3>
                        <p class="mb-4">Browse available programs and enroll to start your learning journey.</p>
                        <a href="${pageContext.request.contextPath}/programs" class="btn btn-primary">Browse Programs</a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                    <c:forEach items="${enrollments}" var="enrollment">
                        <div class="col">
                            <div class="card h-100 shadow-sm">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <span class="status-pill status-${enrollment.status.toLowerCase()}">${enrollment.status}</span>
                                    <small class="text-muted">
                                        Enrolled: <fmt:formatDate value="${enrollment.enrolledAt}" pattern="MMM dd, yyyy" />
                                    </small>
                                </div>
                                <div class="card-body">
                                    <h5 class="card-title">
                                        <c:if test="${not empty enrollment.trainingProgram}">
                                            ${enrollment.trainingProgram.title}
                                        </c:if>
                                    </h5>
                                    <c:if test="${not empty enrollment.trainingProgram}">
                                        <p class="card-text">${enrollment.trainingProgram.description}</p>
                                    </c:if>
                                    <c:if test="${enrollment.status == 'COMPLETED' && not empty enrollment.completedAt}">
                                        <p class="text-success">
                                            <i class="bi bi-check-circle-fill"></i> 
                                            Completed on <fmt:formatDate value="${enrollment.completedAt}" pattern="MMM dd, yyyy" />
                                        </p>
                                    </c:if>
                                </div>
                                <div class="card-footer">
                                    <c:if test="${not empty enrollment.trainingProgram}">
                                        <a href="${pageContext.request.contextPath}/program/view/${enrollment.trainingProgramId}" class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-info-circle"></i> Program Details
                                        </a>
                                    </c:if>
                                    <a href="${pageContext.request.contextPath}/enrollment/view/${enrollment.id}" class="btn btn-sm btn-outline-secondary">
                                        <i class="bi bi-journal-text"></i> Enrollment Details
                                    </a>
                                    <c:if test="${enrollment.status == 'ACTIVE'}">
                                        <button type="button" 
                                                class="btn btn-sm btn-outline-danger float-end btn-delete"
                                                onclick="if(confirm('Are you sure you want to drop this program?')) { 
                                                    window.location='${pageContext.request.contextPath}/enrollment/status/${enrollment.id}?status=DROPPED'; 
                                                } return false;">
                                            <i class="bi bi-x-circle"></i> Drop
                                        </button>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
    
    <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html> 