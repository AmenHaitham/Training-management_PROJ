<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${empty program.id ? 'Create' : 'Edit'} Training Program | TMS</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="/WEB-INF/views/common/header.jsp" />
    
    <main class="container my-4">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h2 class="card-title h4 mb-0">
                            <i class="bi bi-mortarboard-fill me-2"></i>
                            ${empty program.id ? 'Create New Training Program' : 'Edit Training Program'}
                        </h2>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger" role="alert">
                                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                                ${errorMessage}
                            </div>
                        </c:if>
                        
                        <form action="${pageContext.request.contextPath}/program/${empty program.id ? 'create' : 'edit/'}${program.id}" 
                              method="post" class="needs-validation" novalidate>
                            
                            <div class="mb-3">
                                <label for="title" class="form-label">Program Title <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="title" name="title" 
                                       value="${program.title}" required>
                                <div class="invalid-feedback">Program title is required</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="description" class="form-label">Description <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="description" name="description" 
                                          rows="4" required>${program.description}</textarea>
                                <div class="invalid-feedback">Description is required</div>
                            </div>
                            
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="startDate" class="form-label">Start Date <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="startDate" name="startDate" 
                                           value="<fmt:formatDate value="${program.startDate}" pattern="yyyy-MM-dd" />" required>
                                    <div class="invalid-feedback">Start date is required</div>
                                </div>
                                
                                <div class="col-md-6">
                                    <label for="endDate" class="form-label">End Date <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" id="endDate" name="endDate" 
                                           value="<fmt:formatDate value="${program.endDate}" pattern="yyyy-MM-dd" />" required>
                                    <div class="invalid-feedback">End date is required</div>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="status" class="form-label">Status <span class="text-danger">*</span></label>
                                <select class="form-select" id="status" name="status" required>
                                    <option value="" disabled selected>Select status</option>
                                    <option value="DRAFT" ${program.status == 'DRAFT' ? 'selected' : ''}>Draft</option>
                                    <option value="ACTIVE" ${program.status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                                    <option value="COMPLETED" ${program.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                                    <option value="CANCELLED" ${program.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                </select>
                                <div class="invalid-feedback">Status is required</div>
                            </div>
                            
                            <div class="d-flex justify-content-between mt-4">
                                <a href="${pageContext.request.contextPath}/programs" class="btn btn-secondary">
                                    <i class="bi bi-arrow-left"></i> Back to Programs
                                </a>
                                
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-save"></i> ${empty program.id ? 'Create' : 'Update'} Program
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Set minimum date for start date to today
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('startDate').setAttribute('min', today);
            
            // Update end date minimum when start date changes
            document.getElementById('startDate').addEventListener('change', function() {
                document.getElementById('endDate').setAttribute('min', this.value);
                
                // If end date is before start date, set it to start date
                if (document.getElementById('endDate').value < this.value) {
                    document.getElementById('endDate').value = this.value;
                }
            });
        });
    </script>
</body>
</html> 