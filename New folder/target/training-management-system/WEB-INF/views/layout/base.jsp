<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Training Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .sidebar {
            min-height: 100vh;
            background-color: #343a40;
            color: white;
        }
        .sidebar .nav-link {
            color: rgba(255,255,255,.75);
        }
        .sidebar .nav-link:hover {
            color: rgba(255,255,255,1);
        }
        .sidebar .nav-link.active {
            color: white;
            background-color: rgba(255,255,255,.1);
        }
        .main-content {
            padding: 20px;
        }
        .navbar-brand {
            padding: 15px;
            font-size: 1.25rem;
        }
        /* Loading Spinner */
        .loading-spinner {
            display: none;
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            z-index: 1000;
        }
        .loading-spinner.active {
            display: block;
        }
        /* Error Alert */
        .error-alert {
            display: none;
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1000;
        }
        .error-alert.active {
            display: block;
        }
        /* Responsive Tables */
        .table-responsive {
            overflow-x: auto;
        }
        @media (max-width: 768px) {
            .table-responsive {
                display: block;
                width: 100%;
                overflow-x: auto;
                -webkit-overflow-scrolling: touch;
            }
        }
    </style>
</head>
<body>
    <!-- Loading Spinner -->
    <div class="loading-spinner">
        <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>

    <!-- Error Alert -->
    <div class="error-alert alert alert-danger alert-dismissible fade show" role="alert">
        <span class="error-message"></span>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>

    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 d-md-block sidebar collapse">
                <div class="position-sticky pt-3">
                    <div class="text-center mb-4">
                        <h4>Training Management</h4>
                    </div>
                    <ul class="nav flex-column">
                        <c:if test="${user.role == 'ADMIN' || user.role == 'TRAINER'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/training-programs">
                                    <i class="fas fa-graduation-cap me-2"></i>Training Programs
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/courses">
                                    <i class="fas fa-book me-2"></i>Courses
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/sessions">
                                    <i class="fas fa-calendar-alt me-2"></i>Sessions
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/materials">
                                    <i class="fas fa-file-alt me-2"></i>Materials
                                </a>
                            </li>
                        </c:if>
                        <c:if test="${user.role == 'ADMIN'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/users">
                                    <i class="fas fa-users me-2"></i>Users
                                </a>
                            </li>
                        </c:if>
                        <c:if test="${user.role == 'TRAINEE'}">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/my-courses">
                                    <i class="fas fa-book me-2"></i>My Courses
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/my-assessments">
                                    <i class="fas fa-tasks me-2"></i>My Assessments
                                </a>
                            </li>
                        </c:if>
                    </ul>
                </div>
            </div>

            <!-- Main content -->
            <div class="col-md-9 col-lg-10 ms-sm-auto px-md-4">
                <!-- Top navigation -->
                <nav class="navbar navbar-expand-lg navbar-light bg-light mb-4">
                    <div class="container-fluid">
                        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#sidebar">
                            <span class="navbar-toggler-icon"></span>
                        </button>
                        <div class="d-flex align-items-center">
                            <span class="me-3">Welcome, ${user.firstName} ${user.lastName}</span>
                            <a href="${pageContext.request.contextPath}/login?action=logout" class="btn btn-outline-danger">
                                <i class="fas fa-sign-out-alt"></i> Logout
                            </a>
                        </div>
                    </div>
                </nav>

                <!-- Page content -->
                <div class="main-content">
                    <jsp:include page="${content}" />
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        // Show loading spinner
        function showLoading() {
            $('.loading-spinner').addClass('active');
        }

        // Hide loading spinner
        function hideLoading() {
            $('.loading-spinner').removeClass('active');
        }

        // Show error message
        function showError(message) {
            $('.error-message').text(message);
            $('.error-alert').addClass('active');
            setTimeout(() => {
                $('.error-alert').removeClass('active');
            }, 5000);
        }

        // Handle AJAX errors
        $(document).ajaxError(function(event, jqXHR, settings, error) {
            hideLoading();
            showError('An error occurred: ' + error);
        });

        // Show loading spinner on page load
        $(document).ready(function() {
            showLoading();
            setTimeout(hideLoading, 500);
        });
    </script>
</body>
</html> 