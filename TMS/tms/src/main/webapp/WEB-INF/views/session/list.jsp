<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Training Sessions</title>
    <jsp:include page="../../includes/header.jsp" />
</head>
<body>
    <jsp:include page="../../includes/navbar.jsp" />
    
    <div class="container mt-4">
        <div class="row mb-4">
            <div class="col">
                <h1>Training Sessions</h1>
                
                <c:if test="${not empty course}">
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/programs">Programs</a></li>
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
                            <li class="breadcrumb-item active">Sessions</li>
                        </ol>
                    </nav>
                </c:if>
            </div>
            <div class="col-auto">
                <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                    <c:choose>
                        <c:when test="${not empty courseId}">
                            <a href="${pageContext.request.contextPath}/session/form?courseId=${courseId}" class="btn btn-primary">
                                <i class="bi bi-plus-circle"></i> Schedule Session
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/session/form" class="btn btn-primary">
                                <i class="bi bi-plus-circle"></i> Schedule Session
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>
        </div>
        
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
        
        <!-- Filters -->
        <div class="card mb-4">
            <div class="card-header bg-light">
                <h5 class="mb-0">Filter Sessions</h5>
            </div>
            <div class="card-body">
                <form id="filterForm" action="${pageContext.request.contextPath}/sessions" method="get" class="row g-3">
                    <div class="col-md-6">
                        <label for="courseId" class="form-label">Course</label>
                        <select class="form-select" id="courseId" name="courseId">
                            <option value="">All Courses</option>
                            <c:forEach var="courseOption" items="${courses}">
                                <option value="${courseOption.id}" ${courseId == courseOption.id ? 'selected' : ''}>
                                    ${courseOption.title}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="trainerId" class="form-label">Trainer</label>
                        <select class="form-select" id="trainerId" name="trainerId">
                            <option value="">All Trainers</option>
                            <c:forEach var="trainerOption" items="${trainers}">
                                <option value="${trainerOption.id}" ${trainerId == trainerOption.id ? 'selected' : ''}>
                                    ${trainerOption.firstName} ${trainerOption.lastName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary">Apply Filters</button>
                        <a href="${pageContext.request.contextPath}/sessions" class="btn btn-secondary">Reset</a>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Calendar View Toggle -->
        <div class="d-flex justify-content-end mb-3">
            <div class="btn-group" role="group">
                <button type="button" class="btn btn-outline-primary active" id="listViewBtn">
                    <i class="bi bi-list-ul"></i> List View
                </button>
                <button type="button" class="btn btn-outline-primary" id="calendarViewBtn">
                    <i class="bi bi-calendar3"></i> Calendar View
                </button>
            </div>
        </div>
        
        <!-- List View -->
        <div id="listView" class="card">
            <div class="card-header bg-light d-flex justify-content-between align-items-center">
                <h5 class="mb-0">Sessions</h5>
                <span class="badge bg-primary">${sessions.size()} sessions found</span>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty sessions}">
                        <div class="alert alert-info">
                            No sessions found matching the search criteria.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-striped table-hover">
                                <thead>
                                    <tr>
                                        <th>Title</th>
                                        <th>Course</th>
                                        <th>Date</th>
                                        <th>Time</th>
                                        <th>Trainer</th>
                                        <th>Location</th>
                                        <th>Status</th>
                                        <th class="text-center">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="session" items="${sessions}">
                                        <tr>
                                            <td>${session.title}</td>
                                            <td>
                                                <c:if test="${not empty session.course}">
                                                    <a href="${pageContext.request.contextPath}/course/view?id=${session.courseId}">
                                                        ${session.course.title}
                                                    </a>
                                                </c:if>
                                                <c:if test="${empty session.course}">
                                                    <a href="${pageContext.request.contextPath}/course/view?id=${session.courseId}">
                                                        View Course
                                                    </a>
                                                </c:if>
                                            </td>
                                            <td><fmt:formatDate value="${session.sessionDate}" pattern="dd MMM yyyy" /></td>
                                            <td>
                                                <fmt:formatDate value="${session.startTime}" pattern="HH:mm" /> - 
                                                <fmt:formatDate value="${session.endTime}" pattern="HH:mm" />
                                            </td>
                                            <td>
                                                <c:if test="${not empty session.trainer}">
                                                    ${session.trainer.firstName} ${session.trainer.lastName}
                                                </c:if>
                                                <c:if test="${empty session.trainer}">
                                                    <span class="text-muted">Not assigned</span>
                                                </c:if>
                                            </td>
                                            <td>${session.location}</td>
                                            <td>
                                                <span class="badge status-pill status-${session.sessionStatus.toLowerCase()}">${session.sessionStatus}</span>
                                            </td>
                                            <td class="text-center">
                                                <div class="btn-group" role="group">
                                                    <a href="${pageContext.request.contextPath}/session/view?id=${session.id}"
                                                       class="btn btn-outline-primary btn-sm" data-bs-toggle="tooltip"
                                                       title="View session details">
                                                        <i class="bi bi-eye"></i>
                                                    </a>
                                                    
                                                    <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                                                        <a href="${pageContext.request.contextPath}/session/form?id=${session.id}"
                                                           class="btn btn-outline-secondary btn-sm" data-bs-toggle="tooltip"
                                                           title="Edit session">
                                                            <i class="bi bi-pencil"></i>
                                                        </a>
                                                    </c:if>
                                                    
                                                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                                        <button type="button" class="btn btn-outline-danger btn-sm btn-delete"
                                                                data-bs-toggle="tooltip" title="Delete session"
                                                                data-bs-session-id="${session.id}"
                                                                data-bs-session-title="${session.title}">
                                                            <i class="bi bi-trash"></i>
                                                        </button>
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
        
        <!-- Calendar View (hidden by default) -->
        <div id="calendarView" class="card" style="display: none;">
            <div class="card-header bg-light">
                <h5 class="mb-0">Calendar View</h5>
            </div>
            <div class="card-body">
                <div id="sessionCalendar"></div>
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
                    <form id="deleteForm" action="${pageContext.request.contextPath}/session/delete" method="post">
                        <input type="hidden" id="sessionId" name="sessionId">
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
                
                document.getElementById('sessionId').value = sessionId;
                document.getElementById('sessionTitle').textContent = sessionTitle;
                
                const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
                deleteModal.show();
            });
        });
        
        // View toggle functionality
        document.getElementById('listViewBtn').addEventListener('click', function() {
            document.getElementById('listView').style.display = 'block';
            document.getElementById('calendarView').style.display = 'none';
            this.classList.add('active');
            document.getElementById('calendarViewBtn').classList.remove('active');
        });
        
        document.getElementById('calendarViewBtn').addEventListener('click', function() {
            document.getElementById('listView').style.display = 'none';
            document.getElementById('calendarView').style.display = 'block';
            this.classList.add('active');
            document.getElementById('listViewBtn').classList.remove('active');
            
            // Initialize calendar if not already done
            initializeCalendar();
        });
        
        // Calendar initialization function
        function initializeCalendar() {
            // This would be implemented with a calendar library like FullCalendar
            // For now, we'll just show a placeholder message
            if (!document.getElementById('calendarInitialized')) {
                const calendarDiv = document.getElementById('sessionCalendar');
                calendarDiv.innerHTML = '<div class="alert alert-info">Calendar view will be implemented with a JavaScript calendar library.</div>';
                calendarDiv.id = 'calendarInitialized';
            }
        }
    </script>
</body>
</html> 