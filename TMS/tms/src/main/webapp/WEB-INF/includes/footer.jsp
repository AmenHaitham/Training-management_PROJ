<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<footer class="py-5 mt-5">
    <div class="container">
        <div class="row">
            <div class="col-lg-4 mb-4 mb-lg-0">
                <h5 class="fw-bold mb-3"><i class="fas fa-graduation-cap me-2"></i>Training Management System</h5>
                <p class="text-white-50">A comprehensive system for managing training programs, courses, and enrollments.</p>
                <div class="mt-3">
                    <a href="#" class="text-white me-3"><i class="fab fa-facebook-f"></i></a>
                    <a href="#" class="text-white me-3"><i class="fab fa-twitter"></i></a>
                    <a href="#" class="text-white me-3"><i class="fab fa-linkedin-in"></i></a>
                    <a href="#" class="text-white"><i class="fab fa-instagram"></i></a>
                </div>
            </div>
            <div class="col-lg-2 col-md-6 mb-4 mb-md-0">
                <h5 class="fw-bold mb-3">Quick Links</h5>
                <ul class="list-unstyled">
                    <li class="mb-2"><a href="<c:url value='/'/>" class="text-white-50 text-decoration-none"><i class="fas fa-home me-2"></i>Home</a></li>
                    <li class="mb-2"><a href="<c:url value='/training-programs'/>" class="text-white-50 text-decoration-none"><i class="fas fa-list-alt me-2"></i>Programs</a></li>
                    <c:if test="${empty sessionScope.currentUser}">
                        <li class="mb-2"><a href="<c:url value='/login'/>" class="text-white-50 text-decoration-none"><i class="fas fa-sign-in-alt me-2"></i>Login</a></li>
                    </c:if>
                </ul>
            </div>
            <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
                <h5 class="fw-bold mb-3">Resources</h5>
                <ul class="list-unstyled">
                    <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none"><i class="fas fa-file-alt me-2"></i>Documentation</a></li>
                    <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none"><i class="fas fa-question-circle me-2"></i>FAQ</a></li>
                    <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none"><i class="fas fa-book me-2"></i>User Guide</a></li>
                </ul>
            </div>
            <div class="col-lg-3 col-md-6">
                <h5 class="fw-bold mb-3">Contact</h5>
                <address class="text-white-50">
                    <p><i class="fas fa-map-marker-alt me-2"></i>123 Training Street<br>Education City, ED 12345</p>
                    <p><i class="fas fa-envelope me-2"></i>info@tms.com</p>
                    <p><i class="fas fa-phone me-2"></i>(123) 456-7890</p>
                </address>
            </div>
        </div>
        <hr class="border-secondary my-4">
        <div class="row align-items-center">
            <div class="col-md-6 text-center text-md-start">
                <p class="small text-white-50 mb-0">&copy; <%= java.time.Year.now() %> Training Management System. All rights reserved.</p>
            </div>
            <div class="col-md-6 text-center text-md-end">
                <p class="small text-white-50 mb-0">
                    <a href="#" class="text-white-50 text-decoration-none">Privacy Policy</a> | 
                    <a href="#" class="text-white-50 text-decoration-none">Terms of Service</a>
                </p>
            </div>
        </div>
    </div>
</footer> 