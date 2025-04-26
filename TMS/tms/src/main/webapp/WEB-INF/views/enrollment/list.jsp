<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>All Enrollments - Training Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/shared/header.jsp" />
    
    <main class="container my-4">
        <div class="row mb-4">
            <div class="col-md-6">
                <h1>Enrollment Management</h1>
            </div>
            <div class="col-md-6 text-end">
                <form action="${pageContext.request.contextPath}/enrollment/" method="get" class="d-inline-block" id="filterForm">
                    <div class="input-group">
                        <select name="status" class="form-select form-select-sm" aria-label="Status filter">
                            <option value="">All Statuses</option>
                            <option value="ACTIVE" ${statusFilter == 'ACTIVE' ? 'selected' : ''}>Active</option>
                            <option value="COMPLETED" ${statusFilter == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                            <option value="DROPPED" ${statusFilter == 'DROPPED' ? 'selected' : ''}>Dropped</option>
                        </select>
                        <button class="btn btn-sm btn-outline-secondary" type="submit">Filter</button>
                    </div>
                </form>
            </div>
        </div>
        
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="successMessage" scope="session" />
        </c:if>
        
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        
        <div class="card shadow-sm">
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty enrollments}">
                        <div class="alert alert-info">No enrollments found.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-striped table-hover align-middle">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Trainee</th>
                                        <th>Training Program</th>
                                        <th>Status</th>
                                        <th>Enrolled</th>
                                        <th>Completed</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${enrollments}" var="enrollment">
                                        <tr>
                                            <td>${enrollment.id}</td>
                                            <td>
                                                <c:if test="${not empty enrollment.trainee}">
                                                    ${enrollment.trainee.fullName}
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:if test="${not empty enrollment.trainingProgram}">
                                                    <a href="${pageContext.request.contextPath}/program/view/${enrollment.trainingProgramId}">
                                                        ${enrollment.trainingProgram.title}
                                                    </a>
                                                </c:if>
                                            </td>
                                            <td>
                                                <span class="status-pill status-${enrollment.status.toLowerCase()}">${enrollment.status}</span>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${enrollment.enrolledAt}" pattern="MMM dd, yyyy" />
                                            </td>
                                            <td>
                                                <c:if test="${not empty enrollment.completedAt}">
                                                    <fmt:formatDate value="${enrollment.completedAt}" pattern="MMM dd, yyyy" />
                                                </c:if>
                                                <c:if test="${empty enrollment.completedAt && enrollment.status == 'ACTIVE'}">
                                                    <span class="text-muted">In Progress</span>
                                                </c:if>
                                            </td>
                                            <td>
                                                <div class="btn-group btn-group-sm">
                                                    <a href="${pageContext.request.contextPath}/enrollment/view/${enrollment.id}" 
                                                       class="btn btn-outline-primary" 
                                                       data-bs-toggle="tooltip" title="View">
                                                        <i class="bi bi-eye"></i> View
                                                    </a>
                                                    
                                                    <c:if test="${enrollment.status == 'ACTIVE'}">
                                                        <button type="button" 
                                                                class="btn btn-outline-success dropdown-toggle"
                                                                data-bs-toggle="dropdown" 
                                                                aria-expanded="false">
                                                            Status
                                                        </button>
                                                        <ul class="dropdown-menu">
                                                            <li>
                                                                <form action="${pageContext.request.contextPath}/enrollment/status/${enrollment.id}" method="post">
                                                                    <input type="hidden" name="status" value="COMPLETED">
                                                                    <button type="submit" class="dropdown-item">Complete</button>
                                                                </form>
                                                            </li>
                                                            <li>
                                                                <form action="${pageContext.request.contextPath}/enrollment/status/${enrollment.id}" method="post">
                                                                    <input type="hidden" name="status" value="DROPPED">
                                                                    <button type="submit" class="dropdown-item">Drop</button>
                                                                </form>
                                                            </li>
                                                        </ul>
                                                    </c:if>
                                                    
                                                    <a href="#" 
                                                       class="btn btn-outline-danger btn-delete" 
                                                       data-bs-toggle="tooltip" 
                                                       title="Delete"
                                                       onclick="if(confirm('Are you sure you want to delete this enrollment?')) { window.location.href='${pageContext.request.contextPath}/enrollment/delete/${enrollment.id}'; } return false;">
                                                        <i class="bi bi-trash"></i> Delete
                                                    </a>
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
    </main>
    
    <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html> 