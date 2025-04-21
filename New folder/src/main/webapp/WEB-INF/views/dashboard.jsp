<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col-12">
            <h2>Dashboard</h2>
            <p class="text-muted">Welcome to the Training Management System</p>
        </div>
    </div>

    <!-- Statistics Cards -->
    <div class="row">
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Active Programs</h5>
                    <h2 class="card-text">${activeProgramsCount}</h2>
                    <p class="text-muted">Running training programs</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Total Courses</h5>
                    <h2 class="card-text">${totalCoursesCount}</h2>
                    <p class="text-muted">Available courses</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Active Sessions</h5>
                    <h2 class="card-text">${activeSessionsCount}</h2>
                    <p class="text-muted">Ongoing sessions</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Total Users</h5>
                    <h2 class="card-text">${totalUsersCount}</h2>
                    <p class="text-muted">System users</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Activities -->
    <div class="row mt-4">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Recent Training Programs</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Title</th>
                                    <th>Start Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${recentPrograms}" var="program">
                                    <tr>
                                        <td>${program.title}</td>
                                        <td>${program.startDate}</td>
                                        <td><span class="badge bg-${program.status == 'ACTIVE' ? 'success' : 'warning'}">${program.status}</span></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Upcoming Sessions</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Course</th>
                                    <th>Date & Time</th>
                                    <th>Location</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${upcomingSessions}" var="session">
                                    <tr>
                                        <td>${session.course.title}</td>
                                        <td>${session.scheduledDatetime}</td>
                                        <td>${session.location}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="row mt-4">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Quick Actions</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/training-programs/new" class="btn btn-primary w-100 mb-2">
                                <i class="fas fa-plus me-2"></i> New Program
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/courses/new" class="btn btn-primary w-100 mb-2">
                                <i class="fas fa-plus me-2"></i> New Course
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/sessions/new" class="btn btn-primary w-100 mb-2">
                                <i class="fas fa-plus me-2"></i> New Session
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/users/new" class="btn btn-primary w-100 mb-2">
                                <i class="fas fa-plus me-2"></i> New User
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div> 