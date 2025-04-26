<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Training Management System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
</head>
<body>
    <jsp:include page="/WEB-INF/includes/header.jsp" />
    
    <div class="container-fluid py-4">
        <div class="container">
            <!-- Dashboard Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h1 class="border-start border-4 border-primary ps-3">Admin Dashboard</h1>
                <div>
                    <a href="<c:url value='/admin/users/form'/>" class="btn btn-primary">
                        <i class="fas fa-user-plus me-1"></i> Add User
                    </a>
                    <a href="<c:url value='/admin/training-programs/form'/>" class="btn btn-outline-primary ms-2">
                        <i class="fas fa-plus-circle me-1"></i> New Program
                    </a>
                </div>
            </div>
            
            <!-- Stats Overview -->
            <div class="row g-4 mb-5">
                <!-- Total Users -->
                <div class="col-md-6 col-lg-3">
                    <div class="dashboard-card h-100">
                        <div class="dashboard-card-icon">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="dashboard-card-number">${totalUsers}</div>
                        <div class="text-muted">Total Users</div>
                        <div class="mt-3 small">
                            <span class="text-primary"><i class="fas fa-user-shield me-1"></i> ${adminCount} Admins</span> |
                            <span class="text-success"><i class="fas fa-chalkboard-teacher me-1"></i> ${trainerCount} Trainers</span> |
                            <span class="text-info"><i class="fas fa-user-graduate me-1"></i> ${traineeCount} Trainees</span>
                        </div>
                    </div>
                </div>
                
                <!-- Total Programs -->
                <div class="col-md-6 col-lg-3">
                    <div class="dashboard-card h-100">
                        <div class="dashboard-card-icon">
                            <i class="fas fa-list-alt"></i>
                        </div>
                        <div class="dashboard-card-number">${totalPrograms}</div>
                        <div class="text-muted">Training Programs</div>
                        <div class="mt-3 small">
                            <span class="text-success"><i class="fas fa-check-circle me-1"></i> ${activePrograms} Active</span> |
                            <span class="text-warning"><i class="fas fa-exclamation-circle me-1"></i> ${draftPrograms} Draft</span>
                        </div>
                    </div>
                </div>
                
                <!-- Total Enrollments -->
                <div class="col-md-6 col-lg-3">
                    <div class="dashboard-card h-100">
                        <div class="dashboard-card-icon">
                            <i class="fas fa-user-plus"></i>
                        </div>
                        <div class="dashboard-card-number">${totalEnrollments}</div>
                        <div class="text-muted">Enrollments</div>
                        <div class="mt-3 small">
                            <span class="text-primary"><i class="fas fa-clock me-1"></i> ${pendingEnrollments} Pending</span> |
                            <span class="text-success"><i class="fas fa-check me-1"></i> ${approvedEnrollments} Approved</span>
                        </div>
                    </div>
                </div>
                
                <!-- Completed Trainings -->
                <div class="col-md-6 col-lg-3">
                    <div class="dashboard-card h-100">
                        <div class="dashboard-card-icon">
                            <i class="fas fa-trophy"></i>
                        </div>
                        <div class="dashboard-card-number">${completedEnrollments}</div>
                        <div class="text-muted">Completed Trainings</div>
                        <div class="mt-3 small">
                            <span class="text-success"><i class="fas fa-graduation-cap me-1"></i> ${completedPrograms} Programs Completed</span>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Charts & Data Section -->
            <div class="row g-4 mb-5">
                <!-- Program Status Chart -->
                <div class="col-lg-6">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h5 class="mb-0"><i class="fas fa-chart-pie me-2"></i>Programs by Status</h5>
                        </div>
                        <div class="card-body">
                            <canvas id="programsChart" height="240"></canvas>
                        </div>
                    </div>
                </div>
                
                <!-- Enrollment Status Chart -->
                <div class="col-lg-6">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h5 class="mb-0"><i class="fas fa-chart-bar me-2"></i>Enrollments by Status</h5>
                        </div>
                        <div class="card-body">
                            <canvas id="enrollmentsChart" height="240"></canvas>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Recent Data Section -->
            <div class="row g-4">
                <!-- Recent Users -->
                <div class="col-lg-6">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h5 class="mb-0"><i class="fas fa-user-clock me-2"></i>Recent Users</h5>
                            <a href="<c:url value='/admin/users'/>" class="btn btn-sm btn-outline-primary">View All</a>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th scope="col">Name</th>
                                            <th scope="col">Username</th>
                                            <th scope="col">Role</th>
                                            <th scope="col">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="user" items="${recentUsers}">
                                            <tr>
                                                <td>${user.firstName} ${user.lastName}</td>
                                                <td>${user.username}</td>
                                                <td>
                                                    <span class="badge rounded-pill bg-${user.role eq 'ADMIN' ? 'danger' : user.role eq 'TRAINER' ? 'success' : 'info'}">
                                                        ${user.role}
                                                    </span>
                                                </td>
                                                <td>
                                                    <a href="<c:url value='/admin/users/form?id=${user.id}'/>" class="btn btn-sm btn-outline-primary">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty recentUsers}">
                                            <tr>
                                                <td colspan="4" class="text-center py-3">No users found</td>
                                            </tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Recent Programs -->
                <div class="col-lg-6">
                    <div class="card h-100">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h5 class="mb-0"><i class="fas fa-clipboard-list me-2"></i>Recent Programs</h5>
                            <a href="<c:url value='/training-programs'/>" class="btn btn-sm btn-outline-primary">View All</a>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th scope="col">Title</th>
                                            <th scope="col">Status</th>
                                            <th scope="col">Start Date</th>
                                            <th scope="col">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="program" items="${recentPrograms}">
                                            <tr>
                                                <td>${program.title}</td>
                                                <td>
                                                    <span class="badge rounded-pill bg-${program.status eq 'ACTIVE' ? 'success' : program.status eq 'DRAFT' ? 'warning' : program.status eq 'COMPLETED' ? 'info' : 'secondary'}">
                                                        ${program.status}
                                                    </span>
                                                </td>
                                                <td>
                                                    <c:if test="${not empty program.startDate}">
                                                        <fmt:formatDate value="${program.startDate}" pattern="MMM dd, yyyy" />
                                                    </c:if>
                                                    <c:if test="${empty program.startDate}">Not set</c:if>
                                                </td>
                                                <td>
                                                    <a href="<c:url value='/training-programs/details?id=${program.id}'/>" class="btn btn-sm btn-outline-primary">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                    <a href="<c:url value='/admin/training-programs/form?id=${program.id}'/>" class="btn btn-sm btn-outline-primary">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty recentPrograms}">
                                            <tr>
                                                <td colspan="4" class="text-center py-3">No programs found</td>
                                            </tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/includes/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="<c:url value='/assets/js/script.js'/>"></script>
    
    <script>
        // Initialize charts
        document.addEventListener('DOMContentLoaded', function() {
            // Programs chart
            const programsCtx = document.getElementById('programsChart').getContext('2d');
            const programsChart = new Chart(programsCtx, {
                type: 'pie',
                data: {
                    labels: ['Active', 'Draft', 'Completed', 'Cancelled'],
                    datasets: [{
                        data: [${activePrograms}, ${draftPrograms}, ${completedPrograms}, ${cancelledPrograms}],
                        backgroundColor: [
                            '#198754',  // Success
                            '#ffc107',  // Warning
                            '#0dcaf0',  // Info
                            '#6c757d'   // Secondary
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'right'
                        }
                    }
                }
            });
            
            // Enrollments chart
            const enrollmentsCtx = document.getElementById('enrollmentsChart').getContext('2d');
            const enrollmentsChart = new Chart(enrollmentsCtx, {
                type: 'bar',
                data: {
                    labels: ['Pending', 'Approved', 'Completed', 'Dropped'],
                    datasets: [{
                        label: 'Enrollments by Status',
                        data: [${pendingEnrollments}, ${approvedEnrollments}, ${completedEnrollments}, ${droppedEnrollments}],
                        backgroundColor: [
                            '#0d6efd',  // Primary
                            '#198754',  // Success
                            '#0dcaf0',  // Info
                            '#dc3545'   // Danger
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                precision: 0
                            }
                        }
                    }
                }
            });
        });
    </script>
</body>
</html> 