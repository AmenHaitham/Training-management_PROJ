<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>View User | Training Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="/WEB-INF/views/common/header.jsp" />
    
    <main class="container my-4">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h2>User Details</h2>
                    </div>
                    <div class="card-body">
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <h5>Basic Information</h5>
                                <dl class="row">
                                    <dt class="col-sm-4">ID:</dt>
                                    <dd class="col-sm-8">${user.id}</dd>
                                    
                                    <dt class="col-sm-4">Username:</dt>
                                    <dd class="col-sm-8">${user.username}</dd>
                                    
                                    <dt class="col-sm-4">Full Name:</dt>
                                    <dd class="col-sm-8">${user.firstName} ${user.lastName}</dd>
                                    
                                    <dt class="col-sm-4">Role:</dt>
                                    <dd class="col-sm-8">
                                        <span class="badge bg-${user.role == 'ADMIN' ? 'danger' : (user.role == 'TRAINER' ? 'primary' : 'success')}">
                                            ${user.role}
                                        </span>
                                    </dd>
                                </dl>
                            </div>
                            
                            <div class="col-md-6">
                                <h5>Contact Information</h5>
                                <dl class="row">
                                    <dt class="col-sm-4">Email:</dt>
                                    <dd class="col-sm-8">
                                        <a href="mailto:${user.email}">${user.email}</a>
                                    </dd>
                                    
                                    <dt class="col-sm-4">Phone:</dt>
                                    <dd class="col-sm-8">
                                        <c:choose>
                                            <c:when test="${not empty user.phone}">
                                                <a href="tel:${user.phone}">${user.phone}</a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Not provided</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </dd>
                                    
                                    <dt class="col-sm-4">Birth Date:</dt>
                                    <dd class="col-sm-8">
                                        <c:choose>
                                            <c:when test="${not empty user.birthDate}">
                                                <fmt:formatDate value="${user.birthDate}" pattern="dd MMMM yyyy" />
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Not provided</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </dd>
                                </dl>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-12">
                                <h5>System Information</h5>
                                <dl class="row">
                                    <dt class="col-sm-2">Created:</dt>
                                    <dd class="col-sm-4">
                                        <fmt:formatDate value="${user.createdAt}" pattern="dd MMM yyyy HH:mm" />
                                    </dd>
                                    
                                    <dt class="col-sm-2">Updated:</dt>
                                    <dd class="col-sm-4">
                                        <fmt:formatDate value="${user.updatedAt}" pattern="dd MMM yyyy HH:mm" />
                                    </dd>
                                </dl>
                            </div>
                        </div>
                        
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                            <div class="mt-4 border-top pt-3">
                                <h5>Related Information</h5>
                                <ul class="nav nav-tabs" id="userTabs" role="tablist">
                                    <c:if test="${user.role == 'TRAINEE'}">
                                        <li class="nav-item" role="presentation">
                                            <button class="nav-link active" id="enrollments-tab" data-bs-toggle="tab" 
                                                    data-bs-target="#enrollments" type="button" role="tab">
                                                Enrollments
                                            </button>
                                        </li>
                                    </c:if>
                                    
                                    <c:if test="${user.role == 'TRAINER'}">
                                        <li class="nav-item" role="presentation">
                                            <button class="nav-link ${user.role == 'TRAINEE' ? '' : 'active'}" id="programs-tab" 
                                                    data-bs-toggle="tab" data-bs-target="#programs" type="button" role="tab">
                                                Training Programs
                                            </button>
                                        </li>
                                    </c:if>
                                </ul>
                                
                                <div class="tab-content p-3 border border-top-0 rounded-bottom" id="userTabsContent">
                                    <c:if test="${user.role == 'TRAINEE'}">
                                        <div class="tab-pane fade show active" id="enrollments" role="tabpanel">
                                            <p class="text-muted">This section will display the trainee's enrollments.</p>
                                        </div>
                                    </c:if>
                                    
                                    <c:if test="${user.role == 'TRAINER'}">
                                        <div class="tab-pane fade ${user.role == 'TRAINEE' ? '' : 'show active'}" id="programs" role="tabpanel">
                                            <p class="text-muted">This section will display the training programs assigned to this trainer.</p>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>
                    </div>
                    
                    <div class="card-footer">
                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary">
                                <i class="bi bi-arrow-left"></i> Back to List
                            </a>
                            
                            <div>
                                <a href="${pageContext.request.contextPath}/user/edit/${user.id}" class="btn btn-primary me-2">
                                    <i class="bi bi-pencil"></i> Edit
                                </a>
                                
                                <c:if test="${sessionScope.currentUser.role == 'ADMIN' && sessionScope.currentUser.id != user.id}">
                                    <button class="btn btn-danger btn-delete" data-bs-toggle="modal" data-bs-target="#deleteModal">
                                        <i class="bi bi-trash"></i> Delete
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
    
    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">Confirm Delete</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to delete the user <strong>${user.firstName} ${user.lastName}</strong>?
                    This action cannot be undone.
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <a href="${pageContext.request.contextPath}/user/delete/${user.id}" class="btn btn-danger">Delete</a>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html> 