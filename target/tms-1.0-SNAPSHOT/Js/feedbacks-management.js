document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const feedbacksTable = document.getElementById('feedbacks-table');
    const feedbackSearch = document.getElementById('feedback-search');
    const sessionFilter = document.getElementById('session-filter');
    const ratingFilter = document.getElementById('rating-filter');
    const viewModal = document.getElementById('view-modal');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageIndicator = document.getElementById('page-indicator');
    const paginationInfo = document.getElementById('pagination-info');

    // State
    let currentPage = 1;
    const feedbacksPerPage = 10;
    let totalFeedbacks = 0;
    let allFeedbacks = [];
    let filteredFeedbacks = [];
    let allSessions = [];

    // Initialize
    fetchSessions();
    fetchFeedbacks();
    setupEventListeners();

    function setupEventListeners() {
        feedbackSearch.addEventListener('input', filterFeedbacks);
        sessionFilter.addEventListener('change', filterFeedbacks);
        ratingFilter.addEventListener('change', filterFeedbacks);
        
        Array.from(document.querySelectorAll('.close-modal')).forEach(btn => {
            btn.addEventListener('click', closeModal);
        });
        
        prevPageBtn.addEventListener('click', goToPrevPage);
        nextPageBtn.addEventListener('click', goToNextPage);
        
        window.addEventListener('click', function(e) {
            if (e.target === viewModal) {
                closeModal();
            }
        });
    }

    function fetchSessions() {
        fetch(`${API_CONFIG.BASE_URL}/tms/sessions`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(sessions => {
                allSessions = sessions;
                populateSessionFilter();
            })
            .catch(error => {
                console.error('Error fetching sessions:', error);
                showToast('Failed to load sessions. Please try again.', 'error');
            });
    }

    function fetchFeedbacks() {
        fetch(`${API_CONFIG.BASE_URL}/tms/feedbacks`)
            .then(response => response.json())
            .then(feedbacks => {
                allFeedbacks = feedbacks;
                filterFeedbacks();
            })
            .catch(error => {
                console.error('Error fetching feedbacks:', error);
                showToast('Failed to load feedbacks. Please try again.', 'error');
            });
    }

    function populateSessionFilter() {
        sessionFilter.innerHTML = '<option value="">All Sessions</option>';
        allSessions.forEach(session => {
            const option = document.createElement('option');
            option.value = session.id;
            option.textContent = session.trainingCourseId && session.trainingCourseId.training && session.trainingCourseId.course
                ? `${session.trainingCourseId.training.title} - ${session.trainingCourseId.course.title}`
                : `Session on ${session.sessionDate || 'N/A'}`;
            sessionFilter.appendChild(option);
        });
    }

    function filterFeedbacks() {
        const searchTerm = feedbackSearch.value.toLowerCase();
        const sessionFilterValue = sessionFilter.value;
        const ratingFilterValue = ratingFilter.value;

        filteredFeedbacks = allFeedbacks.filter(feedback => {
            const matchesSearch =
                feedback.comment.toLowerCase().includes(searchTerm) ||
                feedback.trainee.firstName.toLowerCase().includes(searchTerm) ||
                feedback.trainee.lastName.toLowerCase().includes(searchTerm);

            const matchesSession = sessionFilterValue === '' || (feedback.session && feedback.session.id.toString() === sessionFilterValue);
            const matchesRating = ratingFilterValue === '' || feedback.rating.toString() === ratingFilterValue;

            return matchesSearch && matchesSession && matchesRating;
        });

        totalFeedbacks = filteredFeedbacks.length;
        currentPage = 1;
        renderFeedbacksTable();
        updatePagination();
    }

    function renderFeedbacksTable() {
        const tbody = feedbacksTable.querySelector('tbody');
        if (!tbody) {
            console.error('No <tbody> found inside feedbacks table.');
            return;
        }
        tbody.innerHTML = '';

        const startIndex = (currentPage - 1) * feedbacksPerPage;
        const endIndex = Math.min(startIndex + feedbacksPerPage, filteredFeedbacks.length);
        const feedbacksToDisplay = filteredFeedbacks.slice(startIndex, endIndex);

        if (feedbacksToDisplay.length === 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td colspan="6" class="no-results">No feedbacks found matching your criteria</td>`;
            tbody.appendChild(tr);
            return;
        }

        feedbacksToDisplay.forEach(feedback => {
            const tr = document.createElement('tr');

            const stars = '★'.repeat(feedback.rating) + '☆'.repeat(5 - feedback.rating);
            const session = feedback.session;
            const sessionName = session && session.trainingCourseId && session.trainingCourseId.training && session.trainingCourseId.course
                ? `${session.trainingCourseId.training.title} - ${session.trainingCourseId.course.title}`
                : `${session?.sessionDate || 'N/A'}`;

            tr.innerHTML = `
                <td>${feedback.trainee.firstName} ${feedback.trainee.lastName}</td>
                <td>${sessionName}</td>
                <td><span class="rating-stars">${stars}</span></td>
                <td><div class="comment-preview" title="${escapeHtml(feedback.comment)}">${escapeHtml(feedback.comment)}</div></td>
                <td>${formatDate(feedback.submittedAt)}</td>
                <td class="actions">
                    <button class="action-btn view-btn" data-id="${feedback.id}" title="View">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="action-btn delete-btn" data-id="${feedback.id}" title="Delete">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            `;

            tbody.appendChild(tr);
        });

        // Add event listeners to action buttons after rendering
        tbody.querySelectorAll('.view-btn').forEach(btn => {
            btn.addEventListener('click', () => viewFeedback(btn.dataset.id));
        });

        tbody.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => confirmDeleteFeedback(btn.dataset.id));
        });
    }

    function viewFeedback(feedbackId) {
        const feedback = allFeedbacks.find(f => f.id.toString() === feedbackId);
        if (!feedback) return;

        const session = feedback.session;
        const sessionName = session && session.trainingCourseId && session.trainingCourseId.training && session.trainingCourseId.course
            ? `${session.trainingCourseId.training.title} - ${session.trainingCourseId.course.title}`
            : `${session?.sessionDate || 'N/A'} @ ${session?.startTime || ''}`;

        const stars = '★'.repeat(feedback.rating) + '☆'.repeat(5 - feedback.rating);

        document.getElementById('detail-trainee').textContent =
            `${feedback.trainee.firstName} ${feedback.trainee.lastName}`;
        document.getElementById('detail-session').textContent = sessionName;
        document.getElementById('detail-rating').innerHTML =
            `<span class="rating-stars">${stars} (${feedback.rating}/5)</span>`;
        document.getElementById('detail-date').textContent = formatDateTime(feedback.submittedAt);
        document.getElementById('detail-comment').textContent = feedback.comment;

        viewModal.classList.add('active');
    }

    function confirmDeleteFeedback(feedbackId) {
        const feedback = allFeedbacks.find(f => f.id.toString() === feedbackId);
        if (!feedback) return;

        if (confirm(`Are you sure you want to delete feedback from ${feedback.trainee.firstName} ${feedback.trainee.lastName}?`)) {
            deleteFeedback(feedbackId);
        }
    }

    function deleteFeedback(feedbackId) {
        fetch(`${API_CONFIG.BASE_URL}/tms/feedbacks/${feedbackId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete feedback');
            }
            return response.json();
        })
        .then(() => {
            showToast('Feedback deleted successfully', 'success');
            fetchFeedbacks(); // Refresh the feedback list
        })
        .catch(error => {
            console.error('Error deleting feedback:', error);
            showToast('Failed to delete feedback', 'error');
        });
    }

    function updatePagination() {
        const totalPages = Math.ceil(totalFeedbacks / feedbacksPerPage);

        pageIndicator.textContent = currentPage;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;

        const startItem = (currentPage - 1) * feedbacksPerPage + 1;
        const endItem = Math.min(currentPage * feedbacksPerPage, totalFeedbacks);

        paginationInfo.textContent = totalFeedbacks === 0 ? 'No feedbacks found' :
            `Showing ${startItem}-${endItem} of ${totalFeedbacks} feedbacks`;
    }

    function goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderFeedbacksTable();
            updatePagination();
        }
    }

    function goToNextPage() {
        const totalPages = Math.ceil(totalFeedbacks / feedbacksPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderFeedbacksTable();
            updatePagination();
        }
    }

    function closeModal() {
        viewModal.classList.remove('active');
    }

    function formatDate(dateString) {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }

    function formatDateTime(dateString) {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function showToast(message, type = 'info') {
        // Implement your toast notification system or use an existing one
        console.log(`${type.toUpperCase()}: ${message}`);
        alert(`${type.toUpperCase()}: ${message}`); // Simple fallback
    }

    // Helper to escape HTML entities in comments (to avoid HTML injection)
    function escapeHtml(text) {
        if (!text) return '';
        return text.replace(/[&<>"']/g, function(match) {
            const escapeMap = {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#39;'
            };
            return escapeMap[match];
        });
    }

function showToast(message, type = 'info') {
    const container = document.querySelector('.toast-container') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    // Icons for different toast types (using Font Awesome classes)
    const icons = {
        success: 'fas fa-check-circle',
        error: 'fas fa-exclamation-circle',
        info: 'fas fa-info-circle',
        warning: 'fas fa-exclamation-triangle'
    };
    
    toast.innerHTML = `
        <i class="toast-icon ${icons[type] || icons.info}"></i>
        <span class="toast-message">${message}</span>
        <span class="close-toast">&times;</span>
    `;
    
    container.appendChild(toast);
    
    // Show the toast
    setTimeout(() => toast.classList.add('show'), 100);
    
    // Close button functionality
    toast.querySelector('.close-toast').addEventListener('click', () => {
        removeToast(toast);
    });
    
    // Auto-remove after 5 seconds
    setTimeout(() => removeToast(toast), 5000);
}

function createToastContainer() {
    const container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
    return container;
}

function removeToast(toast) {
    toast.classList.remove('show');
    setTimeout(() => toast.remove(), 300);
}

});
