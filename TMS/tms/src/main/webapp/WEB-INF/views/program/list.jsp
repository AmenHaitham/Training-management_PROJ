<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Training Programs - Training Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
    <jsp:include page="/WEB-INF/includes/header.jsp" />
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1>Training Programs</h1>
            
            <div>
                <c:if test="${not empty sessionScope.currentUser && (sessionScope.currentUser.role eq 'ADMIN' || sessionScope.currentUser.role eq 'TRAINER')}">
                    <a href="<c:url value='/${sessionScope.currentUser.role.toLowerCase()}/training-programs/form'/>" class="btn btn-primary">
                        <i class="bi bi-plus-circle"></i> Create New Program
                    </a>
                </c:if>
            </div>
        </div>
        
        <!-- Filter options -->
        <div class="card mb-4">
            <div class="card-body">
                <form action="<c:url value='/training-programs'/>" method="get" class="row g-3">
                    <div class="col-md-4">
                        <label for="status" class="form-label">Status</label>
                        <select class="form-select" id="status" name="status">
                            <option value="">All</option>
                            <option value="DRAFT" ${param.status eq 'DRAFT' ? 'selected' : ''}>Draft</option>
                            <option value="ACTIVE" ${param.status eq 'ACTIVE' ? 'selected' : ''}>Active</option>
                            <option value="COMPLETED" ${param.status eq 'COMPLETED' ? 'selected' : ''}>Completed</option>
                            <option value="CANCELLED" ${param.status eq 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label for="title" class="form-label">Title</label>
                        <input type="text" class="form-control" id="title" name="title" value="${param.title}">
                    </div>
                    <div class="col-md-4 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary w-100">Filter</button>
                    </div>
                </form>
            </div>
        </div>
        
        <c:choose>
            <c:when test="${empty programs}">
                <div class="alert alert-info">
                    No training programs found.
                </div>
            </c:when>
            <c:otherwise>
                <div class="row">
                    <c:forEach var="program" items="${programs}">
                        <div class="col-md-4 mb-4">
                            <div class="card h-100">
                                <div class="card-header bg-${program.status eq 'ACTIVE' ? 'success' : program.status eq 'DRAFT' ? 'warning' : program.status eq 'COMPLETED' ? 'info' : 'secondary'} text-white">
                                    <h5 class="card-title mb-0">${program.title}</h5>
                                </div>
                                <div class="card-body">
                                    <p class="card-text">${program.description}</p>
                                    <p><strong>Status:</strong> ${program.status}</p>
                                    <p>
                                        <strong>Start Date:</strong> 
                                        <c:if test="${not empty program.startDate}">
                                            <fmt:formatDate value="${program.startDate}" pattern="MMM dd, yyyy" />
                                        </c:if>
                                        <c:if test="${empty program.startDate}">Not set</c:if>
                                    </p>
                                    <p>
                                        <strong>End Date:</strong> 
                                        <c:if test="${not empty program.endDate}">
                                            <fmt:formatDate value="${program.endDate}" pattern="MMM dd, yyyy" />
                                        </c:if>
                                        <c:if test="${empty program.endDate}">Not set</c:if>
                                    </p>
                                    <p>
                                        <strong>Created By:</strong> 
                                        <c:if test="${not empty program.creator}">
                                            ${program.creator.firstName} ${program.creator.lastName}
                                        </c:if>
                                        <c:if test="${empty program.creator}">Unknown</c:if>
                                    </p>
                                </div>
                                <div class="card-footer">
                                    <div class="d-flex justify-content-between">
                                        <a href="<c:url value='/training-programs/details?id=${program.id}'/>" class="btn btn-primary btn-sm">View Details</a>
                                        
                                        <c:if test="${not empty sessionScope.currentUser && sessionScope.currentUser.role eq 'TRAINEE' && program.status eq 'ACTIVE'}">
                                            <a href="<c:url value='/trainee/enroll?programId=${program.id}'/>" class="btn btn-success btn-sm">Enroll</a>
                                        </c:if>
                                        
                                        <c:if test="${not empty sessionScope.currentUser && (sessionScope.currentUser.role eq 'ADMIN' || (sessionScope.currentUser.role eq 'TRAINER' && sessionScope.currentUser.id eq program.createdBy))}">
                                            <a href="<c:url value='/${sessionScope.currentUser.role.toLowerCase()}/training-programs/form?id=${program.id}'/>" class="btn btn-secondary btn-sm">Edit</a>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    
    <jsp:include page="/WEB-INF/includes/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<c:url value='/assets/js/script.js'/>"></script>
</body>
</html> 