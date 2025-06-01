document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const coursesTable = document.getElementById('courses-table');
    const courseSearch = document.getElementById('course-search');
    const trainerFilter = document.getElementById('trainer-filter');
    const addCourseBtn = document.getElementById('add-course-btn');
    const courseModal = document.getElementById('course-modal');
    const confirmModal = document.getElementById('confirm-modal');
    const courseForm = document.getElementById('course-form');
    const saveCourseBtn = document.getElementById('save-course');
    const confirmActionBtn = document.getElementById('confirm-action');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageIndicator = document.getElementById('page-indicator');
    const paginationInfo = document.getElementById('pagination-info');
    const trainerSelect = document.getElementById('trainer');

    // State variables
    let currentPage = 1;
    const coursesPerPage = 10;
    let totalCourses = 0;
    let allCourses = [];
    let filteredCourses = [];
    let allTrainers = [];
    let currentAction = { type: '', courseId: '' };

    // Initialize the page
    fetchInitialData();
    setupEventListeners();

    function fetchInitialData() {
        showLoading(true);
        Promise.all([fetchCourses(), fetchTrainers()])
            .catch(error => {
                console.error('Initialization error:', error);
                showToast('Failed to initialize data. Please refresh the page.', 'error');
            })
            .finally(() => showLoading(false));
    }

    function setupEventListeners() {
        // Search and filter events with debounce
        courseSearch.addEventListener('input', debounce(filterCourses, 300));
        trainerFilter.addEventListener('change', filterCourses);

        // Modal events
        addCourseBtn.addEventListener('click', () => openCourseModal('add'));
        Array.from(document.querySelectorAll('.close-modal')).forEach(btn => {
            btn.addEventListener('click', closeModals);
        });

        // Form submission
        saveCourseBtn.addEventListener('click', handleSaveCourse);
        courseForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleSaveCourse();
        });

        // Pagination
        prevPageBtn.addEventListener('click', goToPrevPage);
        nextPageBtn.addEventListener('click', goToNextPage);

        // Confirm action
        confirmActionBtn.addEventListener('click', handleConfirmAction);

        // Close modals when clicking outside
        window.addEventListener('click', function(e) {
            if (e.target === courseModal || e.target === confirmModal) {
                closeModals();
            }
        });
    }

    function debounce(func, wait) {
        let timeout;
        return function() {
            const context = this, args = arguments;
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(context, args), wait);
        };
    }

    function fetchCourses() {
        return fetch('http://localhost:1010/tms/courses')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                allCourses = data;
                totalCourses = data.length;
                filterCourses();
            })
            .catch(error => {
                console.error('Error fetching courses:', error);
                showToast('Failed to load courses. Please try again.', 'error');
                throw error;
            });
    }

    function fetchTrainers() {
        return fetch('http://localhost:1010/tms/users')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                allTrainers = data.filter(user => user.role === 'TRAINER');
                populateTrainerSelects();
            })
            .catch(error => {
                console.error('Error fetching trainers:', error);
                showToast('Failed to load trainers. Please try again.', 'error');
                throw error;
            });
    }

    function populateTrainerSelects() {
        const defaultOption = '<option value="">Select Trainer</option>';
        const trainerOptions = allTrainers.map(trainer => 
            `<option value="${trainer.id}">${trainer.firstName} ${trainer.lastName}</option>`
        ).join('');

        trainerFilter.innerHTML = '<option value="">All Trainers</option>' + trainerOptions;
        trainerSelect.innerHTML = defaultOption + trainerOptions;
    }

    function filterCourses() {
        const searchTerm = courseSearch.value.toLowerCase();
        const trainerFilterValue = trainerFilter.value;

        filteredCourses = allCourses.filter(course => {
            const matchesSearch =
                course.title.toLowerCase().includes(searchTerm) ||
                (course.description && course.description.toLowerCase().includes(searchTerm));

            const matchesTrainer =
                trainerFilterValue === '' ||
                (course.trainer && course.trainer.id == trainerFilterValue);

            return matchesSearch && matchesTrainer;
        });

        totalCourses = filteredCourses.length;
        currentPage = 1;
        renderCoursesTable();
        updatePagination();
    }

    function renderCoursesTable() {
        const tbody = coursesTable.querySelector('tbody');
        tbody.innerHTML = '';

        if (filteredCourses.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="no-results">No courses found matching your criteria</td></tr>`;
            return;
        }

        const startIndex = (currentPage - 1) * coursesPerPage;
        const endIndex = Math.min(startIndex + coursesPerPage, filteredCourses.length);
        const coursesToDisplay = filteredCourses.slice(startIndex, endIndex);

        coursesToDisplay.forEach(course => {
            const tr = document.createElement('tr');
            const trainerName = course.trainer ? 
                `${course.trainer.firstName} ${course.trainer.lastName}` : 'Not assigned';

            tr.innerHTML = `
                <td>${course.title}</td>
                <td>${course.description || 'N/A'}</td>
                <td>${trainerName}</td>
                <td>${formatDateTime(course.createdAt)}</td>
                <td>${formatDateTime(course.updatedAt)}</td>
                <td class="actions">
                    <button class="action-btn edit-btn" data-id="${course.id}" title="Edit">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="action-btn delete-btn" data-id="${course.id}" title="Delete">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // Add event listeners to action buttons
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', () => openCourseModal('edit', btn.dataset.id));
        });

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => confirmDeleteCourse(btn.dataset.id));
        });
    }

    function updatePagination() {
        const totalPages = Math.ceil(totalCourses / coursesPerPage);
        
        pageIndicator.textContent = currentPage;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;
        
        const startItem = (currentPage - 1) * coursesPerPage + 1;
        const endItem = Math.min(currentPage * coursesPerPage, totalCourses);
        
        paginationInfo.textContent = totalCourses === 0 
            ? 'No courses found' 
            : `Showing ${startItem}-${endItem} of ${totalCourses} courses`;
    }

    function goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderCoursesTable();
            updatePagination();
        }
    }

    function goToNextPage() {
        const totalPages = Math.ceil(totalCourses / coursesPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderCoursesTable();
            updatePagination();
        }
    }

    function openCourseModal(mode, courseId = null) {
        document.getElementById('modal-title').textContent = 
            mode === 'add' ? 'Add New Course' : 'Edit Course';
        
        courseForm.reset();
        document.getElementById('course-id').value = '';
        
        if (mode === 'edit' && courseId) {
            const course = allCourses.find(c => c.id == courseId);
            if (course) {
                document.getElementById('course-id').value = course.id;
                document.getElementById('title').value = course.title;
                document.getElementById('description').value = course.description || '';
                document.getElementById('trainer').value = course.trainer?.id || '';
            }
        }
        
        courseModal.classList.add('active');
    }

    function handleConfirmAction() {
        const { type, courseId } = currentAction;
        
        if (type === 'delete') {
            deleteCourse(courseId);
        }
        
        closeModals();
    }

    function confirmDeleteCourse(courseId) {
        const course = allCourses.find(c => c.id == courseId);
        if (!course) {
            showToast('Course not found', 'error');
            return;
        }
        
        currentAction = { type: 'delete', courseId };
        document.getElementById('confirm-title').textContent = 'Delete Course?';
        document.getElementById('confirm-message').textContent = 
            `Are you sure you want to permanently delete "${course.title}"? This action cannot be undone.`;
        
        confirmModal.classList.add('active');
    }

    function closeModals() {
        courseModal.classList.remove('active');
        confirmModal.classList.remove('active');
    }

    function handleSaveCourse() {
        const courseId = document.getElementById('course-id').value;
        const isEditMode = courseId !== '';
        
        const courseData = {
            title: document.getElementById('title').value,
            description: document.getElementById('description').value,
            trainer: {id : document.getElementById('trainer').value}
        };
        
        // Validate required fields
        if (!courseData.title || !courseData.trainer.id) {
            showToast('Please fill in all required fields', 'error');
            return;
        }
        
        const url = isEditMode ? `http://localhost:1010/tms/courses/${courseId}` : 'http://localhost:1010/tms/courses';
        const method = isEditMode ? 'PUT' : 'POST';
        
        setButtonLoading('save-course', true);

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(courseData)
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw err; });
            }
            return response.json();
        })
        .then(data => {
            showToast(`Course ${isEditMode ? 'updated' : 'created'} successfully`, 'success');
            closeModals();
            fetchCourses();
        })
        .catch(error => {
            console.error('Error saving course:', error);
            showToast(error.message || 'Failed to save course', 'error');
        })
        .finally(() => setButtonLoading('save-course', false));
    }

    function deleteCourse(courseId) {
        setButtonLoading('confirm-action', true);

        fetch(`http://localhost:1010/tms/courses/${courseId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete course');
            }
            return response.json();
        })
        .then(data => {
            showToast('Course deleted successfully', 'success');
            fetchCourses();
        })
        .catch(error => {
            console.error('Error deleting course:', error);
            showToast('Failed to delete course', 'error');
        })
        .finally(() => setButtonLoading('confirm-action', false));
    }

    // Helper functions
    function showLoading(show) {
        const overlay = document.querySelector('.loading-overlay');
        if (overlay) overlay.style.display = show ? 'flex' : 'none';
    }

    function setButtonLoading(buttonId, isLoading) {
        const button = document.getElementById(buttonId);
        if (!button) return;
        
        const spinner = button.querySelector('.spinner-border');
        const buttonText = button.querySelector('.btn-text');
        
        if (isLoading) {
            button.disabled = true;
            if (spinner) spinner.style.display = 'inline-block';
            if (buttonText) buttonText.style.display = 'none';
        } else {
            button.disabled = false;
            if (spinner) spinner.style.display = 'none';
            if (buttonText) buttonText.style.display = 'inline-block';
        }
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
        const toastContainer = document.getElementById('toast-container');
        if (!toastContainer) {
            console.log(`${type.toUpperCase()}: ${message}`);
            return;
        }
        
        const toast = document.createElement('div');
        toast.className = `toast show toast-${type}`;
        toast.innerHTML = `
            <div class="toast-header">
                <strong class="me-auto">${type.charAt(0).toUpperCase() + type.slice(1)}</strong>
                <button type="button" class="btn-close" onclick="this.parentElement.parentElement.remove()"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        `;
        
        toastContainer.appendChild(toast);
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 5000);
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