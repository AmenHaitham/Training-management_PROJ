document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const sessionsTable = document.getElementById('sessions-table');
    const sessionSearch = document.getElementById('session-search');
    const trainingCourseFilter = document.getElementById('training-course-filter');
    const statusFilter = document.getElementById('status-filter');
    const startDateFilter = document.getElementById('start-date-filter');
    const endDateFilter = document.getElementById('end-date-filter');
    const addSessionBtn = document.getElementById('add-session-btn');
    const sessionModal = document.getElementById('session-modal');
    const confirmModal = document.getElementById('confirm-modal');
    const sessionForm = document.getElementById('session-form');
    const saveSessionBtn = document.getElementById('save-session');
    const confirmActionBtn = document.getElementById('confirm-action');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageIndicator = document.getElementById('page-indicator');
    const paginationInfo = document.getElementById('pagination-info');
    const trainingCourseSelect = document.getElementById('training-course');

    // State variables
    let currentPage = 1;
    const sessionsPerPage = 10;
    let totalSessions = 0;
    let allSessions = [];
    let filteredSessions = [];
    let allTrainingCourses = [];
    let currentAction = { type: '', sessionId: '' };

    // Initialize the page
    fetchSessions();
    fetchTrainingCourses();
    setupEventListeners();

    function setupEventListeners() {
        // Search and filter events
        sessionSearch.addEventListener('input', filterSessions);
        trainingCourseFilter.addEventListener('change', filterSessions);
        statusFilter.addEventListener('change', filterSessions);
        startDateFilter.addEventListener('change', filterSessions);
        endDateFilter.addEventListener('change', filterSessions);

        // Modal events
        addSessionBtn.addEventListener('click', () => openSessionModal('add'));
        Array.from(document.querySelectorAll('.close-modal')).forEach(btn => {
            btn.addEventListener('click', closeModals);
        });

        // Form submission
        saveSessionBtn.addEventListener('click', saveSession);
        sessionForm.addEventListener('submit', function(e) {
            e.preventDefault();
            saveSession();
        });

        // Pagination
        prevPageBtn.addEventListener('click', goToPrevPage);
        nextPageBtn.addEventListener('click', goToNextPage);

        // Confirm action
        confirmActionBtn.addEventListener('click', function() {
            const { type, sessionId } = currentAction;
            
            if (type === 'delete') {
                deleteSession(sessionId);
            }
            
            closeModals();
        });

        // Close modals when clicking outside
        window.addEventListener('click', function(e) {
            if (e.target === sessionModal) {
                closeModals();
            }
            if (e.target === confirmModal) {
                closeModals();
            }
        });
    }

    function fetchSessions() {
        fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.SESSIONS}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                // Handle different possible response structures
                allSessions = Array.isArray(data) ? data : 
                            data.data || data.sessions || [];
                
                console.log('Fetched sessions:', allSessions); // Debug log
                
                filteredSessions = [...allSessions];
                renderSessionsTable();
                updatePagination();
            })
            .catch(error => {
                console.error('Error fetching sessions:', error);
                showToast('Failed to load sessions. Please try again.', 'error');
                allSessions = [];
                filteredSessions = [];
                renderSessionsTable();
                updatePagination();
            });
    }

    function fetchTrainingCourses() {
        fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.TRAINING_COURSES}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                // Handle different possible response structures
                allTrainingCourses = Array.isArray(data) ? data : 
                                   data.data || data.trainingCourses || [];
                
                console.log('Fetched training courses:', allTrainingCourses); // Debug log
                
                populateTrainingCourseSelects();
            })
            .catch(error => {
                console.error('Error fetching training courses:', error);
                showToast('Failed to load training courses. Please try again.', 'error');
                allTrainingCourses = [];
                populateTrainingCourseSelects();
            });
    }

    function populateTrainingCourseSelects() {
        // Populate filter dropdown
        trainingCourseFilter.innerHTML = '<option value="">All Training Courses</option>';
        allTrainingCourses.forEach(tc => {
            const option = document.createElement('option');
            option.value = tc.id;
            const training = tc.training || {};
            const course = tc.course || {};
            option.textContent = `${training.title || ''} - ${course.title || ''}`;
            trainingCourseFilter.appendChild(option);
        });

        // Populate modal select
        trainingCourseSelect.innerHTML = '<option value="">Select Training Course</option>';
        allTrainingCourses.forEach(tc => {
            const option = document.createElement('option');
            option.value = tc.id;
            const training = tc.training || {};
            const course = tc.course || {};
            option.textContent = `${training.title || ''} - ${course.title || ''}`;
            trainingCourseSelect.appendChild(option);
        });
    }

    function filterSessions() {
        const searchTerm = sessionSearch.value.toLowerCase();
        const trainingCourseFilterValue = trainingCourseFilter.value;
        const statusFilterValue = statusFilter.value;
        const startDateValue = startDateFilter.value;
        const endDateValue = endDateFilter.value;

        filteredSessions = allSessions.filter(session => {
            const matchesSearch = 
                session.trainingCourseId.training.title.toLowerCase().includes(searchTerm) ||
                session.trainingCourseId.course.title.toLowerCase().includes(searchTerm);

            const matchesTrainingCourse = trainingCourseFilterValue === '' || 
                session.trainingCourseId.id == trainingCourseFilterValue;
            const matchesStatus = statusFilterValue === '' || session.status === statusFilterValue;
            
            let matchesDate = true;
            if (startDateValue) {
                const sessionDate = new Date(session.sessionDate);
                const filterStartDate = new Date(startDateValue);
                matchesDate = matchesDate && sessionDate >= filterStartDate;
            }
            if (endDateValue) {
                const sessionDate = new Date(session.sessionDate);
                const filterEndDate = new Date(endDateValue);
                matchesDate = matchesDate && sessionDate <= filterEndDate;
            }

            return matchesSearch && matchesTrainingCourse && matchesStatus && matchesDate;
        });

        totalSessions = filteredSessions.length;
        currentPage = 1; // Reset to first page when filtering
        renderSessionsTable();
        updatePagination();
    }

    function renderSessionsTable() {
        const tbody = sessionsTable.querySelector('tbody');
        tbody.innerHTML = '';

        const startIndex = (currentPage - 1) * sessionsPerPage;
        const endIndex = Math.min(startIndex + sessionsPerPage, filteredSessions.length);
        const sessionsToDisplay = filteredSessions.slice(startIndex, endIndex);

        if (sessionsToDisplay.length === 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td colspan="7" class="no-results">No sessions found matching your criteria</td>`;
            tbody.appendChild(tr);
            return;
        }

        sessionsToDisplay.forEach(session => {
            const tr = document.createElement('tr');
            
            const statusClass = `status-${(session.status || '').toLowerCase()}`;
            const statusText = getStatusLabel(session.status);
            
            // Safely access nested properties
            const trainingCourse = session.trainingCourseId || {};
            const training = trainingCourse.training || {};
            const course = trainingCourse.course || {};
            
            tr.innerHTML = `
                <td>${session.id || ''}</td>
                <td>${training.title || ''} - ${course.title || ''}</td>
                <td>${formatDate(session.sessionDate)}</td>
                <td>${formatTime(session.startTime)} - ${formatTime(session.endTime)}</td>
                <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                <td>${formatDateTime(session.createdAt)}</td>
                <td class="actions">
                    <button class="action-btn edit-btn" data-id="${session.id}">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="action-btn delete-btn" data-id="${session.id}">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // Add event listeners to action buttons
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', () => openSessionModal('edit', btn.dataset.id));
        });

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => confirmDeleteSession(btn.dataset.id));
        });
    }

    function updatePagination() {
        const totalPages = Math.ceil(totalSessions / sessionsPerPage);
        
        pageIndicator.textContent = currentPage;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;
        
        const startItem = (currentPage - 1) * sessionsPerPage + 1;
        const endItem = Math.min(currentPage * sessionsPerPage, totalSessions);
        
        paginationInfo.textContent = totalSessions === 0 ? 'No sessions found' : 
            `Showing ${startItem}-${endItem} of ${totalSessions} sessions`;
    }

    function goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderSessionsTable();
            updatePagination();
        }
    }

    function goToNextPage() {
        const totalPages = Math.ceil(totalSessions / sessionsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderSessionsTable();
            updatePagination();
        }
    }

    function openSessionModal(mode, sessionId = null) {
        document.getElementById('modal-title').textContent = 
            mode === 'add' ? 'Add New Session' : 'Edit Session';
        
        // Reset form
        sessionForm.reset();
        document.getElementById('session-id').value = '';
        
        if (mode === 'edit' && sessionId) {
            const session = allSessions.find(s => s.id == sessionId);
            if (session) {
                document.getElementById('session-id').value = session.id;
                document.getElementById('training-course').value = session.trainingCourseId.id;
                document.getElementById('session-date').value = formatDateForInput(session.sessionDate);
                document.getElementById('start-time').value = formatTimeForInput(session.startTime);
                document.getElementById('end-time').value = formatTimeForInput(session.endTime);
                document.getElementById('status').value = session.status;
            }
        }
        
        sessionModal.classList.add('active');
    }

    function openStatusModal(sessionId) {
        const session = allSessions.find(s => s.id == sessionId);
        if (!session) return;
        
        // Create status options dynamically
        const statusOptions = Object.values(SessionStatus).map(status => 
            `<option value="${status}" ${session.status === status ? 'selected' : ''}>
                ${getStatusLabel(status)}
            </option>`
        ).join('');
        
        document.getElementById('confirm-title').textContent = 'Change Session Status';
        document.getElementById('confirm-message').innerHTML = `
            <p>Current status: <span class="status-badge status-${session.status.toLowerCase()}">
                ${getStatusLabel(session.status)}
            </span></p>
            <div class="form-group">
                <label for="new-status">New Status:</label>
                <select id="new-status" class="full-width">
                    ${statusOptions}
                </select>
            </div>
        `;
        
        currentAction = { type: 'status', sessionId };
        confirmModal.classList.add('active');
    }

    function confirmDeleteSession(sessionId) {
        const session = allSessions.find(s => s.id == sessionId);
        if (!session) return;
        
        currentAction = { type: 'delete', sessionId };
        document.getElementById('confirm-title').textContent = 'Delete Session?';
        document.getElementById('confirm-message').textContent = 
            `Are you sure you want to permanently delete this session on ${formatDate(session.sessionDate)}?`;
        
        confirmModal.classList.add('active');
    }

    function closeModals() {
        sessionModal.classList.remove('active');
        confirmModal.classList.remove('active');
    }

    function saveSession() {
        const sessionId = document.getElementById('session-id').value;
        const isEditMode = sessionId !== '';
        
        const sessionData = {
            trainingCourseId: {id: document.getElementById('training-course').value},
            sessionDate: document.getElementById('session-date').value,
            startTime: document.getElementById('start-time').value,
            endTime: document.getElementById('end-time').value,
            status: document.getElementById('status').value
        };
        
        // Validate required fields
        if (!sessionData.trainingCourseId || !sessionData.sessionDate || 
            !sessionData.startTime || !sessionData.endTime || !sessionData.status) {
            showToast('Please fill in all required fields', 'error');
            return;
        }
        
        // Validate time
        if (sessionData.startTime >= sessionData.endTime) {
            showToast('End time must be after start time', 'error');
            return;
        }
        
        const url = isEditMode ? `${API_CONFIG.BASE_URL}/tms/sessions/${sessionId}` : `${API_CONFIG.BASE_URL}/tms/sessions`;
        const method = isEditMode ? 'PUT' : 'POST';
        
        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(sessionData)
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.message || 'Failed to save session'); });
            }
            return response.json();
        })
        .then(data => {
            showToast(`Session ${isEditMode ? 'updated' : 'created'} successfully`, 'success');
            closeModals();
            fetchSessions(); // Refresh the session list
        })
        .catch(error => {
            console.error('Error saving session:', error);
            showToast(error.message || 'Failed to save session', 'error');
        });
    }

    function deleteSession(sessionId) {
        fetch(`${API_CONFIG.BASE_URL}/tms/sessions/${sessionId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.message || 'Failed to delete session'); });
            }
            return response.json();
        })
        .then(data => {
            showToast('Session deleted successfully', 'success');
            fetchSessions(); // Refresh the session list
        })
        .catch(error => {
            console.error('Error deleting session:', error);
            showToast(error.message || 'Failed to delete session', 'error');
        });
    }

    // Session status enum for frontend
    const SessionStatus = {
        COMING: 'COMING',
        COMPLETED: 'COMPLETED',
        CANCELLED: 'CANCELLED'
    };

    function getStatusLabel(status) {
        const statusLabels = {
            [SessionStatus.COMING]: 'Coming',
            [SessionStatus.COMPLETED]: 'Completed',
            [SessionStatus.CANCELLED]: 'Cancelled'
        };
        return statusLabels[status] || status;
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

    function formatDateForInput(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    function formatTime(timeString) {
        if (!timeString) return 'N/A';
        const time = new Date(`1970-01-01T${timeString}`);
        return time.toLocaleTimeString('en-US', { 
            hour: '2-digit', 
            minute: '2-digit',
            hour12: true
        });
    }

    function formatTimeForInput(timeString) {
        if (!timeString) return '';
        // Convert from "HH:MM:SS" to "HH:MM" format
        return timeString.substring(0, 5);
    }

    function formatDateTime(dateTimeString) {
        if (!dateTimeString) return 'N/A';
        const dateTime = new Date(dateTimeString);
        return dateTime.toLocaleString('en-US', { 
            year: 'numeric', 
            month: 'short', 
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
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