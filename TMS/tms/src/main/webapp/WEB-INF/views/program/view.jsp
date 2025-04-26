<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${program.title} | Training Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="/WEB-INF/views/common/header.jsp" />
    
    <main class="container my-4">
        <!-- Program Details Card -->
        <div class="card shadow-sm mb-4">
            <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                <h2 class="h4 mb-0">
                    <i class="bi bi-mortarboard-fill me-2"></i> ${program.title}
                </h2>
                <div>
                    <span class="badge rounded-pill status-pill-${program.status.toLowerCase()}">${program.status}</span>
                </div>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-8">
                        <h3 class="h5 border-bottom pb-2 mb-3">Program Details</h3>
                        <p class="lead">${program.description}</p>
                        
                        <div class="row mt-4">
                            <div class="col-md-6">
                                <h4 class="h6">Program Duration</h4>
                                <p>
                                    <i class="bi bi-calendar-event text-primary me-2"></i>
                                    <strong>Start Date:</strong> 
                                    <fmt:formatDate value="${program.startDate}" pattern="MMMM dd, yyyy" />
                                </p>
                                <p>
                                    <i class="bi bi-calendar-check text-primary me-2"></i>
                                    <strong>End Date:</strong> 
                                    <fmt:formatDate value="${program.endDate}" pattern="MMMM dd, yyyy" />
                                </p>
                            </div>
                            <div class="col-md-6">
                                <h4 class="h6">Program Information</h4>
                                <p>
                                    <i class="bi bi-person-circle text-primary me-2"></i>
                                    <strong>Created By:</strong> 
                                    ${program.createdByUser != null ? program.createdByUser.firstName : 'System'} 
                                    ${program.createdByUser != null ? program.createdByUser.lastName : ''}
                                </p>
                                <p>
                                    <i class="bi bi-clock-history text-primary me-2"></i>
                                    <strong>Created:</strong> 
                                    <fmt:formatDate value="${program.createdAt}" pattern="MMM dd, yyyy HH:mm" />
                                </p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card h-100">
                            <div class="card-header bg-light">
                                <h3 class="h5 mb-0">Program Stats</h3>
                            </div>
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <span><i class="bi bi-book text-primary me-2"></i> Courses:</span>
                                    <span class="badge bg-primary rounded-pill">${program.coursesCount != null ? program.coursesCount : 0}</span>
                                </div>
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <span><i class="bi bi-people text-primary me-2"></i> Enrollments:</span>
                                    <span class="badge bg-primary rounded-pill">${program.enrollmentsCount != null ? program.enrollmentsCount : 0}</span>
                                </div>
                                <div class="d-flex justify-content-between align-items-center">
                                    <span><i class="bi bi-calendar2-week text-primary me-2"></i> Sessions:</span>
                                    <span class="badge bg-primary rounded-pill">${program.sessionsCount != null ? program.sessionsCount : 0}</span>
                                </div>
                            </div>
                            <div class="card-footer bg-light">
                                <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER' && sessionScope.currentUser.id == program.createdBy}">
                                    <div class="d-grid gap-2">
                                        <a href="${pageContext.request.contextPath}/program/edit/${program.id}" class="btn btn-primary btn-sm">
                                            <i class="bi bi-pencil"></i> Edit Program
                                        </a>
                                        <a href="${pageContext.request.contextPath}/course/create?programId=${program.id}" class="btn btn-success btn-sm">
                                            <i class="bi bi-plus-circle"></i> Add Course
                                        </a>
                                    </div>
                                </c:if>
                                <c:if test="${sessionScope.currentUser.role == 'TRAINEE' && program.status == 'ACTIVE'}">
                                    <div class="d-grid">
                                        <a href="${pageContext.request.contextPath}/enrollment/enroll?programId=${program.id}" class="btn btn-success btn-sm">
                                            <i class="bi bi-person-plus"></i> Enroll in Program
                                        </a>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Program Tabs -->
        <ul class="nav nav-tabs mb-4" id="programTabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="courses-tab" data-bs-toggle="tab" data-bs-target="#courses" type="button" role="tab">
                    <i class="bi bi-book me-1"></i> Courses
                </button>
            </li>
            <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="enrollments-tab" data-bs-toggle="tab" data-bs-target="#enrollments" type="button" role="tab">
                        <i class="bi bi-people me-1"></i> Enrollments
                    </button>
                </li>
            </c:if>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="sessions-tab" data-bs-toggle="tab" data-bs-target="#sessions" type="button" role="tab">
                    <i class="bi bi-calendar2-week me-1"></i> Sessions
                </button>
            </li>
        </ul>
        
        <div class="tab-content" id="programTabsContent">
            <!-- Courses Tab -->
            <div class="tab-pane fade show active" id="courses" role="tabpanel" aria-labelledby="courses-tab">
                <div class="card shadow-sm">
                    <div class="card-header bg-light d-flex justify-content-between align-items-center">
                        <h3 class="h5 mb-0">Program Courses</h3>
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                            <a href="${pageContext.request.contextPath}/course/create?programId=${program.id}" class="btn btn-sm btn-success">
                                <i class="bi bi-plus-circle"></i> Add Course
                            </a>
                        </c:if>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty program.courses}">
                                <div class="alert alert-info">
                                    <i class="bi bi-info-circle me-2"></i> No courses have been added to this program yet.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle">
                                        <thead>
                                            <tr>
                                                <th>Title</th>
                                                <th>Duration</th>
                                                <th>Category</th>
                                                <th>Trainer</th>
                                                <th>Status</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="course" items="${program.courses}">
                                                <tr>
                                                    <td>${course.title}</td>
                                                    <td>${course.duration} hours</td>
                                                    <td>${course.category}</td>
                                                    <td>${course.assignedTrainer.firstName} ${course.assignedTrainer.lastName}</td>
                                                    <td>
                                                        <span class="badge rounded-pill status-pill-${course.status.toLowerCase()}">${course.status}</span>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group btn-group-sm" role="group">
                                                            <a href="${pageContext.request.contextPath}/course/view/${course.id}" class="btn btn-outline-primary">
                                                                <i class="bi bi-eye"></i>
                                                            </a>
                                                            <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                                                                <a href="${pageContext.request.contextPath}/course/edit/${course.id}" class="btn btn-outline-primary">
                                                                    <i class="bi bi-pencil"></i>
                                                                </a>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            
            <!-- Enrollments Tab -->
            <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                <div class="tab-pane fade" id="enrollments" role="tabpanel" aria-labelledby="enrollments-tab">
                    <div class="card shadow-sm">
                        <div class="card-header bg-light">
                            <h3 class="h5 mb-0">Program Enrollments</h3>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${empty program.enrollments}">
                                    <div class="alert alert-info">
                                        <i class="bi bi-info-circle me-2"></i> No trainees are enrolled in this program yet.
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="table-responsive">
                                        <table class="table table-hover align-middle">
                                            <thead>
                                                <tr>
                                                    <th>Trainee</th>
                                                    <th>Enrollment Date</th>
                                                    <th>Status</th>
                                                    <th>Progress</th>
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="enrollment" items="${program.enrollments}">
                                                    <tr>
                                                        <td>${enrollment.trainee.firstName} ${enrollment.trainee.lastName}</td>
                                                        <td><fmt:formatDate value="${enrollment.enrollmentDate}" pattern="MMM dd, yyyy" /></td>
                                                        <td>
                                                            <span class="badge rounded-pill status-pill-${enrollment.status.toLowerCase()}">${enrollment.status}</span>
                                                        </td>
                                                        <td>
                                                            <div class="progress" style="height: 8px;">
                                                                <div class="progress-bar" role="progressbar" style="width: ${enrollment.progressPercentage}%;" 
                                                                     aria-valuenow="${enrollment.progressPercentage}" aria-valuemin="0" aria-valuemax="100"></div>
                                                            </div>
                                                            <small class="text-muted">${enrollment.progressPercentage}%</small>
                                                        </td>
                                                        <td>
                                                            <div class="btn-group btn-group-sm" role="group">
                                                                <a href="${pageContext.request.contextPath}/enrollment/view/${enrollment.id}" class="btn btn-outline-primary">
                                                                    <i class="bi bi-eye"></i>
                                                                </a>
                                                                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                                                    <button type="button" class="btn btn-outline-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                                                                        Status
                                                                    </button>
                                                                    <ul class="dropdown-menu">
                                                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/enrollment/updateStatus?id=${enrollment.id}&status=APPROVED">Approve</a></li>
                                                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/enrollment/updateStatus?id=${enrollment.id}&status=COMPLETED">Complete</a></li>
                                                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/enrollment/updateStatus?id=${enrollment.id}&status=DROPPED">Drop</a></li>
                                                                    </ul>
                                                                </c:if>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <!-- Sessions Tab -->
            <div class="tab-pane fade" id="sessions" role="tabpanel" aria-labelledby="sessions-tab">
                <div class="card shadow-sm">
                    <div class="card-header bg-light">
                        <h3 class="h5 mb-0">Program Sessions</h3>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty program.sessions}">
                                <div class="alert alert-info">
                                    <i class="bi bi-info-circle me-2"></i> No sessions have been scheduled for this program yet.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle">
                                        <thead>
                                            <tr>
                                                <th>Title</th>
                                                <th>Course</th>
                                                <th>Date & Time</th>
                                                <th>Duration</th>
                                                <th>Location</th>
                                                <th>Status</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="session" items="${program.sessions}">
                                                <tr>
                                                    <td>${session.title}</td>
                                                    <td>${session.course.title}</td>
                                                    <td><fmt:formatDate value="${session.scheduledDateTime}" pattern="MMM dd, yyyy HH:mm" /></td>
                                                    <td>${session.duration} minutes</td>
                                                    <td>${session.location}</td>
                                                    <td>
                                                        <span class="badge rounded-pill status-pill-${session.status.toLowerCase()}">${session.status}</span>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group btn-group-sm" role="group">
                                                            <a href="${pageContext.request.contextPath}/session/view/${session.id}" class="btn btn-outline-primary">
                                                                <i class="bi bi-eye"></i>
                                                            </a>
                                                            <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                                                                <a href="${pageContext.request.contextPath}/session/edit/${session.id}" class="btn btn-outline-primary">
                                                                    <i class="bi bi-pencil"></i>
                                                                </a>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html> 