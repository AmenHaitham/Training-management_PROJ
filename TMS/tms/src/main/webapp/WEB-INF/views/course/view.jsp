<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>${course.title} - Course Details</title>
    <jsp:include page="../../includes/header.jsp" />
</head>
<body>
    <jsp:include page="../../includes/navbar.jsp" />
    
    <div class="container mt-4">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/programs">Programs</a></li>
                <c:if test="${not empty program}">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/program/view?id=${program.id}">
                            ${program.title}
                        </a>
                    </li>
                </c:if>
                <li class="breadcrumb-item">
                    <a href="${pageContext.request.contextPath}/courses?programId=${course.trainingProgramId}">
                        Courses
                    </a>
                </li>
                <li class="breadcrumb-item active">${course.title}</li>
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
        
        <!-- Course Header -->
        <div class="row mb-4 align-items-center">
            <div class="col">
                <h1>${course.title}</h1>
                <span class="badge status-pill status-${course.status.toLowerCase()}">${course.status}</span>
            </div>
            
            <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                <div class="col-auto">
                    <div class="btn-group" role="group">
                        <a href="${pageContext.request.contextPath}/course/form?id=${course.id}" 
                           class="btn btn-outline-primary">
                            <i class="bi bi-pencil"></i> Edit Course
                        </a>
                        
                        <c:if test="${course.status == 'DRAFT' && (sessionScope.currentUser.role == 'ADMIN')}">
                            <form action="${pageContext.request.contextPath}/course/status" method="post">
                                <input type="hidden" name="courseId" value="${course.id}">
                                <input type="hidden" name="status" value="ACTIVE">
                                <button type="submit" class="btn btn-success">
                                    <i class="bi bi-check-circle"></i> Activate
                                </button>
                            </form>
                        </c:if>
                        
                        <c:if test="${course.status == 'ACTIVE' && (sessionScope.currentUser.role == 'ADMIN')}">
                            <form action="${pageContext.request.contextPath}/course/status" method="post">
                                <input type="hidden" name="courseId" value="${course.id}">
                                <input type="hidden" name="status" value="ARCHIVED">
                                <button type="submit" class="btn btn-secondary">
                                    <i class="bi bi-archive"></i> Archive
                                </button>
                            </form>
                        </c:if>
                        
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                            <button type="button" class="btn btn-danger btn-delete"
                                    data-bs-course-id="${course.id}"
                                    data-bs-course-title="${course.title}">
                                <i class="bi bi-trash"></i> Delete
                            </button>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </div>
        
        <!-- Course Details -->
        <div class="row">
            <div class="col-md-8">
                <div class="card mb-4">
                    <div class="card-header bg-light">
                        <h5 class="card-title mb-0">Course Information</h5>
                    </div>
                    <div class="card-body">
                        <div class="mb-4">
                            <h5>Description</h5>
                            <p>${course.description}</p>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6">
                                <h5>Category</h5>
                                <p>${course.category}</p>
                            </div>
                            <div class="col-md-6">
                                <h5>Duration</h5>
                                <p>${course.duration} hours</p>
                            </div>
                        </div>
                        
                        <c:if test="${not empty course.price}">
                            <div class="row mt-3">
                                <div class="col-md-6">
                                    <h5>Price</h5>
                                    <p>$${course.price}</p>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
                
                <!-- Sessions Section Placeholder -->
                <div class="card mb-4">
                    <div class="card-header bg-light d-flex justify-content-between align-items-center">
                        <h5 class="card-title mb-0">Sessions</h5>
                        
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                            <a href="${pageContext.request.contextPath}/session/form?courseId=${course.id}" 
                               class="btn btn-sm btn-primary">
                                <i class="bi bi-plus-circle"></i> Add Session
                            </a>
                        </c:if>
                    </div>
                    <div class="card-body">
                        <!-- Session list will be implemented in a future update -->
                        <p class="text-muted">No sessions have been scheduled for this course yet.</p>
                    </div>
                </div>
                
                <!-- Materials Section Placeholder -->
                <div class="card mb-4">
                    <div class="card-header bg-light d-flex justify-content-between align-items-center">
                        <h5 class="card-title mb-0">Materials</h5>
                        
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN' || sessionScope.currentUser.role == 'TRAINER'}">
                            <a href="${pageContext.request.contextPath}/material/form?courseId=${course.id}" 
                               class="btn btn-sm btn-primary">
                                <i class="bi bi-plus-circle"></i> Add Material
                            </a>
                        </c:if>
                    </div>
                    <div class="card-body">
                        <!-- Materials list will be implemented in a future update -->
                        <p class="text-muted">No materials have been added to this course yet.</p>
                    </div>
                </div>
            </div>
            
            <div class="col-md-4">
                <!-- Program Information -->
                <div class="card mb-4">
                    <div class="card-header bg-light">
                        <h5 class="card-title mb-0">Program</h5>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty program}">
                            <h5>${program.title}</h5>
                            <p class="text-muted">
                                <span class="badge status-pill status-${program.status.toLowerCase()}">${program.status}</span>
                            </p>
                            <a href="${pageContext.request.contextPath}/program/view?id=${program.id}" 
                               class="btn btn-outline-primary btn-sm">
                                View Program Details
                            </a>
                        </c:if>
                        <c:if test="${empty program}">
                            <p class="text-muted">Program information not available.</p>
                        </c:if>
                    </div>
                </div>
                
                <!-- Assigned Trainer -->
                <div class="card mb-4">
                    <div class="card-header bg-light d-flex justify-content-between align-items-center">
                        <h5 class="card-title mb-0">Assigned Trainer</h5>
                        
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                            <a href="${pageContext.request.contextPath}/course/assign-trainer?id=${course.id}" 
                               class="btn btn-sm btn-primary">
                                <i class="bi bi-person-plus"></i> Assign
                            </a>
                        </c:if>
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
                                <p class="text-muted">No trainer assigned to this course yet.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <!-- Metadata -->
                <div class="card">
                    <div class="card-header bg-light">
                        <h5 class="card-title mb-0">Metadata</h5>
                    </div>
                    <div class="card-body">
                        <dl class="row mb-0">
                            <dt class="col-sm-5">Created</dt>
                            <dd class="col-sm-7">
                                <c:if test="${not empty course.createdAt}">
                                    <fmt:formatDate value="${course.createdAt}" pattern="dd MMM yyyy, HH:mm" />
                                </c:if>
                            </dd>
                            
                            <dt class="col-sm-5">Last Updated</dt>
                            <dd class="col-sm-7">
                                <c:if test="${not empty course.updatedAt}">
                                    <fmt:formatDate value="${course.updatedAt}" pattern="dd MMM yyyy, HH:mm" />
                                </c:if>
                            </dd>
                            
                            <dt class="col-sm-5">Course ID</dt>
                            <dd class="col-sm-7">${course.id}</dd>
                            
                            <dt class="col-sm-5">Sequence</dt>
                            <dd class="col-sm-7">${course.sequence}</dd>
                        </dl>
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
                    Are you sure you want to delete the course <strong id="courseTitle"></strong>?
                    This will also delete all associated sessions, materials, and assessments.
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