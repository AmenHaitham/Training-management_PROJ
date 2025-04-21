<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="container-fluid">
    <div class="row mb-4">
        <div class="col-12">
            <h2>${program.id == null ? 'New Training Program' : 'Edit Training Program'}</h2>
        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/training-programs${program.id != null ? '/' += program.id : ''}" 
                          method="post" id="programForm">
                        <input type="hidden" name="_method" value="${program.id == null ? 'POST' : 'PUT'}">
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="title" class="form-label">Title</label>
                                <input type="text" class="form-control" id="title" name="title" 
                                       value="${program.title}" required>
                            </div>
                            <div class="col-md-6">
                                <label for="status" class="form-label">Status</label>
                                <select class="form-select" id="status" name="status" required>
                                    <option value="">Select Status</option>
                                    <option value="ACTIVE" ${program.status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                                    <option value="COMPLETED" ${program.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                                    <option value="UPCOMING" ${program.status == 'UPCOMING' ? 'selected' : ''}>Upcoming</option>
                                </select>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="startDate" class="form-label">Start Date</label>
                                <input type="date" class="form-control" id="startDate" name="startDate" 
                                       value="${program.startDate}" required>
                            </div>
                            <div class="col-md-6">
                                <label for="endDate" class="form-label">End Date</label>
                                <input type="date" class="form-control" id="endDate" name="endDate" 
                                       value="${program.endDate}" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" 
                                      rows="4" required>${program.description}</textarea>
                        </div>

                        <div class="mb-3">
                            <label for="createdBy" class="form-label">Created By</label>
                            <select class="form-select" id="createdBy" name="createdBy" required>
                                <option value="">Select User</option>
                                <c:forEach items="${users}" var="user">
                                    <option value="${user.id}" ${program.createdBy.id == user.id ? 'selected' : ''}>
                                        ${user.firstName} ${user.lastName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="d-flex justify-content-end">
                            <a href="${pageContext.request.contextPath}/training-programs" class="btn btn-secondary me-2">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                ${program.id == null ? 'Create' : 'Update'} Program
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.getElementById('programForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const startDate = new Date(document.getElementById('startDate').value);
    const endDate = new Date(document.getElementById('endDate').value);
    
    if (endDate < startDate) {
        alert('End date cannot be before start date');
        return;
    }
    
    this.submit();
});
</script> 