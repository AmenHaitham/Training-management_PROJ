<div class="container-fluid">
    <h2 class="mb-4">Trainee Dashboard</h2>
    
    <!-- Statistics Cards -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Enrolled Programs</h5>
                    <h2 class="card-text">${stats.enrolledProgramsCount}</h2>
                    <a href="${pageContext.request.contextPath}/my-courses" class="btn btn-primary btn-sm">View All</a>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Upcoming Sessions</h5>
                    <h2 class="card-text">${stats.upcomingSessionsCount}</h2>
                    <a href="${pageContext.request.contextPath}/my-sessions" class="btn btn-primary btn-sm">View All</a>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Pending Assessments</h5>
                    <h2 class="card-text">${stats.pendingAssessmentsCount}</h2>
                    <a href="${pageContext.request.contextPath}/my-assessments" class="btn btn-primary btn-sm">View All</a>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Attendance Rate</h5>
                    <h2 class="card-text">${stats.attendanceRate}%</h2>
                    <a href="${pageContext.request.contextPath}/my-attendance" class="btn btn-primary btn-sm">View Details</a>
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
                    <div class="input-group" style="width: 300px;">
                        <input type="text" class="form-control" id="sessionSearch" placeholder="Search sessions...">
                        <button class="btn btn-outline-secondary" type="button" id="sessionSearchBtn">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover" id="sessionsTable">
                            <thead>
                                <tr>
                                    <th>Program</th>
                                    <th>Course</th>
                                    <th>Session</th>
                                    <th>Date & Time</th>
                                    <th>Location</th>
                                    <th>Trainer</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${upcomingSessions}" var="session">
                                    <tr data-id="${session.id}">
                                        <td>${session.programName}</td>
                                        <td>${session.courseName}</td>
                                        <td>${session.name}</td>
                                        <td>${session.startTime} - ${session.endTime}</td>
                                        <td>${session.location}</td>
                                        <td>${session.trainerName}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <div class="form-group">
                            <select class="form-select" id="sessionPageSize">
                                <option value="5">5 per page</option>
                                <option value="10">10 per page</option>
                                <option value="25">25 per page</option>
                                <option value="50">50 per page</option>
                            </select>
                        </div>
                        <nav>
                            <ul class="pagination mb-0" id="sessionsPagination">
                                <!-- Pagination will be added dynamically -->
                            </ul>
                        </nav>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- My Courses and Assessments -->
    <div class="row">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">My Courses</h5>
                    <div class="input-group" style="width: 250px;">
                        <input type="text" class="form-control" id="courseSearch" placeholder="Search courses...">
                        <button class="btn btn-outline-secondary" type="button" id="courseSearchBtn">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover" id="coursesTable">
                            <thead>
                                <tr>
                                    <th>Program</th>
                                    <th>Course</th>
                                    <th>Progress</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${myCourses}" var="course">
                                    <tr data-id="${course.id}">
                                        <td>${course.programName}</td>
                                        <td>${course.name}</td>
                                        <td>
                                            <div class="progress">
                                                <div class="progress-bar" role="progressbar" 
                                                     style="width: ${course.progress}%;" 
                                                     aria-valuenow="${course.progress}" 
                                                     aria-valuemin="0" 
                                                     aria-valuemax="100">
                                                    ${course.progress}%
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/courses/${course.id}" 
                                               class="btn btn-sm btn-outline-primary">View</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <div class="form-group">
                            <select class="form-select" id="coursePageSize">
                                <option value="5">5 per page</option>
                                <option value="10">10 per page</option>
                                <option value="25">25 per page</option>
                                <option value="50">50 per page</option>
                            </select>
                        </div>
                        <nav>
                            <ul class="pagination mb-0" id="coursesPagination">
                                <!-- Pagination will be added dynamically -->
                            </ul>
                        </nav>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Recent Assessments</h5>
                    <div class="input-group" style="width: 250px;">
                        <input type="text" class="form-control" id="assessmentSearch" placeholder="Search assessments...">
                        <button class="btn btn-outline-secondary" type="button" id="assessmentSearchBtn">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover" id="assessmentsTable">
                            <thead>
                                <tr>
                                    <th>Course</th>
                                    <th>Assessment</th>
                                    <th>Due Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${recentAssessments}" var="assessment">
                                    <tr data-id="${assessment.id}">
                                        <td>${assessment.courseName}</td>
                                        <td>${assessment.name}</td>
                                        <td>${assessment.dueDate}</td>
                                        <td>
                                            <span class="badge bg-${assessment.status == 'PENDING' ? 'warning' : 
                                                                   assessment.status == 'COMPLETED' ? 'success' : 'danger'}">
                                                ${assessment.status}
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <div class="form-group">
                            <select class="form-select" id="assessmentPageSize">
                                <option value="5">5 per page</option>
                                <option value="10">10 per page</option>
                                <option value="25">25 per page</option>
                                <option value="50">50 per page</option>
                            </select>
                        </div>
                        <nav>
                            <ul class="pagination mb-0" id="assessmentsPagination">
                                <!-- Pagination will be added dynamically -->
                            </ul>
                        </nav>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        $(document).ready(function() {
            // Initialize tables with pagination
            function initializeTable(tableId, pageSizeId, paginationId) {
                const table = $(`#${tableId}`);
                const pageSize = $(`#${pageSizeId}`);
                const pagination = $(`#${paginationId}`);
                const rows = table.find('tbody tr');
                let currentPage = 1;
                let itemsPerPage = parseInt(pageSize.val());

                function updatePagination() {
                    const totalPages = Math.ceil(rows.length / itemsPerPage);
                    let paginationHtml = '';

                    // Previous button
                    paginationHtml += `
                        <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                            <a class="page-link" href="#" data-page="${currentPage - 1}">Previous</a>
                        </li>
                    `;

                    // Page numbers
                    for (let i = 1; i <= totalPages; i++) {
                        paginationHtml += `
                            <li class="page-item ${currentPage === i ? 'active' : ''}">
                                <a class="page-link" href="#" data-page="${i}">${i}</a>
                            </li>
                        `;
                    }

                    // Next button
                    paginationHtml += `
                        <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="#" data-page="${currentPage + 1}">Next</a>
                        </li>
                    `;

                    pagination.html(paginationHtml);
                }

                function showPage(page) {
                    const start = (page - 1) * itemsPerPage;
                    const end = start + itemsPerPage;

                    rows.hide();
                    rows.slice(start, end).show();
                }

                // Event handlers
                pageSize.on('change', function() {
                    itemsPerPage = parseInt($(this).val());
                    currentPage = 1;
                    showPage(currentPage);
                    updatePagination();
                });

                pagination.on('click', '.page-link', function(e) {
                    e.preventDefault();
                    const page = parseInt($(this).data('page'));
                    if (page >= 1 && page <= Math.ceil(rows.length / itemsPerPage)) {
                        currentPage = page;
                        showPage(currentPage);
                        updatePagination();
                    }
                });

                // Initialize
                showPage(currentPage);
                updatePagination();
            }

            // Initialize all tables
            initializeTable('sessionsTable', 'sessionPageSize', 'sessionsPagination');
            initializeTable('coursesTable', 'coursePageSize', 'coursesPagination');
            initializeTable('assessmentsTable', 'assessmentPageSize', 'assessmentsPagination');

            // Search functionality
            function setupSearch(searchInputId, searchButtonId, tableId) {
                const searchInput = $(`#${searchInputId}`);
                const searchButton = $(`#${searchButtonId}`);
                const table = $(`#${tableId}`);

                function performSearch() {
                    const searchText = searchInput.val().toLowerCase();
                    table.find('tbody tr').each(function() {
                        const rowText = $(this).text().toLowerCase();
                        $(this).toggle(rowText.includes(searchText));
                    });
                }

                searchInput.on('keyup', performSearch);
                searchButton.on('click', performSearch);
            }

            setupSearch('sessionSearch', 'sessionSearchBtn', 'sessionsTable');
            setupSearch('courseSearch', 'courseSearchBtn', 'coursesTable');
            setupSearch('assessmentSearch', 'assessmentSearchBtn', 'assessmentsTable');

            // Row click handlers
            $('table tbody tr').on('click', function() {
                const id = $(this).data('id');
                if (id) {
                    const tableId = $(this).closest('table').attr('id');
                    let url = '';
                    switch (tableId) {
                        case 'sessionsTable':
                            url = `${pageContext.request.contextPath}/sessions/\${id}`;
                            break;
                        case 'coursesTable':
                            url = `${pageContext.request.contextPath}/courses/\${id}`;
                            break;
                        case 'assessmentsTable':
                            url = `${pageContext.request.contextPath}/assessments/\${id}`;
                            break;
                    }
                    if (url) {
                        window.location.href = url;
                    }
                }
            });
        });
    </script>
</div> 