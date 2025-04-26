<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>${editing ? 'Edit' : 'Schedule'} Session</title>
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
                <li class="breadcrumb-item active">${editing ? 'Edit' : 'Schedule'} Session</li>
            </ol>
        </nav>
        
        <!-- Page Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1>${editing ? 'Edit' : 'Schedule'} Session</h1>
        </div>
        
        <!-- Error messages -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <!-- Course Selection Form (if no course selected) -->
        <c:if test="${selectCourse}">
            <div class="card mb-4">
                <div class="card-header bg-light">
                    <h5 class="mb-0">Select Course</h5>
                </div>
                <div class="card-body">
                    <p>Please select a course for this session:</p>
                    
                    <div class="row row-cols-1 row-cols-md-3 g-4">
                        <c:forEach var="course" items="${courses}">
                            <div class="col">
                                <div class="card h-100">
                                    <div class="card-body">
                                        <h5 class="card-title">${course.title}</h5>
                                        <p class="card-text">
                                            <span class="badge status-pill status-${course.status.toLowerCase()}">${course.status}</span>
                                        </p>
                                        <a href="${pageContext.request.contextPath}/session/form?courseId=${course.id}" 
                                           class="btn btn-outline-primary">
                                            Select Course
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>
        
        <!-- Session Form -->
        <c:if test="${not selectCourse}">
            <div class="card">
                <div class="card-header bg-light">
                    <h5 class="mb-0">${editing ? 'Edit' : 'Schedule'} Session</h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/session/${editing ? 'update/'.concat(session.id) : 'create'}" 
                          method="post" class="form-container needs-validation" novalidate>
                        
                        <input type="hidden" name="courseId" value="${session.courseId}">
                        
                        <div class="row mb-3">
                            <div class="col-md-12">
                                <label for="title" class="form-label">Session Title <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="title" name="title" value="${session.title}" required>
                                <div class="invalid-feedback">
                                    Please provide a session title.
                                </div>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="3">${session.description}</textarea>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-4">
                                <label for="sessionDate" class="form-label">Date <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" id="sessionDate" name="sessionDate" 
                                       value="<fmt:formatDate value="${session.sessionDate}" pattern="yyyy-MM-dd" />" 
                                       required>
                                <div class="invalid-feedback">
                                    Please select a date.
                                </div>
                            </div>
                            <div class="col-md-4">
                                <label for="startTime" class="form-label">Start Time <span class="text-danger">*</span></label>
                                <input type="time" class="form-control" id="startTime" name="startTime" 
                                       value="<fmt:formatDate value="${session.startTime}" pattern="HH:mm" />" 
                                       required>
                                <div class="invalid-feedback">
                                    Please specify a start time.
                                </div>
                            </div>
                            <div class="col-md-4">
                                <label for="endTime" class="form-label">End Time <span class="text-danger">*</span></label>
                                <input type="time" class="form-control" id="endTime" name="endTime" 
                                       value="<fmt:formatDate value="${session.endTime}" pattern="HH:mm" />" 
                                       required>
                                <div class="invalid-feedback">
                                    Please specify an end time.
                                </div>
                            </div>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="location" class="form-label">Location <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="location" name="location" value="${session.location}" required>
                                <div class="invalid-feedback">
                                    Please provide a location.
                                </div>
                            </div>
                            <div class="col-md-6">
                                <label for="trainerId" class="form-label">Trainer</label>
                                <select class="form-select" id="trainerId" name="trainerId">
                                    <option value="">Select Trainer</option>
                                    <c:forEach var="trainer" items="${trainers}">
                                        <option value="${trainer.id}" ${session.trainerId == trainer.id ? 'selected' : ''}>
                                            ${trainer.firstName} ${trainer.lastName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="materials" class="form-label">Materials/Resources</label>
                            <textarea class="form-control" id="materials" name="materials" rows="2">${session.materials}</textarea>
                            <div class="form-text">List any materials or resources needed for this session.</div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="sessionStatus" class="form-label">Status <span class="text-danger">*</span></label>
                            <select class="form-select" id="sessionStatus" name="sessionStatus" required>
                                <option value="">Select Status</option>
                                <option value="SCHEDULED" ${session.sessionStatus == 'SCHEDULED' ? 'selected' : ''}>Scheduled</option>
                                <option value="ONGOING" ${session.sessionStatus == 'ONGOING' ? 'selected' : ''}>Ongoing</option>
                                <option value="COMPLETED" ${session.sessionStatus == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                                <option value="CANCELLED" ${session.sessionStatus == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                            </select>
                            <div class="invalid-feedback">
                                Please select a status.
                            </div>
                        </div>
                        
                        <div class="d-grid gap-2 d-md-flex justify-content-md-end mt-4">
                            <a href="${pageContext.request.contextPath}/sessions${not empty session.courseId ? '?courseId='.concat(session.courseId) : ''}" 
                               class="btn btn-secondary me-md-2">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                ${editing ? 'Update' : 'Schedule'} Session
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>
    </div>
    
    <jsp:include page="../../includes/footer.jsp" />
    
    <script>
        // Initialize date inputs with min values
        document.addEventListener('DOMContentLoaded', function() {
            // Set min date for session date to today
            const sessionDateInput = document.getElementById('sessionDate');
            if (sessionDateInput) {
                const today = new Date().toISOString().split('T')[0];
                
                // Only set min date if this is a new session
                if (!${editing}) {
                    sessionDateInput.min = today;
                }
            }
            
            // Validate time inputs (end time must be after start time)
            const startTimeInput = document.getElementById('startTime');
            const endTimeInput = document.getElementById('endTime');
            
            if (startTimeInput && endTimeInput) {
                const validateTimes = function() {
                    const startTime = startTimeInput.value;
                    const endTime = endTimeInput.value;
                    
                    if (startTime && endTime && startTime >= endTime) {
                        endTimeInput.setCustomValidity('End time must be after start time');
                    } else {
                        endTimeInput.setCustomValidity('');
                    }
                };
                
                startTimeInput.addEventListener('change', validateTimes);
                endTimeInput.addEventListener('change', validateTimes);
            }
        });
    </script>
</body>
</html> 