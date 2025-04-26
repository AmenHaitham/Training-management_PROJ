<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enroll in Training Program - Training Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/shared/header.jsp" />
    
    <main class="container my-4">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card shadow">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">
                            <i class="bi bi-journal-plus"></i> 
                            Enroll in Training Program
                        </h4>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                ${errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>
                        
                        <c:choose>
                            <c:when test="${not empty program}">
                                <!-- Show selected program details -->
                                <div class="mb-4">
                                    <div class="card">
                                        <div class="card-body">
                                            <div class="d-flex justify-content-between align-items-start">
                                                <h5 class="card-title">${program.title}</h5>
                                                <span class="status-pill status-${program.status.toLowerCase()}">${program.status}</span>
                                            </div>
                                            <p class="card-text">${program.description}</p>
                                            <div class="row g-3 mb-3">
                                                <div class="col-md-6">
                                                    <small class="text-muted d-block">Start Date</small>
                                                    <span><fmt:formatDate value="${program.startDate}" pattern="MMMM dd, yyyy" /></span>
                                                </div>
                                                <div class="col-md-6">
                                                    <small class="text-muted d-block">End Date</small>
                                                    <span><fmt:formatDate value="${program.endDate}" pattern="MMMM dd, yyyy" /></span>
                                                </div>
                                                <div class="col-md-6">
                                                    <small class="text-muted d-block">Course Count</small>
                                                    <span>${program.courseCount} courses</span>
                                                </div>
                                                <div class="col-md-6">
                                                    <small class="text-muted d-block">Total Duration</small>
                                                    <span>${program.totalDuration} hours</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                
                                <form action="${pageContext.request.contextPath}/enrollment/enroll" method="post">
                                    <input type="hidden" name="programId" value="${program.id}">
                                    
                                    <div class="form-check mb-3">
                                        <input class="form-check-input" type="checkbox" value="" id="terms" required>
                                        <label class="form-check-label" for="terms">
                                            I agree to the terms and conditions of this training program
                                        </label>
                                        <div class="invalid-feedback">
                                            You must agree before enrolling.
                                        </div>
                                    </div>
                                    
                                    <div class="d-grid gap-2 d-flex justify-content-between">
                                        <a href="${pageContext.request.contextPath}/programs" class="btn btn-outline-secondary">
                                            <i class="bi bi-arrow-left"></i> Back to Programs
                                        </a>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-check-circle"></i> Confirm Enrollment
                                        </button>
                                    </div>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <!-- Show list of available programs -->
                                <h5 class="mb-3">Available Training Programs</h5>
                                
                                <c:choose>
                                    <c:when test="${empty programs}">
                                        <div class="alert alert-info">
                                            No active training programs are currently available for enrollment.
                                        </div>
                                        <div class="text-center">
                                            <a href="${pageContext.request.contextPath}/programs" class="btn btn-primary">
                                                Browse All Programs
                                            </a>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="list-group mb-4">
                                            <c:forEach items="${programs}" var="program">
                                                <a href="${pageContext.request.contextPath}/enrollment/enroll?programId=${program.id}" 
                                                   class="list-group-item list-group-item-action">
                                                    <div class="d-flex w-100 justify-content-between">
                                                        <h5 class="mb-1">${program.title}</h5>
                                                        <small>
                                                            <span class="status-pill status-${program.status.toLowerCase()}">${program.status}</span>
                                                        </small>
                                                    </div>
                                                    <p class="mb-1">${program.description}</p>
                                                    <div class="d-flex justify-content-between">
                                                        <small class="text-muted">
                                                            <i class="bi bi-calendar"></i> 
                                                            <fmt:formatDate value="${program.startDate}" pattern="MMM dd, yyyy" /> - 
                                                            <fmt:formatDate value="${program.endDate}" pattern="MMM dd, yyyy" />
                                                        </small>
                                                        <small class="text-muted">
                                                            <i class="bi bi-book"></i> ${program.courseCount} courses
                                                        </small>
                                                    </div>
                                                </a>
                                            </c:forEach>
                                        </div>
                                        <div class="d-grid gap-2 d-md-flex justify-content-md-between">
                                            <a href="${pageContext.request.contextPath}/enrollment/my" class="btn btn-outline-secondary">
                                                <i class="bi bi-arrow-left"></i> Back to My Enrollments
                                            </a>
                                            <a href="${pageContext.request.contextPath}/programs" class="btn btn-outline-primary">
                                                <i class="bi bi-search"></i> View All Programs
                                            </a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html> 