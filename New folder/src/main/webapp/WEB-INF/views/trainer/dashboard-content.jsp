<div class="container-fluid">
    <h2 class="mb-4">Trainer Dashboard</h2>
    
    <!-- Statistics Cards -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">My Courses</h5>
                    <h2 class="card-text">${stats.myCoursesCount}</h2>
                    <a href="${pageContext.request.contextPath}/courses" class="btn btn-primary btn-sm">View All</a>
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
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Total Trainees</h5>
                    <h2 class="card-text">${stats.totalTraineesCount}</h2>
                    <a href="${pageContext.request.contextPath}/trainees" class="btn btn-primary btn-sm">View All</a>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Pending Assessments</h5>
                    <h2 class="card-text">${stats.pendingAssessmentsCount}</h2>
                    <a href="${pageContext.request.contextPath}/assessments" class="btn btn-primary btn-sm">View All</a>
                </div>
            </div>
        </div>
    </div>

    <!-- Upcoming Sessions -->
    <div class="row mb-4">
        <div class="col-12">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Upcoming Sessions</h5>
                    <a href="${pageContext.request.contextPath}/sessions/new" class="btn btn-primary btn-sm">
                        <i class="fas fa-plus me-2"></i>Schedule Session
                    </a>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Course</th>
                                    <th>Session</th>
                                    <th>Date & Time</th>
                                    <th>Location</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${upcomingSessions}" var="session">
                                    <tr>
                                        <td>${session.courseName}</td>
                                        <td>${session.name}</td>
                                        <td>${session.startTime} - ${session.endTime}</td>
                                        <td>${session.location}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/sessions/${session.id}" 
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

    <!-- My Courses -->
    <div class="row">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">My Courses</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Course</th>
                                    <th>Program</th>
                                    <th>Duration</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${myCourses}" var="course">
                                    <tr>
                                        <td>${course.name}</td>
                                        <td>${course.programName}</td>
                                        <td>${course.duration} hours</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/courses/${course.id}" 
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
                    <h5 class="card-title mb-0">Recent Assessments</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Course</th>
                                    <th>Assessment</th>
                                    <th>Due Date</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${recentAssessments}" var="assessment">
                                    <tr>
                                        <td>${assessment.courseName}</td>
                                        <td>${assessment.name}</td>
                                        <td>${assessment.dueDate}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/assessments/${assessment.id}" 
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