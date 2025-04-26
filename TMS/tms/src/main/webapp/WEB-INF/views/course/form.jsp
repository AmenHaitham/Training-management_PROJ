<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>${editing ? 'Edit' : 'Create'} Course</title>
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
                    <a href="${pageContext.request.contextPath}/courses${not empty program ? '?programId='.concat(program.id) : ''}">
                        Courses
                    </a>
                </li>
                <li class="breadcrumb-item active">${editing ? 'Edit' : 'Create'} Course</li>
            </ol>
        </nav>
        
        <!-- Page Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1>${editing ? 'Edit' : 'Create'} Course</h1>
        </div>
        
        <!-- Error messages -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <!-- Program Selection Form (if no program selected) -->
        <c:if test="${selectProgram}">
            <div class="card mb-4">
                <div class="card-header bg-light">
                    <h5 class="mb-0">Select Training Program</h5>
                </div>
                <div class="card-body">
                    <p>Please select a training program for this course:</p>
                    
                    <div class="row row-cols-1 row-cols-md-3 g-4">
                        <c:forEach var="program" items="${programs}">
                            <div class="col">
                                <div class="card h-100">
                                    <div class="card-body">
                                        <h5 class="card-title">${program.title}</h5>
                                        <p class="card-text">
                                            <span class="badge status-pill status-${program.status.toLowerCase()}">${program.status}</span>
                                        </p>
                                        <a href="${pageContext.request.contextPath}/course/form?programId=${program.id}" 
                                           class="btn btn-outline-primary">
                                            Select Program
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>
        
        <!-- Course Form -->
        <c:if test="${not selectProgram}">
            <div class="card">
                <div class="card-header bg-light">
                    <h5 class="mb-0">${editing ? 'Edit' : 'Create'} Course</h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/course/${editing ? 'update' : 'create'}" method="post" class="form-container needs-validation" novalidate>
                        <c:if test="${editing}">
                            <input type="hidden" name="id" value="${course.id}">
                        </c:if>
                        
                        <input type="hidden" name="trainingProgramId" value="${course.trainingProgramId}">
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="title" class="form-label">Title <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="title" name="title" value="${course.title}" required>
                                <div class="invalid-feedback">
                                    Please provide a course title.
                                </div>
                            </div>
                            <div class="col-md-6">
                                <label for="category" class="form-label">Category</label>
                                <input type="text" class="form-control" id="category" name="category" value="${course.category}">
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="description" class="form-label">Description <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="description" name="description" rows="4" required>${course.description}</textarea>
                            <div class="invalid-feedback">
                                Please provide a course description.
                            </div>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-4">
                                <label for="duration" class="form-label">Duration (hours) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="duration" name="duration" value="${course.duration}" min="1" required>
                                <div class="invalid-feedback">
                                    Please provide a duration in hours.
                                </div>
                            </div>
                            <div class="col-md-4">
                                <label for="price" class="form-label">Price ($)</label>
                                <input type="number" class="form-control" id="price" name="price" value="${course.price}" step="0.01" min="0">
                            </div>
                            <div class="col-md-4">
                                <label for="sequence" class="form-label">Sequence <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="sequence" name="sequence" value="${course.sequence}" min="1" required>
                                <div class="invalid-feedback">
                                    Please provide a sequence number.
                                </div>
                                <small class="form-text text-muted">Order within the program</small>
                            </div>
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="status" class="form-label">Status <span class="text-danger">*</span></label>
                                <select class="form-select" id="status" name="status" required>
                                    <option value="">Select status</option>
                                    <option value="DRAFT" ${course.status == 'DRAFT' ? 'selected' : ''}>Draft</option>
                                    <option value="ACTIVE" ${course.status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                                    <option value="ARCHIVED" ${course.status == 'ARCHIVED' ? 'selected' : ''}>Archived</option>
                                </select>
                                <div class="invalid-feedback">
                                    Please select a status.
                                </div>
                            </div>
                            <div class="col-md-6">
                                <label for="assignedTrainerId" class="form-label">Assigned Trainer</label>
                                <select class="form-select" id="assignedTrainerId" name="assignedTrainerId">
                                    <option value="">No trainer assigned</option>
                                    <c:forEach var="trainer" items="${trainers}">
                                        <option value="${trainer.id}" ${course.assignedTrainerId == trainer.id ? 'selected' : ''}>
                                            ${trainer.firstName} ${trainer.lastName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        
                        <div class="d-grid gap-2 d-md-flex justify-content-md-end mt-4">
                            <a href="${pageContext.request.contextPath}/courses${not empty program ? '?programId='.concat(program.id) : ''}" 
                               class="btn btn-secondary me-md-2">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                ${editing ? 'Update' : 'Create'} Course
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>
    </div>
    
    <jsp:include page="../../includes/footer.jsp" />
</body>
</html> 