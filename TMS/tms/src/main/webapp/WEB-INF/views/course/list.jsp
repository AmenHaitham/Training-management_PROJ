<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Courses</title>
    <jsp:include page="../../includes/header.jsp" />
</head>
<body>
    <jsp:include page="../../includes/navbar.jsp" />
    
    <div class="container mt-4">
        <div class="row mb-4">
            <div class="col">
                <h1>Course List</h1>
                
                <c:if test="${not empty program}">
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/programs">Programs</a></li>
                            <li class="breadcrumb-item">
                                <a href="${pageContext.request.contextPath}/program/view?id=${program.id}">
                                    ${program.title}
                                </a>
                            </li>
                            <li class="breadcrumb-item active">Courses</li>
                        </ol>
                    </nav>
                </c:if>
            </div>
            <div class="col-auto">
                <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                    <c:choose>
                        <c:when test="${not empty programId}">
                            <a href="${pageContext.request.contextPath}/course/form?programId=${programId}" class="btn btn-primary">
                                <i class="bi bi-plus-circle"></i> Add Course
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/course/form" class="btn btn-primary">
                                <i class="bi bi-plus-circle"></i> Add Course
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>
        </div>
        
        <!-- Filters -->
        <div class="card mb-4">
            <div class="card-header bg-light">
                <h5 class="mb-0">Filter Courses</h5>
            </div>
            <div class="card-body">
                <form id="filterForm" action="${pageContext.request.contextPath}/courses" method="get" class="row g-3">
                    <div class="col-md-4">
                        <label for="title" class="form-label">Title</label>
                        <input type="text" class="form-control" id="title" name="title" value="${title}">
                    </div>
                    <div class="col-md-4">
                        <label for="category" class="form-label">Category</label>
                        <input type="text" class="form-control" id="category" name="category" value="${category}">
                    </div>
                    <div class="col-md-4">
                        <label for="status" class="form-label">Status</label>
                        <select class="form-select" id="status" name="status">
                            <option value="">All</option>
                            <option value="DRAFT" ${status == 'DRAFT' ? 'selected' : ''}>Draft</option>
                            <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                            <option value="ARCHIVED" ${status == 'ARCHIVED' ? 'selected' : ''}>Archived</option>
                        </select>
                    </div>
                    
                    <c:if test="${not empty programId}">
                        <input type="hidden" name="programId" value="${programId}">
                    </c:if>
                    
                    <c:if test="${not empty trainerId}">
                        <input type="hidden" name="trainerId" value="${trainerId}">
                    </c:if>
                    
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary">Apply Filters</button>
                        <a href="${pageContext.request.contextPath}/courses" class="btn btn-secondary">Reset</a>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Results -->
        <div class="card">
            <div class="card-header bg-light d-flex justify-content-between align-items-center">
                <h5 class="mb-0">Results</h5>
                <span class="badge bg-primary">${courses.size()} courses found</span>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty courses}">
                        <div class="alert alert-info">
                            No courses found matching the search criteria.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-striped table-hover">
                                <thead>
                                    <tr>
                                        <th>Title</th>
                                        <th>Category</th>
                                        <th>Duration</th>
                                        <th>Status</th>
                                        <th>Program</th>
                                        <th class="text-center">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="course" items="${courses}">
                                        <tr>
                                            <td>${course.title}</td>
                                            <td>${course.category}</td>
                                            <td>${course.duration} hours</td>
                                            <td>
                                                <span class="badge status-pill status-${course.status.toLowerCase()}">${course.status}</span>
                                            </td>
                                            <td>
                                                <c:if test="${not empty course.trainingProgram}">
                                                    <a href="${pageContext.request.contextPath}/program/view?id=${course.trainingProgramId}">
                                                        ${course.trainingProgram.title}
                                                    </a>
                                                </c:if>
                                                <c:if test="${empty course.trainingProgram}">
                                                    <a href="${pageContext.request.contextPath}/program/view?id=${course.trainingProgramId}">
                                                        View Program
                                                    </a>
                                                </c:if>
                                            </td>
                                            <td class="text-center">
                                                <div class="btn-group" role="group">
                                                    <a href="${pageContext.request.contextPath}/course/view?id=${course.id}"
                                                       class="btn btn-outline-primary btn-sm" data-bs-toggle="tooltip"
                                                       title="View course details">
                                                        <i class="bi bi-eye"></i>
                                                    </a>
                                                    
                                                    <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                                                        <a href="${pageContext.request.contextPath}/course/form?id=${course.id}"
                                                           class="btn btn-outline-secondary btn-sm" data-bs-toggle="tooltip"
                                                           title="Edit course">
                                                            <i class="bi bi-pencil"></i>
                                                        </a>
                                                    </c:if>
                                                    
                                                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                                        <button type="button" class="btn btn-outline-danger btn-sm btn-delete"
                                                                data-bs-toggle="tooltip" title="Delete course"
                                                                data-bs-course-id="${course.id}"
                                                                data-bs-course-title="${course.title}">
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
                    Are you sure you want to delete the course <strong id="courseTitle"></strong>?
                    This action cannot be undone.
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form id="deleteForm" action="${pageContext.request.contextPath}/course/delete" method="post">
                        <input type="hidden" id="courseId" name="courseId">
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
                const courseId = this.getAttribute('data-bs-course-id');
                const courseTitle = this.getAttribute('data-bs-course-title');
                
                document.getElementById('courseId').value = courseId;
                document.getElementById('courseTitle').textContent = courseTitle;
                
                const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
                deleteModal.show();
            });
        });
    </script>
</body>
</html> 