document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const trainingCoursesTable = document.getElementById('training-courses-table');
    const trainingCourseSearch = document.getElementById('training-course-search');
    const trainingFilter = document.getElementById('training-filter');
    const courseFilter = document.getElementById('course-filter');
    const statusFilter = document.getElementById('status-filter');
    const addTrainingCourseBtn = document.getElementById('add-training-course-btn');
    const trainingCourseModal = document.getElementById('training-course-modal');
    const confirmModal = document.getElementById('confirm-modal');
    const trainingCourseForm = document.getElementById('training-course-form');
    const saveTrainingCourseBtn = document.getElementById('save-training-course');
    const confirmActionBtn = document.getElementById('confirm-action');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageIndicator = document.getElementById('page-indicator');
    const paginationInfo = document.getElementById('pagination-info');
    const trainingSelect = document.getElementById('training');
    const courseSelect = document.getElementById('course');

    // State variables
    let currentPage = 1;
    const trainingCoursesPerPage = 10;
    let totalTrainingCourses = 0;
    let allTrainingCourses = [];
    let filteredTrainingCourses = [];
    let allTrainings = [];
    let allCourses = [];
    let currentAction = { type: '', trainingCourseId: '' };

    // Initialize the page
    fetchTrainingCourses();
    fetchTrainings();
    fetchCourses();
    setupEventListeners();

    function setupEventListeners() {
        // Search and filter events
        trainingCourseSearch.addEventListener('input', filterTrainingCourses);
        trainingFilter.addEventListener('change', filterTrainingCourses);
        courseFilter.addEventListener('change', filterTrainingCourses);
        statusFilter.addEventListener('change', filterTrainingCourses);

        // Modal events
        addTrainingCourseBtn.addEventListener('click', () => openTrainingCourseModal('add'));
        Array.from(document.querySelectorAll('.close-modal')).forEach(btn => {
            btn.addEventListener('click', closeModals);
        });

        // Form submission
        saveTrainingCourseBtn.addEventListener('click', saveTrainingCourse);
        trainingCourseForm.addEventListener('submit', function(e) {
            e.preventDefault();
            saveTrainingCourse();
        });

        // Pagination
        prevPageBtn.addEventListener('click', goToPrevPage);
        nextPageBtn.addEventListener('click', goToNextPage);

        // Confirm action
        confirmActionBtn.addEventListener('click', function() {
            const { type, trainingCourseId } = currentAction;
            
            if (type === 'delete') {
                deleteTrainingCourse(trainingCourseId);
            }
            
            closeModals();
        });

        // Close modals when clicking outside
        window.addEventListener('click', function(e) {
            if (e.target === trainingCourseModal) {
                closeModals();
            }
            if (e.target === confirmModal) {
                closeModals();
            }
        });
    }

    function fetchTrainingCourses() {
        fetch('http://localhost:1010/tms/training-courses')
            .then(response => response.json())
            .then(data => {
                allTrainingCourses = data;
                totalTrainingCourses = data.length;
                filterTrainingCourses();
            })
            .catch(error => {
                console.error('Error fetching training courses:', error);
                showToast('Failed to load training courses. Please try again.', 'error');
            });
    }

    function fetchTrainings() {
        fetch('http://localhost:1010/tms/trainings')
            .then(response => response.json())
            .then(data => {
                allTrainings = data;
                populateTrainingSelects();
            })
            .catch(error => {
                console.error('Error fetching trainings:', error);
                showToast('Failed to load trainings. Please try again.', 'error');
            });
    }

    function fetchCourses() {
        fetch('http://localhost:1010/tms/courses')
            .then(response => response.json())
            .then(data => {
                allCourses = data;
                populateCourseSelects();
            })
            .catch(error => {
                console.error('Error fetching courses:', error);
                showToast('Failed to load courses. Please try again.', 'error');
            });
    }

    function populateTrainingSelects() {
        // Populate filter dropdown
        trainingFilter.innerHTML = '<option value="">All Trainings</option>';
        allTrainings.forEach(training => {
            const option = document.createElement('option');
            option.value = training.id;
            option.textContent = training.title;
            trainingFilter.appendChild(option);
        });

        // Populate modal select
        trainingSelect.innerHTML = '<option value="">Select Training</option>';
        allTrainings.forEach(training => {
            const option = document.createElement('option');
            option.value = training.id;
            option.textContent = training.title;
            trainingSelect.appendChild(option);
        });
    }

    function populateCourseSelects() {
        // Populate filter dropdown
        courseFilter.innerHTML = '<option value="">All Courses</option>';
        allCourses.forEach(course => {
            const option = document.createElement('option');
            option.value = course.id;
            option.textContent = course.title;
            courseFilter.appendChild(option);
        });

        // Populate modal select
        courseSelect.innerHTML = '<option value="">Select Course</option>';
        allCourses.forEach(course => {
            const option = document.createElement('option');
            option.value = course.id;
            option.textContent = course.title;
            courseSelect.appendChild(option);
        });
    }

    function filterTrainingCourses() {
        const searchTerm = trainingCourseSearch.value.toLowerCase();
        const trainingFilterValue = trainingFilter.value;
        const courseFilterValue = courseFilter.value;
        const statusFilterValue = statusFilter.value;

        filteredTrainingCourses = allTrainingCourses.filter(tc => {
            const matchesSearch = 
                tc.training.title.toLowerCase().includes(searchTerm) ||
                tc.course.title.toLowerCase().includes(searchTerm);

            const matchesTraining = trainingFilterValue === '' || tc.training.id == trainingFilterValue;
            const matchesCourse = courseFilterValue === '' || tc.course.id == courseFilterValue;
            const matchesStatus = statusFilterValue === '' || tc.status === statusFilterValue;

            return matchesSearch && matchesTraining && matchesCourse && matchesStatus;
        });

        totalTrainingCourses = filteredTrainingCourses.length;
        currentPage = 1; // Reset to first page when filtering
        renderTrainingCoursesTable();
        updatePagination();
    }

    function renderTrainingCoursesTable() {
        const tbody = trainingCoursesTable.querySelector('tbody');
        tbody.innerHTML = '';

        const startIndex = (currentPage - 1) * trainingCoursesPerPage;
        const endIndex = Math.min(startIndex + trainingCoursesPerPage, filteredTrainingCourses.length);
        const trainingCoursesToDisplay = filteredTrainingCourses.slice(startIndex, endIndex);

        if (trainingCoursesToDisplay.length === 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td colspan="7" class="no-results">No training courses found matching your criteria</td>`;
            tbody.appendChild(tr);
            return;
        }

        trainingCoursesToDisplay.forEach(tc => {
            const tr = document.createElement('tr');
            
            const statusClass = `status-${tc.status.toLowerCase()}`;
            const statusText = getStatusLabel(tc.status);
            
            // Calculate progress percentage
            const progressPercentage = tc.totalSessions > 0 ? 
                Math.round((tc.completedSessions / tc.totalSessions) * 100) : 0;
            
            tr.innerHTML = `
                <td>${tc.id}</td>
                <td>${tc.training.title}</td>
                <td>${tc.course.title}</td>
                <td>
                    <div class="session-progress">
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${progressPercentage}%"></div>
                        </div>
                        <div class="session-stats">
                            <span>${tc.completedSessions}/${tc.totalSessions} completed</span>
                            <span>${tc.cancelledSessions} cancelled</span>
                        </div>
                    </div>
                </td>
                <td>${formatDate(tc.startDate)} - ${formatDate(tc.endDate)}</td>
                <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                <td class="actions">
                    <button class="action-btn edit-btn" data-id="${tc.id}" title="Edit">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="action-btn status-btn" data-id="${tc.id}" title="Change Status">
                        <i class="fas fa-exchange-alt"></i>
                    </button>
                    <button class="action-btn delete-btn" data-id="${tc.id}" title="Delete">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            `;
            
            tbody.appendChild(tr);
        });

        // Add event listeners to action buttons
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', () => openTrainingCourseModal('edit', btn.dataset.id));
        });

        document.querySelectorAll('.status-btn').forEach(btn => {
            btn.addEventListener('click', () => openStatusModal(btn.dataset.id));
        });

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => confirmDeleteTrainingCourse(btn.dataset.id));
        });
    }

    function updatePagination() {
        const totalPages = Math.ceil(totalTrainingCourses / trainingCoursesPerPage);
        
        pageIndicator.textContent = currentPage;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;
        
        const startItem = (currentPage - 1) * trainingCoursesPerPage + 1;
        const endItem = Math.min(currentPage * trainingCoursesPerPage, totalTrainingCourses);
        
        paginationInfo.textContent = totalTrainingCourses === 0 ? 'No training courses found' : 
            `Showing ${startItem}-${endItem} of ${totalTrainingCourses} training courses`;
    }

    function goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderTrainingCoursesTable();
            updatePagination();
        }
    }

    function goToNextPage() {
        const totalPages = Math.ceil(totalTrainingCourses / trainingCoursesPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderTrainingCoursesTable();
            updatePagination();
        }
    }

    function openTrainingCourseModal(mode, trainingCourseId = null) {
        document.getElementById('modal-title').textContent = 
            mode === 'add' ? 'Add New Training Course' : 'Edit Training Course';
        
        // Reset form
        trainingCourseForm.reset();
        document.getElementById('training-course-id').value = '';
        
        if (mode === 'edit' && trainingCourseId) {
            const tc = allTrainingCourses.find(t => t.id == trainingCourseId);
            if (tc) {
                document.getElementById('training-course-id').value = tc.id;
                document.getElementById('training').value = tc.training.id;
                document.getElementById('course').value = tc.course.id;
                document.getElementById('total-sessions').value = tc.totalSessions;
                document.getElementById('status').value = tc.status;
                document.getElementById('start-date').value = formatDateForInput(tc.startDate);
                document.getElementById('end-date').value = formatDateForInput(tc.endDate);
            }
        }
        
        trainingCourseModal.classList.add('active');
    }

    function openStatusModal(trainingCourseId) {
        const tc = allTrainingCourses.find(t => t.id == trainingCourseId);
        if (!tc) return;
        
        // Create status options dynamically
        const statusOptions = Object.values(TrainingCourseStatus).map(status => 
            `<option value="${status}" ${tc.status === status ? 'selected' : ''}>
                ${getStatusLabel(status)}
            </option>`
        ).join('');
        
        document.getElementById('confirm-title').textContent = 'Change Training Course Status';
        document.getElementById('confirm-message').innerHTML = `
            <p>Current status: <span class="status-badge status-${tc.status.toLowerCase()}">
                ${getStatusLabel(tc.status)}
            </span></p>
            <div class="form-group">
                <label for="new-status">New Status:</label>
                <select id="new-status" class="full-width">
                    ${statusOptions}
                </select>
            </div>
        `;
        
        currentAction = { type: 'status', trainingCourseId };
        confirmModal.classList.add('active');
    }

    function confirmDeleteTrainingCourse(trainingCourseId) {
        const tc = allTrainingCourses.find(t => t.id == trainingCourseId);
        if (!tc) return;
        
        currentAction = { type: 'delete', trainingCourseId };
        document.getElementById('confirm-title').textContent = 'Delete Training Course?';
        document.getElementById('confirm-message').textContent = 
            `Are you sure you want to permanently delete "${tc.training.title} - ${tc.course.title}"? This action cannot be undone.`;
        
        confirmModal.classList.add('active');
    }

    function closeModals() {
        trainingCourseModal.classList.remove('active');
        confirmModal.classList.remove('active');
    }

    function saveTrainingCourse() {
        const trainingCourseId = document.getElementById('training-course-id').value;
        const isEditMode = trainingCourseId !== '';
        
        const trainingCourseData = {
            trainingId: document.getElementById('training').value,
            courseId: document.getElementById('course').value,
            totalSessions: parseInt(document.getElementById('total-sessions').value),
            status: document.getElementById('status').value,
            startDate: document.getElementById('start-date').value,
            endDate: document.getElementById('end-date').value
        };
        
        // Validate required fields
        if (!trainingCourseData.trainingId || !trainingCourseData.courseId || 
            !trainingCourseData.totalSessions || !trainingCourseData.status || 
            !trainingCourseData.startDate || !trainingCourseData.endDate) {
            showToast('Please fill in all required fields', 'error');
            return;
        }
        
        // Validate dates
        const startDate = new Date(trainingCourseData.startDate);
        const endDate = new Date(trainingCourseData.endDate);
        
        if (startDate > endDate) {
            showToast('End date must be after start date', 'error');
            return;
        }
        
        // Validate sessions
        if (trainingCourseData.totalSessions <= 0) {
            showToast('Total sessions must be greater than 0', 'error');
            return;
        }
        
        const url = isEditMode ? `/tms/api/training-courses/${trainingCourseId}` : '/tms/api/training-courses';
        const method = isEditMode ? 'PUT' : 'POST';
        
        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(trainingCourseData)
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw err; });
            }
            return response.json();
        })
        .then(data => {
            showToast(`Training course ${isEditMode ? 'updated' : 'created'} successfully`, 'success');
            closeModals();
            fetchTrainingCourses(); // Refresh the training course list
        })
        .catch(error => {
            console.error('Error saving training course:', error);
            showToast(error.message || 'Failed to save training course', 'error');
        });
    }

    function deleteTrainingCourse(trainingCourseId) {
        fetch(`/tms/api/training-courses/${trainingCourseId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete training course');
            }
            return response.json();
        })
        .then(data => {
            showToast('Training course deleted successfully', 'success');
            fetchTrainingCourses(); // Refresh the training course list
        })
        .catch(error => {
            console.error('Error deleting training course:', error);
            showToast('Failed to delete training course', 'error');
        });
    }

    // TrainingCourse status enum for frontend
    const TrainingCourseStatus = {
        COMING: 'COMING',
        LIVE: 'LIVE',
        COMPLETED: 'COMPLETED',
        CANCELLED: 'CANCELLED'
    };

    function getStatusLabel(status) {
        const statusLabels = {
            [TrainingCourseStatus.COMING]: 'Coming',
            [TrainingCourseStatus.LIVE]: 'Live',
            [TrainingCourseStatus.COMPLETED]: 'Completed',
            [TrainingCourseStatus.CANCELLED]: 'Cancelled'
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

    function showToast(message, type = 'info') {
        // Implement your toast notification system or use an existing one
        console.log(`${type.toUpperCase()}: ${message}`);
        alert(`${type.toUpperCase()}: ${message}`); // Simple fallback
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