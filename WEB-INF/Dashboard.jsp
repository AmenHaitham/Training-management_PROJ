<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="CSS/Dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>

<body>
    <div class="container">
        <aside class="sidebar">
            <div class="logo">
                <i class="fas fa-graduation-cap"></i>
                <span>Admin Dashboard</span>
            </div>
            <ul class="nav">
                <li class="active" id="dashboard-link"><i class="fas fa-tachometer-alt"></i> Dashboard</li>
                <li id="users-link"><i class="fas fa-users"></i> Users</li>
                <li id="courses-link"><i class="fas fa-book"></i> Courses</li>
                <li id="sessions-link"><i class="fas fa-calendar-alt"></i> Sessions</li>
                <li id="trainings-link"><i class="fas fa-chalkboard-teacher"></i> Trainings</li>
                <li class="logout" id="logout-link"><i class="fas fa-sign-out-alt"></i>Logout</li>
            </ul>
        </aside>
        <main class="main-content">
            <div class="dashboard-title">
                <h1>Dashboard Overview</h1>
                <div class="date-display">Today: <span id="current-date"></span></div>
            </div>
            
            <div class="cards">
                <div class="card">
                    <div class="card-icon users">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="card-content">
                        <h3>Total Users</h3>
                        <span class="count">1,250</span>
                        <span class="trend up"><i class="fas fa-arrow-up"></i> 12% from last month</span>
                    </div>
                </div>
                <div class="card">
                    <div class="card-icon courses">
                        <i class="fas fa-book"></i>
                    </div>
                    <div class="card-content">
                        <h3>Total Courses</h3>
                        <span class="count">150</span>
                        <span class="trend up"><i class="fas fa-arrow-up"></i> 5% from last month</span>
                    </div>
                </div>
                <div class="card">
                    <div class="card-icon trainings">
                        <i class="fas fa-chalkboard-teacher"></i>
                    </div>
                    <div class="card-content">
                        <h3>Total Trainings</h3>
                        <span class="count">85</span>
                        <span class="trend up"><i class="fas fa-arrow-up"></i> 7% from last month</span>
                    </div>
                </div>
                <div class="card">
                    <div class="card-icon sessions">
                        <i class="fas fa-calendar-alt"></i>
                    </div>
                    <div class="card-content">
                        <h3>Total Sessions</h3>
                        <span class="count">350</span>
                        <span class="trend down"><i class="fas fa-arrow-down"></i> 3% from last month</span>
                    </div>
                </div>
                <div class="card">
                    <div class="card-icon completed">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="card-content">
                        <h3>Completed Sessions</h3>
                        <span class="count">280</span>
                        <span class="trend up"><i class="fas fa-arrow-up"></i> 8% from last month</span>
                    </div>
                </div>
            </div>
            
            <div class="content-grid">
                <section class="user-management">
                    <div class="section-header">
                        <h2>User Management</h2>
                        <a href="#" class="view-all">View All <i class="fas fa-chevron-right"></i></a>
                    </div>
                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Role</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>John Doe</td>
                                    <td>john@example.com</td>
                                    <td><span class="badge user">User</span></td>
                                    <td><span class="status active">Active</span></td>
                                    <td>
                                        <button class="action-btn edit"><i class="fas fa-edit"></i></button>
                                        <button class="action-btn delete"><i class="fas fa-trash"></i></button>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Jane Smith</td>
                                    <td>jane@example.com</td>
                                    <td><span class="badge instructor">Instructor</span></td>
                                    <td><span class="status active">Active</span></td>
                                    <td>
                                        <button class="action-btn edit"><i class="fas fa-edit"></i></button>
                                        <button class="action-btn delete"><i class="fas fa-trash"></i></button>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Emily Johnson</td>
                                    <td>emily@example.com</td>
                                    <td><span class="badge user">User</span></td>
                                    <td><span class="status inactive">Inactive</span></td>
                                    <td>
                                        <button class="action-btn edit"><i class="fas fa-edit"></i></button>
                                        <button class="action-btn delete"><i class="fas fa-trash"></i></button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </section>
                
                <section class="training-management">
                    <div class="section-header">
                        <h2>Training Management</h2>
                        <a href="Trainings.jsp" class="view-all">View All <i class="fas fa-chevron-right"></i></a>
                    </div>
                    <div class="training-table">
                        <table>
                            <thead>
                                <tr>
                                    <th>Training Name</th>
                                    <th>Status</th>
                                    <th>Completion</th>
                                    <th>Date</th>
                                    <th>Rating</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><i class="fas fa-chalkboard-teacher"></i> Advanced Web Development</td>
                                    <td>Completed</td>
                                    <td>25/30 participants</td>
                                    <td>May 15, 2023</td>
                                    <td><i class="fas fa-star"></i> 4.7</td>
                                </tr>
                                <tr>
                                    <td><i class="fas fa-network-wired"></i> Network Security</td>
                                    <td>Completed</td>
                                    <td>18/20 participants</td>
                                    <td>June 2, 2023</td>
                                    <td><i class="fas fa-star"></i> 4.9</td>
                                </tr>
                                <tr>
                                    <td><i class="fas fa-cloud"></i> Cloud Computing</td>
                                    <td>Ongoing</td>
                                    <td>12/15 participants</td>
                                    <td>June 10, 2023</td>
                                    <td><i class="fas fa-star"></i> 4.5</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </section>

                <section class="course-management">
                    <div class="section-header">
                        <h2>Course Management</h2>
                        <a href="#" class="view-all">View All <i class="fas fa-chevron-right"></i></a>
                    </div>
                    <div class="course-list">
                        <div class="course-item">
                            <div class="course-icon">
                                <i class="fas fa-laptop-code"></i>
                            </div>
                            <div class="course-info">
                                <h3>Course A</h3>
                                <p>Web Development Fundamentals</p>
                                <div class="course-stats">
                                    <span><i class="fas fa-users"></i> 120 enrolled</span>
                                    <span><i class="fas fa-star"></i> 4.8</span>
                                </div>
                            </div>
                        </div>
                        <div class="course-item">
                            <div class="course-icon">
                                <i class="fas fa-mobile-alt"></i>
                            </div>
                            <div class="course-info">
                                <h3>Course B</h3>
                                <p>Mobile App Design</p>
                                <div class="course-stats">
                                    <span><i class="fas fa-users"></i> 85 enrolled</span>
                                    <span><i class="fas fa-star"></i> 4.6</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </main>
    </div>

    <script src="Js/Dashboard.js"></script>
</body>

</html>
