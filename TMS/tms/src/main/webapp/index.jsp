<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Training Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
    <jsp:include page="/WEB-INF/includes/header.jsp" />
    
    <div class="container my-5">
        <div class="jumbotron bg-white p-4 p-md-5 rounded shadow-sm">
            <div class="row align-items-center">
                <div class="col-lg-8">
                    <h1 class="display-4 fw-bold text-primary">Training Management System</h1>
                    <p class="lead">A comprehensive platform for managing training programs, courses, and enrollments.</p>
                    <hr class="my-4">
                    <p>
                        This system allows administrators to create and manage training programs, trainers to manage courses,
                        and trainees to enroll in available programs.
                    </p>
                    
                    <c:choose>
                        <c:when test="${empty sessionScope.currentUser}">
                            <div class="mt-4">
                                <a href="<c:url value='/login'/>" class="btn btn-primary btn-lg">
                                    <i class="fas fa-sign-in-alt me-2"></i>Login
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="mt-4">
                                <c:if test="${sessionScope.currentUser.role eq 'ADMIN'}">
                                    <a href="<c:url value='/admin/training-programs/form'/>" class="btn btn-primary">
                                        <i class="fas fa-plus-circle me-1"></i> Create Training Program
                                    </a>
                                    <a href="<c:url value='/admin/users'/>" class="btn btn-secondary">
                                        <i class="fas fa-users me-1"></i> Manage Users
                                    </a>
                                </c:if>
                                <c:if test="${sessionScope.currentUser.role eq 'TRAINER'}">
                                    <a href="<c:url value='/trainer/training-programs/form'/>" class="btn btn-primary">
                                        <i class="fas fa-plus-circle me-1"></i> Create Training Program
                                    </a>
                                </c:if>
                                <a href="<c:url value='/training-programs'/>" class="btn btn-outline-primary">
                                    <i class="fas fa-list-alt me-1"></i> View All Programs
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="col-lg-4 d-none d-lg-block text-center">
                    <img src="<c:url value='/assets/images/training.svg'/>" alt="Training Illustration" class="img-fluid" style="max-height: 300px;">
                </div>
            </div>
        </div>
        
        <!-- Active Training Programs -->
        <div class="mt-5">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="border-start border-4 border-primary ps-3">Active Training Programs</h2>
                <a href="<c:url value='/training-programs'/>" class="btn btn-sm btn-outline-primary">
                    View All <i class="fas fa-arrow-right ms-1"></i>
                </a>
            </div>
            
            <div class="row">
                <c:choose>
                    <c:when test="${empty activePrograms}">
                        <div class="col-12">
                            <div class="alert alert-info">
                                <i class="fas fa-info-circle me-2"></i> No active training programs available at the moment.
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="program" items="${activePrograms}">
                            <div class="col-md-4 mb-4">
                                <div class="card h-100">
                                    <div class="card-header d-flex justify-content-between align-items-center">
                                        <h5 class="card-title mb-0">${program.title}</h5>
                                        <span class="status-pill status-active">Active</span>
                                    </div>
                                    <div class="card-body">
                                        <p class="card-text">${program.description}</p>
                                        <p>
                                            <i class="fas fa-calendar-alt text-primary me-2"></i>
                                            <strong>Start Date:</strong> ${program.startDate}<br>
                                            <i class="fas fa-flag-checkered text-primary me-2"></i>
                                            <strong>End Date:</strong> ${program.endDate}
                                        </p>
                                    </div>
                                    <div class="card-footer bg-white">
                                        <a href="<c:url value='/training-programs/details?id=${program.id}'/>" class="btn btn-primary w-100">
                                            <i class="fas fa-info-circle me-1"></i> View Details
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        
        <!-- Features Section -->
        <div class="mt-5 pt-3">
            <h2 class="text-center mb-5">Key Features</h2>
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card h-100 text-center">
                        <div class="card-body">
                            <div class="mb-3 text-primary">
                                <i class="fas fa-chalkboard-teacher fa-3x"></i>
                            </div>
                            <h4 class="card-title">Training Programs</h4>
                            <p class="card-text">Create and manage comprehensive training programs with multiple courses and sessions.</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 text-center">
                        <div class="card-body">
                            <div class="mb-3 text-primary">
                                <i class="fas fa-user-graduate fa-3x"></i>
                            </div>
                            <h4 class="card-title">Enrollments</h4>
                            <p class="card-text">Manage trainee enrollments, track progress, and monitor completion rates.</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 text-center">
                        <div class="card-body">
                            <div class="mb-3 text-primary">
                                <i class="fas fa-certificate fa-3x"></i>
                            </div>
                            <h4 class="card-title">Certification</h4>
                            <p class="card-text">Generate and distribute certificates upon successful program completion.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/includes/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<c:url value='/assets/js/script.js'/>"></script>
</body>
</html> 