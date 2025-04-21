<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col-12 d-flex justify-content-between align-items-center">
            <h2>${program.title}</h2>
            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/training-programs/${program.id}/edit" 
                   class="btn btn-primary">
                    <i class="fas fa-edit me-2"></i> Edit
                </a>
                <a href="${pageContext.request.contextPath}/training-programs" 
                   class="btn btn-secondary">
                    <i class="fas fa-arrow-left me-2"></i> Back to List
                </a>
            </div>
        </div>
    </div>

    <div class="row">
        <!-- Program Details -->
        <div class="col-md-8">
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="card-title mb-0">Program Information</h5>
                </div>
                <div class="card-body">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <strong>Status:</strong>
                            <span class="badge bg-${program.status == 'ACTIVE' ? 'success' : 
                                                   program.status == 'COMPLETED' ? 'secondary' : 'warning'}">
                                ${program.status}
                            </span>
                        </div>
                        <div class="col-md-6">
                            <strong>Created By:</strong>
                            ${program.createdBy.firstName} ${program.createdBy.lastName}
                        </div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <strong>Start Date:</strong>
                            ${program.startDate}
                        </div>
                        <div class="col-md-6">
                            <strong>End Date:</strong>
                            ${program.endDate}
                        </div>
                    </div>
                    <div class="mb-3">
                        <strong>Description:</strong>
                        <p>${program.description}</p>
                    </div>
                </div>
            </div>

            <!-- Courses -->
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Courses</h5>
                    <a href="${pageContext.request.contextPath}/courses/new?programId=${program.id}" 
                       class="btn btn-sm btn-primary">
                        <i class="fas fa-plus me-2"></i> Add Course
                    </a>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Title</th>
                                    <th>Category</th>
                                    <th>Duration</th>
                                    <th>Trainer</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${program.courses}" var="course">
                                    <tr>
                                        <td>${course.title}</td>
                                        <td>${course.category}</td>
                                        <td>${course.duration} hours</td>
                                        <td>${course.assignedTrainer.firstName} ${course.assignedTrainer.lastName}</td>
                                        <td>
                                            <span class="badge bg-${course.status == 'ACTIVE' ? 'success' : 
                                                                   course.status == 'COMPLETED' ? 'secondary' : 'warning'}">
                                                ${course.status}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="btn-group">
                                                <a href="${pageContext.request.contextPath}/courses/${course.id}" 
                                                   class="btn btn-sm btn-outline-primary">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/courses/${course.id}/edit" 
                                                   class="btn btn-sm btn-outline-secondary">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Enrollments -->
        <div class="col-md-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Enrollments</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Trainee</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${program.enrollments}" var="enrollment">
                                    <tr>
                                        <td>${enrollment.trainee.firstName} ${enrollment.trainee.lastName}</td>
                                        <td>
                                            <span class="badge bg-${enrollment.status == 'ACTIVE' ? 'success' : 
                                                                   enrollment.status == 'COMPLETED' ? 'secondary' : 'warning'}">
                                                ${enrollment.status}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="btn-group">
                                                <a href="${pageContext.request.contextPath}/enrollments/${enrollment.id}" 
                                                   class="btn btn-sm btn-outline-primary">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div> 