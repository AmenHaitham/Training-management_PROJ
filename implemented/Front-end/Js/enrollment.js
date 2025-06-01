document.addEventListener('DOMContentLoaded', function () {
    // DOM Elements
    const table = document.getElementById('enrollments-table');
    const searchInput = document.getElementById('enrollment-search');
    const trainingFilter = document.getElementById('training-filter');
    const traineeFilter = document.getElementById('trainee-filter');
    const addBtn = document.getElementById('add-enrollment-btn');
    const modal = document.getElementById('enrollment-modal');
    const confirmModal = document.getElementById('confirm-modal');
    const form = document.getElementById('enrollment-form');
    const saveBtn = document.getElementById('save-enrollment');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageIndicator = document.getElementById('page-indicator');
    const paginationInfo = document.getElementById('pagination-info');

    // State
    let currentPage = 1;
    const itemsPerPage = 10;
    let allEnrollments = [];
    let filteredEnrollments = [];
    let trainings = [];
    let trainees = [];
    let currentAction = { type: '', id: '' };

    // Initialize
    fetchInitialData();
    setupEventListeners();

    function fetchInitialData() {
    Promise.all([
        fetch('http://localhost:1010/tms/enrollments').then(handleResponse),
        fetch('http://localhost:1010/tms/trainings').then(handleResponse),
        fetch('http://localhost:1010/tms/users').then(handleResponse) // Changed from 'users' to 'trainees'
    ])
    .then(([enrollmentsResponse, trainingsResponse, traineesResponse]) => {
        // Handle different possible response structures
        allEnrollments = Array.isArray(enrollmentsResponse) ? enrollmentsResponse : 
                        enrollmentsResponse.data || enrollmentsResponse.enrollments || [];
        
        trainings = Array.isArray(trainingsResponse) ? trainingsResponse : 
                   trainingsResponse.data || trainingsResponse.trainings || [];
        
        trainees = Array.isArray(traineesResponse) ? traineesResponse : 
                  traineesResponse.data || traineesResponse.trainees || [];
        
        // Filter trainees to only include those with role 'TRAINEE' if needed
        if (trainees.some(t => t.role)) {
            trainees = trainees.filter(user => user.role === 'TRAINEE');
        }

        populateDropdown(trainingFilter, trainings, 'title');
        populateDropdown(traineeFilter, trainees, 'fullName');
        populateDropdown(document.getElementById('training'), trainings, 'title');
        populateDropdown(document.getElementById('trainee'), trainees, 'fullName');

        filterEnrollments();
    })
    .catch(error => {
        console.error('Error loading data:', error);
        showToast('Failed to load data. Please try again.', 'error');
        allEnrollments = [];
        trainings = [];
        trainees = [];

        // Clear dropdowns gracefully
        trainingFilter.innerHTML = '<option value="">All trainings</option>';
        traineeFilter.innerHTML = '<option value="">All trainees</option>';
    });
}

async function handleResponse(response) {
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || `HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
}

    function populateDropdown(dropdown, items, textProperty) {
        dropdown.innerHTML = dropdown.id.includes('filter') 
            ? '<option value="">All ' + dropdown.id.replace('-filter', '') + 's</option>'
            : '<option value="">Select ' + dropdown.id + '</option>';
            
        items.forEach(item => {
            const option = document.createElement('option');
            option.value = item.id;
            option.textContent = textProperty === 'fullName' 
                ? `${item.firstName} ${item.lastName}`
                : item[textProperty];
            dropdown.appendChild(option);
        });
    }

    function setupEventListeners() {
        searchInput.addEventListener('input', debounce(filterEnrollments, 300));
        trainingFilter.addEventListener('change', filterEnrollments);
        traineeFilter.addEventListener('change', filterEnrollments);
        
        addBtn.addEventListener('click', () => openModal('add'));
        Array.from(document.querySelectorAll('.close-modal')).forEach(btn => {
            btn.addEventListener('click', closeModals);
        });

        saveBtn.addEventListener('click', saveEnrollment);
        form.addEventListener('submit', e => {
            e.preventDefault();
            saveEnrollment();
        });

        prevPageBtn.addEventListener('click', goToPrevPage);
        nextPageBtn.addEventListener('click', goToNextPage);

        document.getElementById('confirm-action').addEventListener('click', function () {
            const { type, id } = currentAction;
            if (type === 'delete') deleteEnrollment(id);
            closeModals();
        });
    }

    function filterEnrollments() {
        const searchTerm = searchInput.value.toLowerCase();
        const trainingValue = trainingFilter.value;
        const traineeValue = traineeFilter.value;

        filteredEnrollments = allEnrollments.filter(enrollment => {
            const traineeName = enrollment.trainee ? 
                `${enrollment.trainee.firstName || ''} ${enrollment.trainee.lastName || ''}`.toLowerCase() : '';
            const trainingTitle = enrollment.training ? 
                enrollment.training.title.toLowerCase() : '';
                
            const matchesSearch = 
                traineeName.includes(searchTerm) ||
                trainingTitle.includes(searchTerm);
            const matchesTraining = trainingValue === '' || 
                (enrollment.training && enrollment.training.id.toString() === trainingValue);
            const matchesTrainee = traineeValue === '' || 
                (enrollment.trainee && enrollment.trainee.id.toString() === traineeValue);

            return matchesSearch && matchesTraining && matchesTrainee;
        });

        currentPage = 1;
        renderTable();
        updatePagination();
    }

    function renderTable() {
        const tbody = table.querySelector('tbody');
        tbody.innerHTML = '';

        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = Math.min(startIndex + itemsPerPage, filteredEnrollments.length);
        const itemsToDisplay = filteredEnrollments.slice(startIndex, endIndex);

        if (itemsToDisplay.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="no-results">No enrollments found</td></tr>';
            return;
        }

        itemsToDisplay.forEach(enrollment => {
            const tr = document.createElement('tr');
            tr.className = 'enrollment-row';

            const traineeName = enrollment.trainee ? 
                `${enrollment.trainee.firstName} ${enrollment.trainee.lastName}` : 'Unknown Trainee';
            const traineeEmail = enrollment.trainee?.email || '';
            const trainingTitle = enrollment.training?.title || 'Unknown Training';
            const enrollmentDate = enrollment.enrollmentDate || enrollment.enrollment_date || '';

            tr.innerHTML = `
                <td class="trainee-info">
                    <span class="trainee-name">${traineeName}</span>
                    <span class="trainee-email">${traineeEmail}</span>
                </td>
                <td>
                    <span class="training-title">${trainingTitle}</span>
                </td>
                <td>
                    <span class="enrollment-date">${formatDate(enrollmentDate)}</span>
                </td>
                <td class="actions">
                    <button class="action-btn delete-btn" data-id="${enrollment.id}">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => confirmDelete(btn.dataset.id));
        });
    }

    function openModal(mode, id = null) {
        document.getElementById('modal-title').textContent = 
            mode === 'add' ? 'Add New Enrollment' : 'Edit Enrollment';
        form.reset();
        document.getElementById('enrollment-id').value = '';

        if (mode === 'edit' && id) {
            const enrollment = allEnrollments.find(e => e.id == id);
            if (enrollment) {
                document.getElementById('enrollment-id').value = enrollment.id;
                document.getElementById('trainee').value = enrollment.trainee?.id || '';
                document.getElementById('training').value = enrollment.training?.id || '';
            }
        }

        modal.classList.add('active');
    }

    async function saveEnrollment() {
        const id = document.getElementById('enrollment-id').value;
        const isEdit = Boolean(id);

        const enrollment = {
            trainee: { id: parseInt(document.getElementById('trainee').value) },
            training: { id: parseInt(document.getElementById('training').value) }
        };

        if (!enrollment.trainee.id || !enrollment.training.id) {
            return showToast('Please select both trainee and training', 'error');
        }

        const base = 'http://localhost:1010/tms';
        const url = isEdit ? `${base}/enrollments/${id}` : `${base}/enrollments`;
        const method = isEdit ? 'PUT' : 'POST';

        saveBtn.disabled = true;
        const oldText = saveBtn.innerHTML;
        saveBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Saving…';

        try {
            const resp = await fetch(url, {
                method,
                headers: { 
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(enrollment)
            });

            const responseData = await handleResponse(resp);

            if (resp.ok) {
                showToast(`Enrollment ${isEdit ? 'updated' : 'created'} successfully!`, 'success');
                closeModals();
                await fetchInitialData();
            } else {
                throw new Error(responseData.message || 'Failed to save enrollment');
            }
        } catch (err) {
            console.error('Save enrollment error:', err);
            showToast(err.message.includes('Failed to fetch')
                ? 'Cannot reach server. Is the backend running?'
                : err.message, 'error');
        } finally {
            saveBtn.disabled = false;
            saveBtn.innerHTML = oldText;
        }
    }

    function confirmDelete(id) {
        const enrollment = allEnrollments.find(e => e.id == id);
        if (!enrollment) return;

        const traineeName = enrollment.trainee ? 
            `${enrollment.trainee.firstName} ${enrollment.trainee.lastName}` : 'Unknown Trainee';
        const trainingTitle = enrollment.training?.title || 'Unknown Training';

        currentAction = { type: 'delete', id };
        document.getElementById('confirm-title').textContent = 'Delete Enrollment?';
        document.getElementById('confirm-message').textContent = 
            `Are you sure you want to delete the enrollment for ${traineeName} in ${trainingTitle}?`;
        confirmModal.classList.add('active');
    }

    async function deleteEnrollment(id) {
        const originalText = document.getElementById('confirm-action').innerHTML;
        document.getElementById('confirm-action').disabled = true;
        document.getElementById('confirm-action').innerHTML = 
            '<span class="spinner-border spinner-border-sm"></span> Deleting...';

        try {
            const response = await fetch(`http://localhost:1010/tms/enrollments/${id}`, { 
                method: 'DELETE' 
            });
            
            if (!response.ok) {
                const errorData = await response.json().catch(() => null);
                throw new Error(errorData?.message || 'Delete failed');
            }

            showToast('Enrollment deleted successfully', 'success');
            await fetchInitialData();
        } catch (error) {
            console.error('Error:', error);
            showToast(error.message || 'Failed to delete enrollment', 'error');
        } finally {
            document.getElementById('confirm-action').disabled = false;
            document.getElementById('confirm-action').innerHTML = originalText;
        }
    }

    function updatePagination() {
        const totalPages = Math.ceil(filteredEnrollments.length / itemsPerPage);
        pageIndicator.textContent = currentPage;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;

        const startItem = (currentPage - 1) * itemsPerPage + 1;
        const endItem = Math.min(currentPage * itemsPerPage, filteredEnrollments.length);
        paginationInfo.textContent = filteredEnrollments.length === 0 
            ? 'No enrollments found' 
            : `Showing ${startItem}-${endItem} of ${filteredEnrollments.length} enrollments`;
    }

    function goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderTable();
            updatePagination();
        }
    }

    function goToNextPage() {
        const totalPages = Math.ceil(filteredEnrollments.length / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderTable();
            updatePagination();
        }
    }

    function formatDate(dateString) {
        if (!dateString) return 'N/A';
        try {
            const date = new Date(dateString);
            return isNaN(date.getTime()) ? 'N/A' : 
                date.toLocaleDateString('en-US', { 
                    year: 'numeric', 
                    month: 'short', 
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                });
        } catch (e) {
            return 'N/A';
        }
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

    function closeModals() {
        modal.classList.remove('active');
        confirmModal.classList.remove('active');
    }

    function debounce(func, wait) {
        let timeout;
        return function() {
            const context = this, args = arguments;
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(context, args), wait);
        };
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