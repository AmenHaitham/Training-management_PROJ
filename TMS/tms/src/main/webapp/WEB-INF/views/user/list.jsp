<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>User Management - Training Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container mt-4 mb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="mb-0">User Management</h1>
            <c:if test="${authUtil.hasRole('ADMIN')}">
                <a href="${pageContext.request.contextPath}/users/create" class="btn btn-danger">
                    <i class="bi bi-plus-circle me-1"></i> Add User
                </a>
            </c:if>
        </div>
        
        <!-- Filter Form -->
        <div class="card mb-4 shadow-sm">
            <div class="card-header bg-dark text-white">
                <h5 class="mb-0">Filter Users</h5>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/users" method="get" id="filterForm" class="row g-3">
                    <div class="col-md-4">
                        <label for="name" class="form-label">Name</label>
                        <input type="text" class="form-control" id="name" name="name" value="${param.name}" placeholder="Search by name">
                    </div>
                    <div class="col-md-4">
                        <label for="email" class="form-label">Email</label>
                        <input type="text" class="form-control" id="email" name="email" value="${param.email}" placeholder="Search by email">
                    </div>
                    <div class="col-md-4">
                        <label for="role" class="form-label">Role</label>
                        <select class="form-select" id="role" name="role">
                            <option value="" ${empty param.role ? 'selected' : ''}>All Roles</option>
                            <option value="ADMIN" ${param.role == 'ADMIN' ? 'selected' : ''}>Admin</option>
                            <option value="TRAINER" ${param.role == 'TRAINER' ? 'selected' : ''}>Trainer</option>
                            <option value="TRAINEE" ${param.role == 'TRAINEE' ? 'selected' : ''}>Trainee</option>
                        </select>
                    </div>
                    <div class="col-12 text-end">
                        <button type="submit" class="btn btn-dark">
                            <i class="bi bi-search me-1"></i> Search
                        </button>
                        <a href="${pageContext.request.contextPath}/users" class="btn btn-outline-secondary">
                            <i class="bi bi-x-circle me-1"></i> Clear Filters
                        </a>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Users Table -->
        <div class="card shadow-sm">
            <div class="card-header bg-dark text-white">
                <h5 class="mb-0">Users</h5>
            </div>
            <div class="card-body table-responsive">
                <c:choose>
                    <c:when test="${empty users}">
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle me-2"></i> No users found matching your criteria.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="table table-striped table-hover align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Role</th>
                                    <th>Created</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="user" items="${users}">
                                    <tr>
                                        <td>${user.id}</td>
                                        <td>
                                            <div class="d-flex align-items-center">
                                                <div class="avatar-placeholder bg-secondary text-light me-2">
                                                    ${user.firstname.charAt(0)}${user.lastname.charAt(0)}
                                                </div>
                                                <div>
                                                    <strong>${user.firstname} ${user.lastname}</strong>
                                                </div>
                                            </div>
                                        </td>
                                        <td>${user.email}</td>
                                        <td>
                                            <span class="badge 
                                                <c:choose>
                                                    <c:when test="${user.role == 'ADMIN'}">bg-danger</c:when>
                                                    <c:when test="${user.role == 'TRAINER'}">bg-success</c:when>
                                                    <c:otherwise>bg-primary</c:otherwise>
                                                </c:choose>">
                                                ${user.role}
                                            </span>
                                        </td>
                                        <td><fmt:formatDate value="${user.createdAt}" pattern="MMM dd, yyyy" /></td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <a href="${pageContext.request.contextPath}/users/view?id=${user.id}" class="btn btn-sm btn-outline-secondary" data-bs-toggle="tooltip" title="View details">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                                <c:if test="${authUtil.hasRole('ADMIN')}">
                                                    <a href="${pageContext.request.contextPath}/users/edit?id=${user.id}" class="btn btn-sm btn-outline-primary" data-bs-toggle="tooltip" title="Edit user">
                                                        <i class="bi bi-pencil"></i>
                                                    </a>
                                                    <button type="button" class="btn btn-sm btn-outline-danger btn-delete" data-id="${user.id}" data-name="${user.firstname} ${user.lastname}" data-bs-toggle="tooltip" title="Delete user">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        
                        <!-- Pagination -->
                        <c:if test="${totalPages > 1}">
                            <nav aria-label="Page navigation" class="mt-4">
                                <ul class="pagination justify-content-center">
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/users?page=${currentPage - 1}&name=${param.name}&email=${param.email}&role=${param.role}">Previous</a>
                                    </li>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/users?page=${i}&name=${param.name}&email=${param.email}&role=${param.role}">${i}</a>
                                        </li>
                                    </c:forEach>
                                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/users?page=${currentPage + 1}&name=${param.name}&email=${param.email}&role=${param.role}">Next</a>
                                    </li>
                                </ul>
                            </nav>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    
    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title" id="deleteModalLabel">Confirm Delete</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to delete the user <strong id="userName"></strong>?</p>
                    <p class="text-danger"><i class="bi bi-exclamation-triangle me-2"></i>This action cannot be undone.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form action="${pageContext.request.contextPath}/users/delete" method="post" id="deleteForm">
                        <input type="hidden" name="id" id="deleteUserId">
                        <button type="submit" class="btn btn-danger">Delete</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Delete modal setup
            const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            const deleteButtons = document.querySelectorAll('.btn-delete');
            
            deleteButtons.forEach(button => {
                button.addEventListener('click', function() {
                    const userId = this.getAttribute('data-id');
                    const userName = this.getAttribute('data-name');
                    
                    document.getElementById('deleteUserId').value = userId;
                    document.getElementById('userName').textContent = userName;
                    deleteModal.show();
                });
            });
            
            // Auto-submit filter form on select change
            document.getElementById('role').addEventListener('change', function() {
                document.getElementById('filterForm').submit();
            });
        });
    </script>
</body>
</html> 