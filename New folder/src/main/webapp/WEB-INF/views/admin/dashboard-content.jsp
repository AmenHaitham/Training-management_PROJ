<div class="container-fluid">
    <h2 class="mb-4">Admin Dashboard</h2>
    
    <!-- Statistics Cards -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Training Programs</h5>
                    <h2 class="card-text">${stats.trainingProgramsCount}</h2>
                    <a href="${pageContext.request.contextPath}/training-programs" class="btn btn-primary btn-sm">View All</a>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Active Courses</h5>
                    <h2 class="card-text">${stats.activeCoursesCount}</h2>
                    <a href="${pageContext.request.contextPath}/courses" class="btn btn-primary btn-sm">View All</a>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Total Users</h5>
                    <h2 class="card-text">${stats.totalUsersCount}</h2>
                    <a href="${pageContext.request.contextPath}/users" class="btn btn-primary btn-sm">View All</a>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Upcoming Sessions</h5>
                    <h2 class="card-text">${stats.upcomingSessionsCount}</h2>
                    <a href="${pageContext.request.contextPath}/sessions" class="btn btn-primary btn-sm">View All</a>
                </div>
            </div>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="row mb-4">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Quick Actions</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/training-programs/new" class="btn btn-primary w-100 mb-2">
                                <i class="fas fa-plus me-2"></i>New Training Program
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/courses/new" class="btn btn-primary w-100 mb-2">
                                <i class="fas fa-plus me-2"></i>New Course
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/users/new" class="btn btn-primary w-100 mb-2">
                                <i class="fas fa-user-plus me-2"></i>Add User
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/sessions/new" class="btn btn-primary w-100 mb-2">
                                <i class="fas fa-calendar-plus me-2"></i>Schedule Session
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Activities -->
    <div class="row">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Recent Training Programs</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Start Date</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${recentPrograms}" var="program">
                                    <tr>
                                        <td>${program.name}</td>
                                        <td>${program.startDate}</td>
                                        <td>
                                            <span class="badge bg-${program.status == 'PLANNED' ? 'info' : 
                                                                   program.status == 'ONGOING' ? 'success' : 
                                                                   program.status == 'COMPLETED' ? 'secondary' : 'danger'}">
                                                ${program.status}
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/training-programs/${program.id}" 
                                               class="btn btn-sm btn-outline-primary">View</a>
                                        </td>
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
                    <h5 class="card-title mb-0">Recent Users</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Role</th>
                                    <th>Email</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${recentUsers}" var="user">
                                    <tr>
                                        <td>${user.firstName} ${user.lastName}</td>
                                        <td>
                                            <span class="badge bg-${user.role == 'ADMIN' ? 'danger' : 
                                                                   user.role == 'TRAINER' ? 'warning' : 'info'}">
                                                ${user.role}
                                            </span>
                                        </td>
                                        <td>${user.email}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/users/${user.id}" 
                                               class="btn btn-sm btn-outline-primary">View</a>
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