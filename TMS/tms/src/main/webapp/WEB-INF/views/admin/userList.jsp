<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - Training Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
    <jsp:include page="/WEB-INF/includes/header.jsp" />
    
    <div class="container py-4">
        <!-- Page Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="border-start border-4 border-primary ps-3">User Management</h1>
            <a href="<c:url value='/admin/users/add'/>" class="btn btn-primary">
                <i class="fas fa-plus me-1"></i> Add New User
            </a>
        </div>
        
        <!-- Alerts for messages -->
        <c:if test="${not empty message}">
            <div class="alert alert-${messageType} alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <!-- Filter Form -->
        <div class="card mb-4">
            <div class="card-header bg-light">
                <h5 class="mb-0"><i class="fas fa-filter me-2"></i>Filter Users</h5>
            </div>
            <div class="card-body">
                <form action="<c:url value='/admin/users'/>" method="get" id="filterForm" class="row g-3">
                    <div class="col-md-4">
                        <label for="searchName" class="form-label">Name/Username</label>
                        <input type="text" class="form-control" id="searchName" name="search" 
                               value="${param.search}" placeholder="Search by name or username">
                    </div>
                    <div class="col-md-3">
                        <label for="role" class="form-label">Role</label>
                        <select class="form-select" id="role" name="role">
                            <option value="" ${empty param.role ? 'selected' : ''}>All Roles</option>
                            <option value="ADMIN" ${param.role eq 'ADMIN' ? 'selected' : ''}>Administrator</option>
                            <option value="TRAINER" ${param.role eq 'TRAINER' ? 'selected' : ''}>Trainer</option>
                            <option value="TRAINEE" ${param.role eq 'TRAINEE' ? 'selected' : ''}>Trainee</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label for="status" class="form-label">Status</label>
                        <select class="form-select" id="status" name="status">
                            <option value="" ${empty param.status ? 'selected' : ''}>All Statuses</option>
                            <option value="ACTIVE" ${param.status eq 'ACTIVE' ? 'selected' : ''}>Active</option>
                            <option value="INACTIVE" ${param.status eq 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="fas fa-search me-1"></i> Apply Filters
                        </button>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Users Table -->
        <div class="card">
            <div class="card-header bg-light d-flex justify-content-between align-items-center">
                <h5 class="mb-0"><i class="fas fa-users me-2"></i>User List</h5>
                <span class="badge bg-primary">${users.size()} Users</span>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-light">
                            <tr>
                                <th scope="col">ID</th>
                                <th scope="col">Name</th>
                                <th scope="col">Username</th>
                                <th scope="col">Email</th>
                                <th scope="col">Role</th>
                                <th scope="col">Status</th>
                                <th scope="col" class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty users}">
                                    <tr>
                                        <td colspan="7" class="text-center py-4">
                                            <div class="alert alert-info mb-0">
                                                <i class="fas fa-info-circle me-2"></i> No users found matching your criteria.
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="user" items="${users}">
                                        <tr>
                                            <td>${user.id}</td>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <div class="avatar-circle-sm me-2">
                                                        <span class="avatar-initials-sm">${user.firstName.charAt(0)}${user.lastName.charAt(0)}</span>
                                                    </div>
                                                    ${user.firstName} ${user.lastName}
                                                </div>
                                            </td>
                                            <td>${user.username}</td>
                                            <td>${user.email}</td>
                                            <td>
                                                <span class="badge rounded-pill bg-${user.role eq 'ADMIN' ? 'danger' : user.role eq 'TRAINER' ? 'success' : 'info'}">
                                                    ${user.role}
                                                </span>
                                            </td>
                                            <td>
                                                <span class="badge rounded-pill bg-${user.active ? 'success' : 'secondary'}">
                                                    ${user.active ? 'Active' : 'Inactive'}
                                                </span>
                                            </td>
                                            <td class="text-center">
                                                <div class="btn-group" role="group">
                                                    <a href="<c:url value='/admin/users/edit?id=${user.id}'/>" class="btn btn-sm btn-outline-primary" 
                                                       data-bs-toggle="tooltip" title="Edit User">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <button type="button" class="btn btn-sm btn-outline-${user.active ? 'warning' : 'success'} btn-toggle-status" 
                                                            data-user-id="${user.id}" data-current-status="${user.active}"
                                                            data-bs-toggle="tooltip" title="${user.active ? 'Deactivate' : 'Activate'} User">
                                                        <i class="fas fa-${user.active ? 'ban' : 'check'}"></i>
                                                    </button>
                                                    <button type="button" class="btn btn-sm btn-outline-danger btn-delete" 
                                                            data-user-id="${user.id}" data-user-name="${user.firstName} ${user.lastName}"
                                                            data-bs-toggle="tooltip" title="Delete User">
                                                        <i class="fas fa-trash-alt"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
            <c:if test="${not empty users && users.size() > 10}">
                <div class="card-footer">
                    <nav aria-label="User navigation">
                        <ul class="pagination justify-content-center mb-0">
                            <li class="page-item disabled">
                                <a class="page-link" href="#" tabindex="-1" aria-disabled="true">Previous</a>
                            </li>
                            <li class="page-item active"><a class="page-link" href="#">1</a></li>
                            <li class="page-item"><a class="page-link" href="#">2</a></li>
                            <li class="page-item"><a class="page-link" href="#">3</a></li>
                            <li class="page-item">
                                <a class="page-link" href="#">Next</a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </c:if>
        </div>
    </div>
    
    <!-- Delete User Modal -->
    <div class="modal fade" id="deleteUserModal" tabindex="-1" aria-labelledby="deleteUserModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title" id="deleteUserModalLabel">
                        <i class="fas fa-exclamation-triangle me-2"></i>Delete User
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to delete the user <strong id="deleteUserName"></strong>? This action cannot be undone.</p>
                    <p>All data associated with this user will be permanently removed from the system.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form action="<c:url value='/admin/users/delete'/>" method="post" id="deleteUserForm">
                        <input type="hidden" name="userId" id="deleteUserId">
                        <button type="submit" class="btn btn-danger">Delete User</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Toggle Status Modal -->
    <div class="modal fade" id="toggleStatusModal" tabindex="-1" aria-labelledby="toggleStatusModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header" id="toggleStatusHeader">
                    <h5 class="modal-title" id="toggleStatusModalLabel">
                        <i class="fas fa-exclamation-circle me-2"></i><span id="toggleStatusTitle"></span>
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" id="toggleStatusBody">
                    <p id="toggleStatusMessage"></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form action="<c:url value='/admin/users/toggle-status'/>" method="post" id="toggleStatusForm">
                        <input type="hidden" name="userId" id="toggleStatusUserId">
                        <input type="hidden" name="active" id="toggleStatusValue">
                        <button type="submit" class="btn" id="toggleStatusButton">Confirm</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/includes/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<c:url value='/assets/js/script.js'/>"></script>
    
    <style>
        .avatar-circle-sm {
            width: 32px;
            height: 32px;
            background-color: var(--primary-color);
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        
        .avatar-initials-sm {
            color: white;
            font-size: 14px;
            font-weight: bold;
        }
    </style>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Setup delete user modal
            const deleteUserBtns = document.querySelectorAll('.btn-delete');
            deleteUserBtns.forEach(btn => {
                btn.addEventListener('click', function() {
                    const userId = this.getAttribute('data-user-id');
                    const userName = this.getAttribute('data-user-name');
                    
                    document.getElementById('deleteUserId').value = userId;
                    document.getElementById('deleteUserName').textContent = userName;
                    
                    // Show the modal
                    const deleteModal = new bootstrap.Modal(document.getElementById('deleteUserModal'));
                    deleteModal.show();
                });
            });
            
            // Setup toggle status modal
            const toggleStatusBtns = document.querySelectorAll('.btn-toggle-status');
            toggleStatusBtns.forEach(btn => {
                btn.addEventListener('click', function() {
                    const userId = this.getAttribute('data-user-id');
                    const currentStatus = this.getAttribute('data-current-status') === 'true';
                    const newStatus = !currentStatus;
                    
                    // Set modal content based on the action (activate or deactivate)
                    document.getElementById('toggleStatusUserId').value = userId;
                    document.getElementById('toggleStatusValue').value = newStatus;
                    
                    const header = document.getElementById('toggleStatusHeader');
                    const title = document.getElementById('toggleStatusTitle');
                    const message = document.getElementById('toggleStatusMessage');
                    const button = document.getElementById('toggleStatusButton');
                    
                    if (currentStatus) {
                        // Deactivating user
                        header.classList.remove('bg-success', 'text-white');
                        header.classList.add('bg-warning', 'text-white');
                        title.textContent = 'Deactivate User';
                        message.textContent = 'Are you sure you want to deactivate this user? They will no longer be able to log in to the system.';
                        button.classList.remove('btn-success');
                        button.classList.add('btn-warning');
                        button.textContent = 'Deactivate';
                    } else {
                        // Activating user
                        header.classList.remove('bg-warning', 'text-white');
                        header.classList.add('bg-success', 'text-white');
                        title.textContent = 'Activate User';
                        message.textContent = 'Are you sure you want to activate this user? They will be able to log in to the system.';
                        button.classList.remove('btn-warning');
                        button.classList.add('btn-success');
                        button.textContent = 'Activate';
                    }
                    
                    // Show the modal
                    const statusModal = new bootstrap.Modal(document.getElementById('toggleStatusModal'));
                    statusModal.show();
                });
            });
        });
    </script>
</body>
</html> 