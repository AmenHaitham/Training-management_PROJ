<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header>
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="<c:url value='/'/>">
                <i class="fas fa-graduation-cap me-2"></i>TMS
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/'/>">
                            <i class="fas fa-home me-1"></i> Home
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/training-programs'/>">
                            <i class="fas fa-list-alt me-1"></i> Training Programs
                        </a>
                    </li>
                    
                    <c:if test="${not empty sessionScope.currentUser}">
                        <c:if test="${sessionScope.currentUser.role eq 'ADMIN'}">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="adminDropdown" role="button"
                                   data-bs-toggle="dropdown" aria-expanded="false">
                                    <i class="fas fa-user-shield me-1"></i> Admin
                                </a>
                                <ul class="dropdown-menu" aria-labelledby="adminDropdown">
                                    <li><a class="dropdown-item" href="<c:url value='/admin/users'/>"><i class="fas fa-users me-1"></i> Users</a></li>
                                    <li><a class="dropdown-item" href="<c:url value='/admin/training-programs/form'/>"><i class="fas fa-plus-circle me-1"></i> Create Training Program</a></li>
                                    <li><a class="dropdown-item" href="<c:url value='/admin/enrollments'/>"><i class="fas fa-user-graduate me-1"></i> Enrollments</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item" href="<c:url value='/admin/dashboard'/>"><i class="fas fa-tachometer-alt me-1"></i> Dashboard</a></li>
                                </ul>
                            </li>
                        </c:if>
                        
                        <c:if test="${sessionScope.currentUser.role eq 'TRAINER'}">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="trainerDropdown" role="button"
                                   data-bs-toggle="dropdown" aria-expanded="false">
                                    <i class="fas fa-chalkboard-teacher me-1"></i> Trainer
                                </a>
                                <ul class="dropdown-menu" aria-labelledby="trainerDropdown">
                                    <li><a class="dropdown-item" href="<c:url value='/trainer/training-programs/form'/>"><i class="fas fa-plus-circle me-1"></i> Create Training Program</a></li>
                                    <li><a class="dropdown-item" href="<c:url value='/trainer/enrollments'/>"><i class="fas fa-user-graduate me-1"></i> Enrollments</a></li>
                                    <li><a class="dropdown-item" href="<c:url value='/trainer/sessions'/>"><i class="fas fa-calendar-alt me-1"></i> Sessions</a></li>
                                    <li><a class="dropdown-item" href="<c:url value='/trainer/attendance'/>"><i class="fas fa-clipboard-check me-1"></i> Attendance</a></li>
                                </ul>
                            </li>
                        </c:if>
                        
                        <c:if test="${sessionScope.currentUser.role eq 'TRAINEE'}">
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value='/trainee/enrollments'/>">
                                    <i class="fas fa-book-reader me-1"></i> My Enrollments
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value='/trainee/certificates'/>">
                                    <i class="fas fa-certificate me-1"></i> Certificates
                                </a>
                            </li>
                        </c:if>
                    </c:if>
                </ul>
                
                <div class="d-flex">
                    <c:choose>
                        <c:when test="${empty sessionScope.currentUser}">
                            <a href="<c:url value='/login'/>" class="btn btn-outline-light me-2">
                                <i class="fas fa-sign-in-alt me-1"></i> Login
                            </a>
                        </c:when>
                        <c:otherwise>
                            <div class="dropdown">
                                <button class="btn btn-outline-light dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                                    <i class="fas fa-user-circle me-1"></i> ${sessionScope.currentUser.firstName} ${sessionScope.currentUser.lastName}
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                                    <li><a class="dropdown-item" href="<c:url value='/profile'/>"><i class="fas fa-id-card me-1"></i> My Profile</a></li>
                                    <li><a class="dropdown-item" href="<c:url value='/change-password'/>"><i class="fas fa-key me-1"></i> Change Password</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item" href="<c:url value='/logout'/>"><i class="fas fa-sign-out-alt me-1"></i> Logout</a></li>
                                </ul>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </nav>
</header> 