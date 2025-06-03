document.addEventListener('DOMContentLoaded', function() {
    // Show loading state
    showLoading('Loading dashboard data...');

    // Check if we're running locally or on the server
    const isLocal = window.location.protocol === 'file:';
    
    // Fetch dashboard statistics
    fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.DASHBOARD}`, {
        method: 'GET',
        headers: API_CONFIG.DEFAULT_HEADERS
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('Dashboard stats:', data);
        document.getElementById('trainees-count').textContent = data.trainees || 0;
        document.getElementById('trainers-count').textContent = data.trainers || 0;
        document.getElementById('trainings-count').textContent = data.trainings || 0;
        document.getElementById('courses-count').textContent = data.courses || 0;
        document.getElementById('sessions-count').textContent = data.sessions || 0;
        document.getElementById('feedbacks-count').textContent = data.feedbacks || 0;
        document.getElementById('totalUsers').textContent = data.totalUsers || 0;
        document.getElementById('totalTrainings').textContent = data.totalTrainings || 0;
        document.getElementById('totalSessions').textContent = data.totalSessions || 0;
        document.getElementById('totalEnrollments').textContent = data.totalEnrollments || 0;
    })
    .catch(error => {
        console.error('Error fetching dashboard statistics:', error);
        showError('Failed to load dashboard statistics. Please try again later.');
    });

    // Fetch recent activities
    fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.DASHBOARD}/recent`, {
        method: 'GET',
        headers: API_CONFIG.DEFAULT_HEADERS
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('Recent activities:', data);
        
        const recentActivitiesList = document.getElementById('recentActivities');
        recentActivitiesList.innerHTML = ''; // Clear existing content
        
        if (data && data.length > 0) {
            data.forEach(activity => {
                const li = document.createElement('li');
                li.textContent = `${activity.description} - ${formatDate(activity.timestamp)}`;
                recentActivitiesList.appendChild(li);
            });
        } else {
            const li = document.createElement('li');
            li.textContent = 'No recent activities';
            recentActivitiesList.appendChild(li);
        }
    })
    .catch(error => {
        console.error('Error fetching recent activities:', error);
        showError('Failed to load recent activities. Please try again later.');
    })
    .finally(() => {
        // Hide loading state
        hideLoading();
    });

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
            return 'Invalid Date';
        }
    }

    // Helper function to show error messages
    function showError(message) {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'alert alert-danger';
        errorDiv.textContent = message;
        document.querySelector('.main-content').prepend(errorDiv);
        
        // Remove error message after 5 seconds
        setTimeout(() => {
            errorDiv.remove();
        }, 5000);
    }

    // Helper function to show loading state
    function showLoading(message) {
        const loadingDiv = document.createElement('div');
        loadingDiv.id = 'loading-message';
        loadingDiv.className = 'alert alert-info';
        loadingDiv.textContent = message;
        document.querySelector('.main-content').prepend(loadingDiv);
    }

    // Helper function to hide loading state
    function hideLoading() {
        const loadingDiv = document.getElementById('loading-message');
        if (loadingDiv) {
            loadingDiv.remove();
        }
    }
});