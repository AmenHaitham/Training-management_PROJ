<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trainee Profile - Training Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
    <jsp:include page="/WEB-INF/includes/header.jsp" />
    
    <div class="container py-4">
        <!-- Page Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="border-start border-4 border-primary ps-3">Trainee Profile</h1>
            <div>
                <a href="<c:url value='/change-password'/>" class="btn btn-outline-primary me-2">
                    <i class="fas fa-key me-1"></i> Change Password
                </a>
                <a href="<c:url value='/trainee/certificates'/>" class="btn btn-primary">
                    <i class="fas fa-certificate me-1"></i> My Certificates
                </a>
            </div>
        </div>
        
        <div class="row">
            <!-- Profile Information -->
            <div class="col-lg-4 mb-4">
                <div class="card h-100">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0"><i class="fas fa-user-circle me-2"></i>Personal Information</h5>
                        <a href="<c:url value='/profile/edit'/>" class="btn btn-sm btn-outline-primary">
                            <i class="fas fa-edit"></i> Edit
                        </a>
                    </div>
                    <div class="card-body">
                        <div class="text-center mb-4">
                            <div class="avatar-circle mb-3 mx-auto">
                                <span class="avatar-initials">${user.firstName.charAt(0)}${user.lastName.charAt(0)}</span>
                            </div>
                            <h4 class="mb-0">${user.firstName} ${user.lastName}</h4>
                            <p class="text-muted">Trainee</p>
                        </div>
                        
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <span><i class="fas fa-envelope me-2 text-primary"></i> Email</span>
                                <span class="text-muted">${user.email}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <span><i class="fas fa-user me-2 text-primary"></i> Username</span>
                                <span class="text-muted">${user.username}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <span><i class="fas fa-graduation-cap me-2 text-primary"></i> Enrollments</span>
                                <span class="badge rounded-pill bg-primary">${enrollments.size()}</span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            
            <!-- Current Enrollments -->
            <div class="col-lg-8">
                <div class="card h-100">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0"><i class="fas fa-book-reader me-2"></i>My Enrollments</h5>
                        <a href="<c:url value='/training-programs'/>" class="btn btn-sm btn-primary">
                            <i class="fas fa-plus"></i> Enroll in Program
                        </a>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th scope="col">Program</th>
                                        <th scope="col">Status</th>
                                        <th scope="col">Enrolled Date</th>
                                        <th scope="col">Progress</th>
                                        <th scope="col">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty enrollments}">
                                            <tr>
                                                <td colspan="5" class="text-center py-4">
                                                    <div class="alert alert-info mb-0">
                                                        <i class="fas fa-info-circle me-2"></i> You are not enrolled in any training programs yet.
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="enrollment" items="${enrollments}">
                                                <tr>
                                                    <td>${enrollment.trainingProgram.title}</td>
                                                    <td>
                                                        <span class="badge rounded-pill bg-${enrollment.status eq 'APPROVED' ? 'success' : enrollment.status eq 'PENDING' ? 'warning' : enrollment.status eq 'COMPLETED' ? 'info' : 'secondary'}">
                                                            ${enrollment.status}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <fmt:formatDate value="${enrollment.enrollmentDate}" pattern="MMM dd, yyyy" />
                                                    </td>
                                                    <td>
                                                        <div class="progress" style="height: 8px;">
                                                            <div class="progress-bar" role="progressbar" style="width: ${enrollment.progress}%;" 
                                                                aria-valuenow="${enrollment.progress}" aria-valuemin="0" aria-valuemax="100"></div>
                                                        </div>
                                                        <small class="text-muted">${enrollment.progress}% Complete</small>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group" role="group">
                                                            <a href="<c:url value='/training-programs/details?id=${enrollment.trainingProgram.id}'/>" class="btn btn-sm btn-outline-primary" 
                                                               data-bs-toggle="tooltip" title="View Program">
                                                                <i class="fas fa-eye"></i>
                                                            </a>
                                                            <a href="<c:url value='/trainee/sessions?programId=${enrollment.trainingProgram.id}'/>" class="btn btn-sm btn-outline-primary" 
                                                               data-bs-toggle="tooltip" title="View Sessions">
                                                                <i class="fas fa-calendar-day"></i>
                                                            </a>
                                                            <c:if test="${enrollment.status eq 'COMPLETED'}">
                                                                <a href="<c:url value='/trainee/certificates?enrollmentId=${enrollment.id}'/>" class="btn btn-sm btn-outline-success" 
                                                                   data-bs-toggle="tooltip" title="View Certificate">
                                                                    <i class="fas fa-certificate"></i>
                                                                </a>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Upcoming Sessions -->
        <div class="card mt-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-calendar-alt me-2"></i>Upcoming Sessions</h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty upcomingSessions}">
                        <div class="alert alert-info mb-0">
                            <i class="fas fa-info-circle me-2"></i> You don't have any upcoming sessions scheduled.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row row-cols-1 row-cols-md-3 g-4">
                            <div class="col">
                                <div class="card h-100">
                                    <div class="card-body">
                                        <h5 class="card-title">Introduction to Java</h5>
                                        <p class="card-text">
                                            <i class="far fa-calendar-alt text-primary me-2"></i> May 20, 2023<br>
                                            <i class="far fa-clock text-primary me-2"></i> 10:00 AM - 12:00 PM<br>
                                            <i class="fas fa-chalkboard-teacher text-primary me-2"></i> John Smith
                                        </p>
                                        <a href="#" class="btn btn-sm btn-outline-primary mt-2">View Details</a>
                                    </div>
                                </div>
                            </div>
                            <div class="col">
                                <div class="card h-100">
                                    <div class="card-body">
                                        <h5 class="card-title">Database Design Basics</h5>
                                        <p class="card-text">
                                            <i class="far fa-calendar-alt text-primary me-2"></i> May 22, 2023<br>
                                            <i class="far fa-clock text-primary me-2"></i> 2:00 PM - 4:00 PM<br>
                                            <i class="fas fa-chalkboard-teacher text-primary me-2"></i> Jane Doe
                                        </p>
                                        <a href="#" class="btn btn-sm btn-outline-primary mt-2">View Details</a>
                                    </div>
                                </div>
                            </div>
                            <div class="col">
                                <div class="card h-100">
                                    <div class="card-body">
                                        <h5 class="card-title">Web Development</h5>
                                        <p class="card-text">
                                            <i class="far fa-calendar-alt text-primary me-2"></i> May 25, 2023<br>
                                            <i class="far fa-clock text-primary me-2"></i> 1:00 PM - 3:00 PM<br>
                                            <i class="fas fa-chalkboard-teacher text-primary me-2"></i> Robert Johnson
                                        </p>
                                        <a href="#" class="btn btn-sm btn-outline-primary mt-2">View Details</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/includes/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<c:url value='/assets/js/script.js'/>"></script>
    
    <style>
        .avatar-circle {
            width: 100px;
            height: 100px;
            background-color: var(--primary-color);
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        
        .avatar-initials {
            color: white;
            font-size: 40px;
            font-weight: bold;
        }
    </style>
</body>
</html> 