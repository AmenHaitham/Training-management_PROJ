<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Session Details</title>
    <jsp:include page="../../includes/header.jsp" />
</head>
<body>
    <jsp:include page="../../includes/navbar.jsp" />
    
    <div class="container mt-4">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/programs">Programs</a></li>
                <c:if test="${not empty course}">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/program/view?id=${course.trainingProgramId}">
                            ${program.title}
                        </a>
                    </li>
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/course/view?id=${course.id}">
                            ${course.title}
                        </a>
                    </li>
                </c:if>
                <li class="breadcrumb-item">
                    <a href="${pageContext.request.contextPath}/sessions${not empty course ? '?courseId='.concat(course.id) : ''}">
                        Sessions
                    </a>
                </li>
                <li class="breadcrumb-item active">${session.title}</li>
            </ol>
        </nav>
        
        <!-- Alert Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="successMessage" scope="session" />
        </c:if>
        
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        
        <!-- Session Header -->
        <div class="row mb-4 align-items-center">
            <div class="col">
                <h1>${session.title}</h1>
                <span class="badge status-pill status-${session.sessionStatus.toLowerCase()}">${session.sessionStatus}</span>
            </div>
            
            <div class="col-auto">
                <div class="btn-group" role="group">
                    <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                        <a href="${pageContext.request.contextPath}/session/form?id=${session.id}" 
                           class="btn btn-outline-primary">
                            <i class="bi bi-pencil"></i> Edit Session
                        </a>
                        
                        <c:if test="${session.sessionStatus == 'SCHEDULED' && (sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER')}">
                            <form action="${pageContext.request.contextPath}/session/status/${session.id}" method="post">
                                <input type="hidden" name="status" value="ONGOING">
                                <button type="submit" class="btn btn-success">
                                    <i class="bi bi-play-circle"></i> Start Session
                                </button>
                            </form>
                        </c:if>
                        
                        <c:if test="${session.sessionStatus == 'ONGOING' && (sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER')}">
                            <form action="${pageContext.request.contextPath}/session/status/${session.id}" method="post">
                                <input type="hidden" name="status" value="COMPLETED">
                                <button type="submit" class="btn btn-info">
                                    <i class="bi bi-check-circle"></i> Complete Session
                                </button>
                            </form>
                        </c:if>
                        
                        <c:if test="${session.sessionStatus != 'CANCELLED' && session.sessionStatus != 'COMPLETED' && (sessionScope.currentUser.role == 'ADMIN')}">
                            <form action="${pageContext.request.contextPath}/session/status/${session.id}" method="post">
                                <input type="hidden" name="status" value="CANCELLED">
                                <button type="submit" class="btn btn-secondary">
                                    <i class="bi bi-x-circle"></i> Cancel Session
                                </button>
                            </form>
                        </c:if>
                    </c:if>
                    
                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                        <button type="button" class="btn btn-danger btn-delete"
                                data-bs-session-id="${session.id}"
                                data-bs-session-title="${session.title}">
                            <i class="bi bi-trash"></i> Delete
                        </button>
                    </c:if>
                </div>
            </div>
        </div>
        
        <!-- Session Details -->
        <div class="row">
            <div class="col-md-8">
                <div class="card mb-4">
                    <div class="card-header bg-light">
                        <h5 class="card-title mb-0">Session Information</h5>
                    </div>
                    <div class="card-body">
                        <div class="mb-4">
                            <h5>Description</h5>
                            <p>${not empty session.description ? session.description : 'No description provided.'}</p>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6">
                                <h5>Date</h5>
                                <p><i class="bi bi-calendar-event me-2"></i> <fmt:formatDate value="${session.sessionDate}" pattern="EEEE, MMMM d, yyyy" /></p>
                            </div>
                            <div class="col-md-6">
                                <h5>Time</h5>
                                <p><i class="bi bi-clock me-2"></i> <fmt:formatDate value="${session.startTime}" pattern="HH:mm" /> - <fmt:formatDate value="${session.endTime}" pattern="HH:mm" /></p>
                            </div>
                        </div>
                        
                        <div class="row mt-3">
                            <div class="col-md-6">
                                <h5>Location</h5>
                                <p><i class="bi bi-geo-alt me-2"></i> ${session.location}</p>
                            </div>
                            <div class="col-md-6">
                                <h5>Status</h5>
                                <p><span class="badge status-pill status-${session.sessionStatus.toLowerCase()}">${session.sessionStatus}</span></p>
                            </div>
                        </div>
                        
                        <c:if test="${not empty session.materials}">
                            <div class="mt-3">
                                <h5>Materials/Resources</h5>
                                <p>${session.materials}</p>
                            </div>
                        </c:if>
                    </div>
                </div>
                
                <!-- Attendance Section (Placeholder) -->
                <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                    <div class="card mb-4">
                        <div class="card-header bg-light d-flex justify-content-between align-items-center">
                            <h5 class="card-title mb-0">Attendance</h5>
                            <button class="btn btn-sm btn-primary" disabled>
                                <i class="bi bi-clipboard-check"></i> Mark Attendance
                            </button>
                        </div>
                        <div class="card-body">
                            <div class="alert alert-info">
                                Attendance tracking functionality will be implemented in a future update.
                            </div>
                        </div>
                    </div>
                </c:if>
                
                <!-- Feedback Section (Placeholder) -->
                <div class="card mb-4">
                    <div class="card-header bg-light d-flex justify-content-between align-items-center">
                        <h5 class="card-title mb-0">Feedback</h5>
                        <c:if test="${sessionScope.currentUser.role == 'TRAINEE' && session.sessionStatus == 'COMPLETED'}">
                            <button class="btn btn-sm btn-primary" disabled>
                                <i class="bi bi-star"></i> Leave Feedback
                            </button>
                        </c:if>
                    </div>
                    <div class="card-body">
                        <div class="alert alert-info">
                            Feedback functionality will be implemented in a future update.
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4">
                <!-- Course Information -->
                <div class="card mb-4">
                    <div class="card-header bg-light">
                        <h5 class="card-title mb-0">Course</h5>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty course}">
                            <h5>${course.title}</h5>
                            <p class="text-muted">
                                <span class="badge status-pill status-${course.status.toLowerCase()}">${course.status}</span>
                            </p>
                            <a href="${pageContext.request.contextPath}/course/view?id=${course.id}" 
                               class="btn btn-outline-primary btn-sm">
                                View Course Details
                            </a>
                        </c:if>
                        <c:if test="${empty course}">
                            <p class="text-muted">Course information not available.</p>
                        </c:if>
                    </div>
                </div>
                
                <!-- Trainer Information -->
                <div class="card mb-4">
                    <div class="card-header bg-light">
                        <h5 class="card-title mb-0">Trainer</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty trainer}">
                                <div class="d-flex align-items-center">
                                    <div class="flex-shrink-0">
                                        <i class="bi bi-person-circle fs-1"></i>
                                    </div>
                                    <div class="flex-grow-1 ms-3">
                                        <h5 class="mb-0">${trainer.firstName} ${trainer.lastName}</h5>
                                        <p class="text-muted mb-0">${trainer.email}</p>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted">No trainer assigned to this session yet.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <!-- Related Sessions (if applicable) -->
                <div class="card">
                    <div class="card-header bg-light">
                        <h5 class="card-title mb-0">Other Sessions in This Course</h5>
                    </div>
                    <div class="card-body">
                        <div class="alert alert-info">
                            This feature will be implemented in a future update.
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">Confirm Delete</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to delete the session <strong id="sessionTitle"></strong>?
                    This action cannot be undone.
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form id="deleteForm" action="${pageContext.request.contextPath}/session/delete/${session.id}" method="post">
                        <button type="submit" class="btn btn-danger">Delete</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="../../includes/footer.jsp" />
    
    <script>
        // Handle delete button click
        document.querySelectorAll('.btn-delete').forEach(button => {
            button.addEventListener('click', function() {
                const sessionId = this.getAttribute('data-bs-session-id');
                const sessionTitle = this.getAttribute('data-bs-session-title');
                
                document.getElementById('sessionTitle').textContent = sessionTitle;
                
                const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
                deleteModal.show();
            });
        });
    </script>
</body>
</html> 