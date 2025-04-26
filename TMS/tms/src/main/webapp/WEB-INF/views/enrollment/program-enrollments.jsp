<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${program.title} - Enrollments - Training Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/shared/header.jsp" />
    
    <main class="container my-4">
        <div class="row mb-4">
            <div class="col-md-8">
                <h2>${program.title} - Enrollments</h2>
                <p class="text-muted">
                    <span class="status-pill status-${program.status.toLowerCase()}">${program.status}</span>
                    <span class="ms-2">
                        <i class="bi bi-calendar"></i> 
                        <fmt:formatDate value="${program.startDate}" pattern="MMM dd, yyyy" /> - 
                        <fmt:formatDate value="${program.endDate}" pattern="MMM dd, yyyy" />
                    </span>
                </p>
            </div>
            <div class="col-md-4 text-end">
                <a href="${pageContext.request.contextPath}/program/view/${program.id}" class="btn btn-outline-primary">
                    <i class="bi bi-arrow-left"></i> Back to Program
                </a>
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
            <div class="card-header">
                <ul class="nav nav-tabs card-header-tabs">
                    <li class="nav-item">
                        <a class="nav-link active" href="#">Enrollments (${enrollments.size()})</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/program/courses/${program.id}">Courses</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/program/materials/${program.id}">Materials</a>
                    </li>
                </ul>
            </div>
            <div class="card-body">
                <div class="row mb-3">
                    <div class="col-md-6">
                        <div class="input-group">
                            <input type="text" id="enrollmentSearch" class="form-control" placeholder="Search enrollments...">
                            <button class="btn btn-outline-secondary" type="button">
                                <i class="bi bi-search"></i>
                            </button>
                        </div>
                    </div>
                    <div class="col-md-6 text-end">
                        <div class="btn-group" role="group">
                            <button type="button" class="btn btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-funnel"></i> Filter
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item filter-link" href="#" data-filter="all">All Enrollments</a></li>
                                <li><a class="dropdown-item filter-link" href="#" data-filter="ACTIVE">Active</a></li>
                                <li><a class="dropdown-item filter-link" href="#" data-filter="COMPLETED">Completed</a></li>
                                <li><a class="dropdown-item filter-link" href="#" data-filter="DROPPED">Dropped</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
                
                <c:choose>
                    <c:when test="${empty enrollments}">
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle"></i> No enrollments found for this training program.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover" id="enrollmentsTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Trainee</th>
                                        <th>Status</th>
                                        <th>Enrolled Date</th>
                                        <th>Completed Date</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${enrollments}" var="enrollment">
                                        <tr class="enrollment-row" data-status="${enrollment.status}">
                                            <td>${enrollment.id}</td>
                                            <td>
                                                <c:if test="${not empty enrollment.trainee}">
                                                    ${enrollment.trainee.fullName}<br>
                                                    <small class="text-muted">${enrollment.trainee.email}</small>
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
                                                       data-bs-toggle="tooltip"
                                                       title="View Details">
                                                        <i class="bi bi-eye"></i>
                                                    </a>
                                                    
                                                    <c:if test="${enrollment.status == 'ACTIVE'}">
                                                        <button type="button" 
                                                                class="btn btn-outline-success dropdown-toggle"
                                                                data-bs-toggle="dropdown">
                                                            <i class="bi bi-gear"></i>
                                                        </button>
                                                        <ul class="dropdown-menu dropdown-menu-end">
                                                            <li>
                                                                <form action="${pageContext.request.contextPath}/enrollment/status/${enrollment.id}" method="post">
                                                                    <input type="hidden" name="status" value="COMPLETED">
                                                                    <button type="submit" class="dropdown-item">Mark as Completed</button>
                                                                </form>
                                                            </li>
                                                            <li>
                                                                <form action="${pageContext.request.contextPath}/enrollment/status/${enrollment.id}" method="post">
                                                                    <input type="hidden" name="status" value="DROPPED">
                                                                    <button type="submit" class="dropdown-item">Mark as Dropped</button>
                                                                </form>
                                                            </li>
                                                        </ul>
                                                    </c:if>
                                                    
                                                    <c:if test="${enrollment.status != 'ACTIVE' && (isAdmin || isTrainer)}">
                                                        <form action="${pageContext.request.contextPath}/enrollment/status/${enrollment.id}" method="post" class="d-inline">
                                                            <input type="hidden" name="status" value="ACTIVE">
                                                            <button type="submit" 
                                                                   class="btn btn-outline-warning"
                                                                   data-bs-toggle="tooltip"
                                                                   title="Reactivate">
                                                                <i class="bi bi-arrow-repeat"></i>
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                    
                                                    <c:if test="${isAdmin}">
                                                        <a href="#" 
                                                           class="btn btn-outline-danger btn-delete"
                                                           data-bs-toggle="tooltip"
                                                           title="Delete"
                                                           onclick="if(confirm('Are you sure you want to delete this enrollment?')) { window.location.href='${pageContext.request.contextPath}/enrollment/delete/${enrollment.id}'; } return false;">
                                                            <i class="bi bi-trash"></i>
                                                        </a>
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
    </main>
    
    <jsp:include page="/WEB-INF/views/shared/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
    <script>
        // Client-side search and filtering
        document.addEventListener('DOMContentLoaded', function() {
            // Search functionality
            const searchInput = document.getElementById('enrollmentSearch');
            if (searchInput) {
                searchInput.addEventListener('keyup', function() {
                    const searchValue = this.value.toLowerCase();
                    const rows = document.querySelectorAll('#enrollmentsTable tbody tr');
                    
                    rows.forEach(row => {
                        const text = row.textContent.toLowerCase();
                        if (text.includes(searchValue)) {
                            row.style.display = '';
                        } else {
                            row.style.display = 'none';
                        }
                    });
                });
            }
            
            // Filter functionality
            const filterLinks = document.querySelectorAll('.filter-link');
            filterLinks.forEach(link => {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    const filter = this.dataset.filter;
                    const rows = document.querySelectorAll('.enrollment-row');
                    
                    rows.forEach(row => {
                        if (filter === 'all' || row.dataset.status === filter) {
                            row.style.display = '';
                        } else {
                            row.style.display = 'none';
                        }
                    });
                });
            });
        });
    </script>
</body>
</html> 