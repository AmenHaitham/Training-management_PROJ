<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Training Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .auth-container {
            min-height: calc(100vh - 200px);
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .auth-card {
            max-width: 900px;
            width: 100%;
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        }
        .auth-sidebar {
            background-color: var(--primary-color);
            color: white;
            padding: 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }
        .auth-form {
            padding: 40px;
            background-color: white;
        }
        .auth-tabs .nav-link {
            color: var(--secondary-color);
            border: none;
            padding: 10px 20px;
            border-radius: 0;
            font-weight: 500;
            border-bottom: 2px solid transparent;
        }
        .auth-tabs .nav-link.active {
            color: var(--primary-color);
            background-color: transparent;
            border-bottom: 2px solid var(--primary-color);
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/includes/header.jsp" />
    
    <div class="auth-container">
        <div class="container">
            <div class="auth-card card">
                <div class="row g-0">
                    <div class="col-lg-5 d-none d-lg-block">
                        <div class="auth-sidebar h-100">
                            <h2 class="fw-bold mb-4"><i class="fas fa-graduation-cap me-2"></i>TMS</h2>
                            <p class="fs-5 mb-4">Welcome to the Training Management System</p>
                            <div class="mb-5">
                                <p class="mb-3"><i class="fas fa-check-circle me-2"></i> Comprehensive training management</p>
                                <p class="mb-3"><i class="fas fa-check-circle me-2"></i> Role-based access control</p>
                                <p class="mb-3"><i class="fas fa-check-circle me-2"></i> Course enrollment tracking</p>
                                <p class="mb-3"><i class="fas fa-check-circle me-2"></i> Attendance and progress monitoring</p>
                            </div>
                            <div class="text-center mt-auto">
                                <img src="<c:url value='/assets/images/login-illustration.svg'/>" alt="Login" class="img-fluid" style="max-height: 200px;">
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-7">
                        <div class="auth-form">
                            <ul class="nav nav-tabs auth-tabs mb-4" id="authTab" role="tablist">
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link active" id="login-tab" data-bs-toggle="tab" data-bs-target="#login-tab-pane" type="button" role="tab" aria-controls="login-tab-pane" aria-selected="true">
                                        <i class="fas fa-sign-in-alt me-2"></i>Login
                                    </button>
                                </li>
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link" id="signup-tab" data-bs-toggle="tab" data-bs-target="#signup-tab-pane" type="button" role="tab" aria-controls="signup-tab-pane" aria-selected="false">
                                        <i class="fas fa-user-plus me-2"></i>Sign Up
                                    </button>
                                </li>
                            </ul>
                            
                            <div class="tab-content" id="authTabContent">
                                <!-- Login Tab -->
                                <div class="tab-pane fade show active" id="login-tab-pane" role="tabpanel" aria-labelledby="login-tab" tabindex="0">
                                    <h3 class="card-title mb-4">Login to Your Account</h3>
                                    
                                    <c:if test="${not empty errorMessage}">
                                        <div class="alert alert-danger" role="alert">
                                            <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                                        </div>
                                    </c:if>
                                    
                                    <form action="<c:url value='/login'/>" method="post" class="needs-validation" novalidate>
                                        <div class="mb-3">
                                            <label for="username" class="form-label">Email / Username</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                                <input type="text" class="form-control" id="username" name="username" required>
                                                <div class="invalid-feedback">
                                                    Please enter your username or email.
                                                </div>
                                            </div>
                                        </div>
                                        <div class="mb-4">
                                            <label for="password" class="form-label">Password</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                                <input type="password" class="form-control" id="password" name="password" required>
                                                <div class="invalid-feedback">
                                                    Please enter your password.
                                                </div>
                                            </div>
                                        </div>
                                        <div class="mb-3 form-check">
                                            <input type="checkbox" class="form-check-input" id="rememberMe" name="rememberMe">
                                            <label class="form-check-label" for="rememberMe">Remember me</label>
                                        </div>
                                        <button type="submit" class="btn btn-primary w-100 mb-3">
                                            <i class="fas fa-sign-in-alt me-2"></i>Login
                                        </button>
                                        <div class="text-center">
                                            <a href="#" class="text-decoration-none">Forgot Password?</a>
                                        </div>
                                    </form>
                                </div>
                                
                                <!-- Sign Up Tab -->
                                <div class="tab-pane fade" id="signup-tab-pane" role="tabpanel" aria-labelledby="signup-tab" tabindex="0">
                                    <h3 class="card-title mb-4">Create an Account</h3>
                                    
                                    <form action="<c:url value='/signup'/>" method="post" class="needs-validation" novalidate>
                                        <div class="row">
                                            <div class="col-md-6 mb-3">
                                                <label for="firstName" class="form-label">First Name</label>
                                                <input type="text" class="form-control" id="firstName" name="firstName" required>
                                                <div class="invalid-feedback">
                                                    Please enter your first name.
                                                </div>
                                            </div>
                                            <div class="col-md-6 mb-3">
                                                <label for="lastName" class="form-label">Last Name</label>
                                                <input type="text" class="form-control" id="lastName" name="lastName" required>
                                                <div class="invalid-feedback">
                                                    Please enter your last name.
                                                </div>
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label for="email" class="form-label">Email</label>
                                            <input type="email" class="form-control" id="email" name="email" required>
                                            <div class="invalid-feedback">
                                                Please enter a valid email address.
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label for="newUsername" class="form-label">Username</label>
                                            <input type="text" class="form-control" id="newUsername" name="username" required>
                                            <div class="invalid-feedback">
                                                Please choose a username.
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label for="newPassword" class="form-label">Password</label>
                                            <input type="password" class="form-control" id="newPassword" name="password" required>
                                            <div class="invalid-feedback">
                                                Please enter a password.
                                            </div>
                                        </div>
                                        <div class="mb-4">
                                            <label for="confirmPassword" class="form-label">Confirm Password</label>
                                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                                            <div class="invalid-feedback">
                                                Please confirm your password.
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label for="role" class="form-label">Role</label>
                                            <select class="form-select" id="role" name="role" required>
                                                <option value="">Select Role</option>
                                                <option value="TRAINEE">Trainee</option>
                                                <option value="TRAINER">Trainer</option>
                                                <option value="ADMIN">Admin</option>
                                            </select>
                                            <div class="invalid-feedback">
                                                Please select a role.
                                            </div>
                                        </div>
                                        <button type="submit" class="btn btn-primary w-100 mb-3">
                                            <i class="fas fa-user-plus me-2"></i>Sign Up
                                        </button>
                                    </form>
                                </div>
                            </div>
                            
                            <div class="mt-4 text-center">
                                <div class="small text-muted">
                                    <p class="mb-2">Default Credentials:</p>
                                    <p class="mb-1"><span class="fw-bold">Admin:</span> admin / admin123</p>
                                    <p class="mb-1"><span class="fw-bold">Trainer:</span> trainer / trainer123</p>
                                    <p class="mb-0"><span class="fw-bold">Trainee:</span> trainee / trainee123</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/includes/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<c:url value='/assets/js/script.js'/>"></script>
</body>
</html> 