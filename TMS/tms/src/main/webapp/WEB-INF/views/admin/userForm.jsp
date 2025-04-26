<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>${user == null ? 'Add New User' : 'Edit User'} - TMS</title>
    <jsp:include page="../common/header.jsp" />
</head>
<body>
    <jsp:include page="../common/navbar.jsp" />
    
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white py-3">
                        <h4 class="card-title mb-0">
                            ${user == null ? 'Add New User' : 'Edit User'}
                        </h4>
                    </div>
                    <div class="card-body p-4">
                        <!-- Flash messages -->
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                ${errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>
                        
                        <form id="userForm" method="POST" action="${pageContext.request.contextPath}/admin/users/${user == null ? 'add' : 'edit'}">
                            <c:if test="${user != null}">
                                <input type="hidden" name="id" value="${user.id}">
                            </c:if>
                            
                            <div class="row g-3">
                                <!-- First Name & Last Name -->
                                <div class="col-md-6">
                                    <label for="firstName" class="form-label">First Name <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="firstName" name="firstName" 
                                           value="${user != null ? user.firstName : ''}" required>
                                    <div class="invalid-feedback">First name is required</div>
                                </div>
                                <div class="col-md-6">
                                    <label for="lastName" class="form-label">Last Name <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="lastName" name="lastName" 
                                           value="${user != null ? user.lastName : ''}" required>
                                    <div class="invalid-feedback">Last name is required</div>
                                </div>
                                
                                <!-- Username -->
                                <div class="col-md-6">
                                    <label for="username" class="form-label">Username <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="username" name="username" 
                                           value="${user != null ? user.username : ''}" required>
                                    <div class="invalid-feedback">Username is required</div>
                                </div>
                                
                                <!-- Email -->
                                <div class="col-md-6">
                                    <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                                    <input type="email" class="form-control" id="email" name="email" 
                                           value="${user != null ? user.email : ''}" required>
                                    <div class="invalid-feedback">Valid email is required</div>
                                </div>
                                
                                <!-- Password - only for new users -->
                                <c:if test="${user == null}">
                                    <div class="col-md-6">
                                        <label for="password" class="form-label">Password</label>
                                        <div class="input-group">
                                            <input type="text" class="form-control" id="password" name="password" 
                                                   value="${generatedPassword}" readonly>
                                            <button class="btn btn-outline-secondary" type="button" id="generatePassword">
                                                <i class="bi bi-arrow-clockwise"></i>
                                            </button>
                                            <button class="btn btn-outline-secondary" type="button" id="copyPassword" 
                                                    data-bs-toggle="tooltip" title="Copy to clipboard">
                                                <i class="bi bi-clipboard"></i>
                                            </button>
                                        </div>
                                        <small class="form-text text-muted">
                                            This is an auto-generated password. The user will be prompted to change it on first login.
                                        </small>
                                    </div>
                                </c:if>
                                
                                <!-- Role -->
                                <div class="col-md-6">
                                    <label for="role" class="form-label">Role <span class="text-danger">*</span></label>
                                    <select class="form-select" id="role" name="role" required>
                                        <option value="" selected disabled>Select Role</option>
                                        <option value="ADMIN" ${user != null && user.role == 'ADMIN' ? 'selected' : ''}>Admin</option>
                                        <option value="TRAINER" ${user != null && user.role == 'TRAINER' ? 'selected' : ''}>Trainer</option>
                                        <option value="TRAINEE" ${user != null && user.role == 'TRAINEE' ? 'selected' : ''}>Trainee</option>
                                    </select>
                                    <div class="invalid-feedback">Please select a role</div>
                                </div>
                                
                                <!-- Status - only for editing users -->
                                <c:if test="${user != null}">
                                    <div class="col-md-6">
                                        <label for="active" class="form-label">Status</label>
                                        <div class="form-check form-switch mt-2">
                                            <input class="form-check-input" type="checkbox" id="active" name="active" 
                                                   ${user != null && user.active ? 'checked' : ''}>
                                            <label class="form-check-label" for="active">
                                                Active User
                                            </label>
                                        </div>
                                    </div>
                                </c:if>
                                
                                <!-- Form buttons -->
                                <div class="col-12 mt-4">
                                    <hr>
                                    <div class="d-flex justify-content-between">
                                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-secondary">
                                            <i class="bi bi-arrow-left me-1"></i> Back to Users
                                        </a>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-save me-1"></i> 
                                            ${user == null ? 'Save User' : 'Update User'}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Form validation
            const form = document.getElementById('userForm');
            
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                
                form.classList.add('was-validated');
            });
            
            // Password generation and copying (for new users only)
            const generatePasswordBtn = document.getElementById('generatePassword');
            const copyPasswordBtn = document.getElementById('copyPassword');
            const passwordInput = document.getElementById('password');
            
            if (generatePasswordBtn) {
                generatePasswordBtn.addEventListener('click', function() {
                    // Generate a random password
                    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*';
                    let password = '';
                    
                    for (let i = 0; i < 12; i++) {
                        password += chars.charAt(Math.floor(Math.random() * chars.length));
                    }
                    
                    passwordInput.value = password;
                });
            }
            
            if (copyPasswordBtn) {
                copyPasswordBtn.addEventListener('click', function() {
                    // Copy password to clipboard
                    passwordInput.select();
                    document.execCommand('copy');
                    
                    // Show tooltip with "Copied!" message
                    const tooltip = bootstrap.Tooltip.getInstance(copyPasswordBtn);
                    const originalTitle = copyPasswordBtn.getAttribute('data-bs-original-title');
                    
                    copyPasswordBtn.setAttribute('data-bs-original-title', 'Copied!');
                    tooltip.show();
                    
                    // Reset tooltip after 1.5 seconds
                    setTimeout(function() {
                        copyPasswordBtn.setAttribute('data-bs-original-title', originalTitle);
                        tooltip.hide();
                    }, 1500);
                });
                
                // Initialize the tooltip
                new bootstrap.Tooltip(copyPasswordBtn);
            }
        });
    </script>
</body>
</html> 