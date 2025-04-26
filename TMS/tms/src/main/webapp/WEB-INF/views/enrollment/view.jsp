<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enrollment Details - Training Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/shared/header.jsp" />
    
    <main class="container my-4">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${successMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>
                
                <div class="card shadow-sm mb-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h4 class="mb-0">Enrollment Details</h4>
                        <span class="status-pill status-${enrollment.status.toLowerCase()}">${enrollment.status}</span>
                    </div>
                    <div class="card-body">
                        <div class="row mb-4">
                            <div class="col-md-6">
                                <h5 class="text-muted mb-3">Enrollment Information</h5>
                                <dl class="row">
                                    <dt class="col-sm-5">Enrollment ID:</dt>
                                    <dd class="col-sm-7">${enrollment.id}</dd>
                                    
                                    <dt class="col-sm-5">Status:</dt>
                                    <dd class="col-sm-7">${enrollment.status}</dd>
                                    
                                    <dt class="col-sm-5">Enrolled Date:</dt>
                                    <dd class="col-sm-7">
                                        <fmt:formatDate value="${enrollment.enrolledAt}" pattern="MMMM dd, yyyy" />
                                    </dd>
                                    
                                    <c:if test="${not empty enrollment.completedAt}">
                                        <dt class="col-sm-5">Completed Date:</dt>
                                        <dd class="col-sm-7">
                                            <fmt:formatDate value="${enrollment.completedAt}" pattern="MMMM dd, yyyy" />
                                        </dd>
                                    </c:if>
                                </dl>
                            </div>
                            <div class="col-md-6">
                                <h5 class="text-muted mb-3">Trainee Information</h5>
                                <c:if test="${not empty enrollment.trainee}">
                                    <dl class="row">
                                        <dt class="col-sm-5">Name:</dt>
                                        <dd class="col-sm-7">${enrollment.trainee.fullName}</dd>
                                        
                                        <dt class="col-sm-5">Email:</dt>
                                        <dd class="col-sm-7">${enrollment.trainee.email}</dd>
                                        
                                        <dt class="col-sm-5">Phone:</dt>
                                        <dd class="col-sm-7">${enrollment.trainee.phone}</dd>
                                    </dl>
                                </c:if>
                            </div>
                        </div>
                        
                        <h5 class="text-muted mb-3">Training Program Details</h5>
                        <c:if test="${not empty enrollment.trainingProgram}">
                            <div class="card mb-4">
                                <div class="card-body">
                                    <h5 class="card-title">${enrollment.trainingProgram.title}</h5>
                                    <p class="card-text">${enrollment.trainingProgram.description}</p>
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <small class="text-muted d-block">Start Date</small>
                                            <span><fmt:formatDate value="${enrollment.trainingProgram.startDate}" pattern="MMMM dd, yyyy" /></span>
                                        </div>
                                        <div class="col-md-6">
                                            <small class="text-muted d-block">End Date</small>
                                            <span><fmt:formatDate value="${enrollment.trainingProgram.endDate}" pattern="MMMM dd, yyyy" /></span>
                                        </div>
                                        <div class="col-md-6">
                                            <small class="text-muted d-block">Course Count</small>
                                            <span>${enrollment.trainingProgram.courseCount} courses</span>
                                        </div>
                                        <div class="col-md-6">
                                            <small class="text-muted d-block">Status</small>
                                            <span class="status-pill status-${enrollment.trainingProgram.status.toLowerCase()}">${enrollment.trainingProgram.status}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty enrollment.notes}">
                            <h5 class="text-muted mb-3">Notes</h5>
                            <div class="p-3 bg-light rounded mb-4">
                                ${enrollment.notes}
                            </div>
                        </c:if>
                        
                        <!-- Administrator Actions -->
                        <c:if test="${isAdmin || isTrainer}">
                            <h5 class="text-muted mb-3">Administration</h5>
                            <div class="card mb-3">
                                <div class="card-body">
                                    <h6 class="card-subtitle mb-3">Update Enrollment Status</h6>
                                    <c:if test="${enrollment.status == 'ACTIVE'}">
                                        <div class="d-flex gap-2">
                                            <form action="${pageContext.request.contextPath}/enrollment/status/${enrollment.id}" method="post" class="d-inline-block">
                                                <input type="hidden" name="status" value="COMPLETED">
                                                <button type="submit" class="btn btn-outline-success">Mark as Completed</button>
                                            </form>
                                            
                                            <form action="${pageContext.request.contextPath}/enrollment/status/${enrollment.id}" method="post" class="d-inline-block">
                                                <input type="hidden" name="status" value="DROPPED">
                                                <button type="submit" class="btn btn-outline-warning">Mark as Dropped</button>
                                            </form>
                                        </div>
                                    </c:if>
                                    <c:if test="${enrollment.status != 'ACTIVE'}">
                                        <form action="${pageContext.request.contextPath}/enrollment/status/${enrollment.id}" method="post" class="d-inline-block">
                                            <input type="hidden" name="status" value="ACTIVE">
                                            <button type="submit" class="btn btn-outline-primary">Reactivate Enrollment</button>
                                        </form>
                                    </c:if>
                                    
                                    <c:if test="${isAdmin}">
                                        <hr class="my-3">
                                        <h6 class="card-subtitle mb-3 text-danger">Danger Zone</h6>
                                        <a href="#" 
                                           class="btn btn-outline-danger btn-delete" 
                                           onclick="if(confirm('Are you sure you want to delete this enrollment? This action cannot be undone.')) { window.location.href='${pageContext.request.contextPath}/enrollment/delete/${enrollment.id}'; } return false;">
                                            <i class="bi bi-trash"></i> Delete Enrollment
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>
                    </div>
                    <div class="card-footer">
                        <div class="d-flex justify-content-between">
                            <c:choose>
                                <c:when test="${isAdmin}">
                                    <a href="${pageContext.request.contextPath}/enrollment/" class="btn btn-outline-secondary">
                                        <i class="bi bi-arrow-left"></i> Back to All Enrollments
                                    </a>
                                </c:when>
                                <c:when test="${isTrainer && not empty enrollment.trainingProgram}">
                                    <a href="${pageContext.request.contextPath}/enrollment/program/${enrollment.trainingProgramId}" class="btn btn-outline-secondary">
                                        <i class="bi bi-arrow-left"></i> Back to Program Enrollments
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/enrollment/my" class="btn btn-outline-secondary">
                                        <i class="bi bi-arrow-left"></i> Back to My Enrollments
                                    </a>
                                </c:otherwise>
                            </c:choose>
                            
                            <c:if test="${not empty enrollment.trainingProgram}">
                                <a href="${pageContext.request.contextPath}/program/view/${enrollment.trainingProgramId}" class="btn btn-primary">
                                    <i class="bi bi-book"></i> View Program Details
                                </a>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html> 