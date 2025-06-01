document.addEventListener('DOMContentLoaded', function() {
            // Fetch dashboard statistics
            fetch('http://localhost:1010/tms/dashboard', {
                method: 'GET',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())
                .then(data => {
                    console.log(data);
                    document.getElementById('trainees-count').textContent = data.trainees;
                    document.getElementById('trainers-count').textContent = data.trainers;
                    document.getElementById('trainings-count').textContent = data.trainings;
                    document.getElementById('courses-count').textContent = data.courses;
                    document.getElementById('sessions-count').textContent = data.sessions;
                    document.getElementById('feedbacks-count').textContent = data.feedbacks;
                })
                .catch(error => console.error('Error fetching dashboard stats:', error));

            // Fetch recent activities
            fetch('http://localhost:1010/tms/dashboard/recent', {
                method: 'GET',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())
                .then(data => {
                    // Populate recent trainings
                    const trainingsList = document.getElementById('recent-trainings');
                    data.trainings.forEach(training => {
                        const li = document.createElement('li');
                        li.className = 'activity-item';
                        li.innerHTML = `
                            <div class="activity-icon">
                                <i class="fas fa-graduation-cap"></i>
                            </div>
                            <div class="activity-content">
                                <h4>${training.title}</h4>
                                <p>${training.description || 'No description'}</p>
                            </div>
                            <span class="activity-time">${formatDate(training.startDate)}</span>
                        `;
                        trainingsList.appendChild(li);
                    });

                    // Populate recent courses
                    const coursesList = document.getElementById('recent-courses');
                    data.courses.forEach(course => {
                        const li = document.createElement('li');
                        li.className = 'activity-item';
                        li.innerHTML = `
                            <div class="activity-icon">
                                <i class="fas fa-book"></i>
                            </div>
                            <div class="activity-content">
                                <h4>${course.title}</h4>
                                <p>${course.description || 'No description'}</p>
                            </div>
                            <span class="activity-time">${formatDate(course.createdAt)}</span>
                        `;
                        coursesList.appendChild(li);
                    });

                    // Populate recent sessions
                    const sessionsList = document.getElementById('recent-sessions');
                    data.sessions.forEach(session => {
                        const li = document.createElement('li');
                        li.className = 'activity-item';
                        li.innerHTML = `
                            <div class="activity-icon">
                                <i class="fas fa-calendar-alt"></i>
                            </div>
                            <div class="activity-content">
                                <h4>${session.trainingCourseId.course.title}</h4>                            
                                <p>${session.startTime} - ${session.endTime}</p>
                            </div>
                            <span class="activity-time">${formatDate(session.sessionDate)}</span>
                        `;
                        sessionsList.appendChild(li);
                    });

                    // Populate recent users
                    const usersList = document.getElementById('recent-users');
                    data.users.forEach(user => {
                        const li = document.createElement('li');
                        li.className = 'activity-item';
                        li.innerHTML = `
                            <div class="activity-icon">
                                <i class="fas fa-user"></i>
                            </div>
                            <div class="activity-content">
                                <h4>${user.firstName} ${user.lastName}</h4>
                                <p>${user.email}</p>
                            </div>
                            <span class="activity-time">${formatDate(user.createdAt)}</span>
                        `;
                        usersList.appendChild(li);
                    });

                    // Populate recent feedbacks
                    const feedbacksList = document.getElementById('recent-feedbacks');
                    data.feedbacks.forEach(feedback => {
                        const li = document.createElement('li');
                        li.className = 'activity-item';
                        li.innerHTML = `
                            <div class="activity-icon">
                                <i class="fas fa-comment"></i>
                            </div>
                            <div class="activity-content">
                                <h4>Rating: ${feedback.rating}/5</h4>
                                <p>${feedback.comment || 'No comment'}</p>
                            </div>
                            <span class="activity-time">${formatDate(feedback.submittedAt)}</span>
                        `;
                        feedbacksList.appendChild(li);
                    });
                })
                .catch(error => console.error('Error fetching recent activities:', error));

            // Helper function to format date
            function formatDate(dateString) {
                if (!dateString) return '';
                const date = new Date(dateString);
                return date.toLocaleDateString();
            }
        });