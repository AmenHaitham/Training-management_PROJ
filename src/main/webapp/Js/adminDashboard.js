document.addEventListener('DOMContentLoaded', function() {
    // Show loading state
    showLoading('Loading dashboard data...');

    // Debug information
    console.log('Current URL:', window.location.href);
    console.log('API Base URL:', API_CONFIG.BASE_URL);
    console.log('Dashboard URL:', `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.DASHBOARD}`);

    // Fetch dashboard statistics
    fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.DASHBOARD}`, {
        method: 'GET',
        headers: API_CONFIG.DEFAULT_HEADERS
    })
    .then(response => {
        console.log('Dashboard response status:', response.status);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('Dashboard stats:', data);
        // Update statistics
        updateStat('trainees-count', data.trainees);
        updateStat('trainers-count', data.trainers);
        updateStat('trainings-count', data.trainings);
        updateStat('courses-count', data.courses);
        updateStat('sessions-count', data.sessions);
        updateStat('feedbacks-count', data.feedbacks);
    })
    .catch(error => {
        console.error('Error fetching dashboard statistics:', error);
        showError('Failed to load dashboard statistics. Please ensure the server is running at http://localhost:8080');
    });

    // Fetch recent activities
    fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.DASHBOARD}/recent`, {
        method: 'GET',
        headers: API_CONFIG.DEFAULT_HEADERS
    })
    .then(response => {
        console.log('Recent activities response status:', response.status);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('Recent activities:', data);
        
        // Update recent trainings
        updateActivityList('recent-trainings', data.trainings, 'No recent trainings');
        
        // Update recent courses
        updateActivityList('recent-courses', data.courses, 'No recent courses');
        
        // Update recent sessions
        updateActivityList('recent-sessions', data.sessions, 'No recent sessions');
        
        // Update recent users
        updateActivityList('recent-users', data.users, 'No recent users');
        
        // Update recent feedbacks
        updateActivityList('recent-feedbacks', data.feedbacks, 'No recent feedbacks');
    })
    .catch(error => {
        console.error('Error fetching recent activities:', error);
        showError('Failed to load recent activities. Please ensure the server is running at http://localhost:8080');
    })
    .finally(() => {
        // Hide loading state
        hideLoading();
    });

    // Helper function to update a statistic
    function updateStat(elementId, value) {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = value || 0;
        } else {
            console.error(`Stat element not found: ${elementId}`);
        }
    }

    // Helper function to update activity list
    function updateActivityList(listId, items, emptyMessage) {
        const list = document.getElementById(listId);
        if (!list) {
            console.error(`Activity list element not found: ${listId}`);
            return;
        }
        
        list.innerHTML = ''; // Clear existing content
        
        if (items && items.length > 0) {
            items.forEach(item => {
                const li = document.createElement('li');
                li.className = 'activity-item';
                
                // Create activity icon
                const iconDiv = document.createElement('div');
                iconDiv.className = 'activity-icon';
                const icon = document.createElement('i');
                icon.className = getIconClass(listId);
                iconDiv.appendChild(icon);
                
                // Create activity content
                const contentDiv = document.createElement('div');
                contentDiv.className = 'activity-content';
                const title = document.createElement('h4');
                title.textContent = getItemTitle(item, listId);
                const description = document.createElement('p');
                description.textContent = getItemDescription(item, listId);
                contentDiv.appendChild(title);
                contentDiv.appendChild(description);
                
                // Create activity time
                const timeSpan = document.createElement('span');
                timeSpan.className = 'activity-time';
                timeSpan.textContent = formatDate(getItemDate(item, listId));
                
                // Append all elements
                li.appendChild(iconDiv);
                li.appendChild(contentDiv);
                li.appendChild(timeSpan);
                list.appendChild(li);
            });
        } else {
            const li = document.createElement('li');
            li.className = 'no-data';
            li.textContent = emptyMessage;
            list.appendChild(li);
        }
    }

    // Helper function to get icon class based on list ID
    function getIconClass(listId) {
        switch (listId) {
            case 'recent-trainings': return 'fas fa-graduation-cap';
            case 'recent-courses': return 'fas fa-book';
            case 'recent-sessions': return 'fas fa-calendar-alt';
            case 'recent-users': return 'fas fa-user';
            case 'recent-feedbacks': return 'fas fa-comment';
            default: return 'fas fa-info-circle';
        }
    }

    // Helper function to get item title based on list ID
    function getItemTitle(item, listId) {
        switch (listId) {
            case 'recent-trainings': return item.title;
            case 'recent-courses': return item.title;
            case 'recent-sessions': return item.trainingCourseId?.course?.title || 'Untitled Course';
            case 'recent-users': return `${item.firstName} ${item.lastName}`;
            case 'recent-feedbacks': return `Rating: ${item.rating}/5`;
            default: return 'Unknown';
        }
    }

    // Helper function to get item description based on list ID
    function getItemDescription(item, listId) {
        switch (listId) {
            case 'recent-trainings': return item.description || 'No description';
            case 'recent-courses': return item.description || 'No description';
            case 'recent-sessions': return `${item.startTime} - ${item.endTime}`;
            case 'recent-users': return item.email;
            case 'recent-feedbacks': return item.comment || 'No comment';
            default: return '';
        }
    }

    // Helper function to get item date based on list ID
    function getItemDate(item, listId) {
        switch (listId) {
            case 'recent-trainings': return item.startDate;
            case 'recent-courses': return item.createdAt;
            case 'recent-sessions': return item.sessionDate;
            case 'recent-users': return item.createdAt;
            case 'recent-feedbacks': return item.submittedAt;
            default: return null;
        }
    }

    // Helper function to format date
    function formatDate(dateString) {
        if (!dateString) return 'N/A';
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('en-US', { 
                year: 'numeric', 
                month: 'short', 
                day: 'numeric' 
            });
        } catch (e) {
            console.error('Error formatting date:', e);
            return 'Invalid Date';
        }
    }

    // Helper function to show error messages
    function showError(message) {
        const mainContent = document.querySelector('.main-content');
        if (!mainContent) {
            console.error('Main content element not found');
            return;
        }

        const errorDiv = document.createElement('div');
        errorDiv.className = 'alert alert-danger';
        errorDiv.textContent = message;
        mainContent.prepend(errorDiv);
        
        // Remove error message after 5 seconds
        setTimeout(() => {
            errorDiv.remove();
        }, 5000);
    }

    // Helper function to show loading state
    function showLoading(message) {
        const mainContent = document.querySelector('.main-content');
        if (!mainContent) {
            console.error('Main content element not found');
            return;
        }

        const loadingDiv = document.createElement('div');
        loadingDiv.id = 'loading-message';
        loadingDiv.className = 'alert alert-info';
        loadingDiv.textContent = message;
        mainContent.prepend(loadingDiv);
    }

    // Helper function to hide loading state
    function hideLoading() {
        const loadingDiv = document.getElementById('loading-message');
        if (loadingDiv) {
            loadingDiv.remove();
        }
    }
});