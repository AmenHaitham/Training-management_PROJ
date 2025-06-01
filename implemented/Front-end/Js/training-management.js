document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const trainingsTable = document.getElementById('trainings-table');
    const trainingSearch = document.getElementById('training-search');
    const statusFilter = document.getElementById('status-filter');
    const startDateFilter = document.getElementById('start-date-filter');
    const endDateFilter = document.getElementById('end-date-filter');
    const addTrainingBtn = document.getElementById('add-training-btn');
    const trainingModal = document.getElementById('training-modal');
    const confirmModal = document.getElementById('confirm-modal');
    const trainingForm = document.getElementById('training-form');
    const saveTrainingBtn = document.getElementById('save-training');
    const confirmActionBtn = document.getElementById('confirm-action');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageIndicator = document.getElementById('page-indicator');
    const paginationInfo = document.getElementById('pagination-info');
    const roomSelect = document.getElementById('room');

    // State variables
    let currentPage = 1;
    const trainingsPerPage = 10;
    let totalTrainings = 0;
    let allTrainings = [];
    let filteredTrainings = [];
    let allRooms = [];
    let currentAction = { type: '', trainingId: '' };

    // Initialize the page
    fetchTrainings();
    fetchRooms();
    setupEventListeners();

    function setupEventListeners() {
        // Search and filter events
        trainingSearch.addEventListener('input', filterTrainings);
        statusFilter.addEventListener('change', filterTrainings);
        startDateFilter.addEventListener('change', filterTrainings);
        endDateFilter.addEventListener('change', filterTrainings);

        // Modal events
        addTrainingBtn.addEventListener('click', () => openTrainingModal('add'));
        Array.from(document.querySelectorAll('.close-modal')).forEach(btn => {
            btn.addEventListener('click', closeModals);
        });

        // Form submission
        saveTrainingBtn.addEventListener('click', saveTraining);
        trainingForm.addEventListener('submit', function(e) {
            e.preventDefault();
            saveTraining();
        });

        // Pagination
        prevPageBtn.addEventListener('click', goToPrevPage);
        nextPageBtn.addEventListener('click', goToNextPage);

        // Confirm action
        confirmActionBtn.addEventListener('click', function() {
            const { type, trainingId } = currentAction;
            
            if (type === 'delete') {
                deleteTraining(trainingId);
            }
            
            closeModals();
        });

        // Close modals when clicking outside
        window.addEventListener('click', function(e) {
            if (e.target === trainingModal) {
                closeModals();
            }
            if (e.target === confirmModal) {
                closeModals();
            }
        });
    }

    function fetchTrainings() {
        fetch('http://localhost:8080/tms/trainings')
            .then(response => response.json())
            .then(data => {
                allTrainings = data;
                totalTrainings = data.length;
                filterTrainings();
            })
            .catch(error => {
                console.error('Error fetching trainings:', error);
                showToast('Failed to load trainings. Please try again.', 'error');
            });
    }

    function fetchRooms() {
        fetch('http://localhost:8080/tms/rooms')
            .then(response => response.json())
            .then(data => {
                allRooms = data;
                populateRoomSelect();
            })
            .catch(error => {
                console.error('Error fetching rooms:', error);
                showToast('Failed to load rooms. Please try again.', 'error');
            });
    }

    function populateRoomSelect() {
        roomSelect.innerHTML = '<option value="">Select Room</option>';
        allRooms.forEach(room => {
            const option = document.createElement('option');
            option.value = room.id;
            option.textContent = `${room.location} (Capacity: ${room.capacity})`;
            roomSelect.appendChild(option);
        });
    }

    function filterTrainings() {
        const searchTerm = trainingSearch.value.toLowerCase();
        const statusFilterValue = statusFilter.value;
        const startDateValue = startDateFilter.value;
        const endDateValue = endDateFilter.value;

        filteredTrainings = allTrainings.filter(training => {
            const matchesSearch = 
                training.title.toLowerCase().includes(searchTerm) ||
                training.description?.toLowerCase().includes(searchTerm) ||
                training.room.location.toLowerCase().includes(searchTerm);

            const matchesStatus = statusFilterValue === '' || training.status === statusFilterValue;
            
            let matchesDate = true;
            if (startDateValue) {
                const startDate = new Date(training.startDate);
                const filterStartDate = new Date(startDateValue);
                matchesDate = matchesDate && startDate >= filterStartDate;
            }
            if (endDateValue) {
                const endDate = new Date(training.endDate);
                const filterEndDate = new Date(endDateValue);
                matchesDate = matchesDate && endDate <= filterEndDate;
            }

            return matchesSearch && matchesStatus && matchesDate;
        });

        totalTrainings = filteredTrainings.length;
        currentPage = 1; // Reset to first page when filtering
        renderTrainingsTable();
        updatePagination();
    }

    function renderTrainingsTable() {
        const tbody = trainingsTable.querySelector('tbody');
        tbody.innerHTML = '';

        const startIndex = (currentPage - 1) * trainingsPerPage;
        const endIndex = Math.min(startIndex + trainingsPerPage, filteredTrainings.length);
        const trainingsToDisplay = filteredTrainings.slice(startIndex, endIndex);

        if (trainingsToDisplay.length === 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td colspan="8" class="no-results">No trainings found matching your criteria</td>`;
            tbody.appendChild(tr);
            return;
        }

        trainingsToDisplay.forEach(training => {
            const tr = document.createElement('tr');
            
            const statusClass = `status-${training.status.toLowerCase()}`;
            const statusText = getStatusLabel(training.status);
            
            tr.innerHTML = `
                <td>${training.id}</td>
                <td>${training.title}</td>
                <td>${training.description || 'N/A'}</td>
                <td>${training.room.location} (${training.room.capacity})</td>
                <td>${formatDate(training.startDate)} - ${formatDate(training.endDate)}</td>
                <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                <td>${formatDateTime(training.createdAt)}</td>
                <td class="actions">
                    <button class="action-btn edit-btn" data-id="${training.id}" title="Edit">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="action-btn status-btn" data-id="${training.id}" title="Change Status">
                        <i class="fas fa-exchange-alt"></i>
                    </button>
                    <button class="action-btn delete-btn" data-id="${training.id}" title="Delete">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            `;
            
            tbody.appendChild(tr);
        });

        // Add event listeners to action buttons
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', () => openTrainingModal('edit', btn.dataset.id));
        });

        document.querySelectorAll('.status-btn').forEach(btn => {
            btn.addEventListener('click', () => openStatusModal(btn.dataset.id));
        });

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => confirmDeleteTraining(btn.dataset.id));
        });
    }

    function updatePagination() {
        const totalPages = Math.ceil(totalTrainings / trainingsPerPage);
        
        pageIndicator.textContent = currentPage;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;
        
        const startItem = (currentPage - 1) * trainingsPerPage + 1;
        const endItem = Math.min(currentPage * trainingsPerPage, totalTrainings);
        
        paginationInfo.textContent = totalTrainings === 0 ? 'No trainings found' : 
            `Showing ${startItem}-${endItem} of ${totalTrainings} trainings`;
    }

    function goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderTrainingsTable();
            updatePagination();
        }
    }

    function goToNextPage() {
        const totalPages = Math.ceil(totalTrainings / trainingsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderTrainingsTable();
            updatePagination();
        }
    }

    function openTrainingModal(mode, trainingId = null) {
        document.getElementById('modal-title').textContent = 
            mode === 'add' ? 'Add New Training' : 'Edit Training';
        
        // Reset form
        trainingForm.reset();
        document.getElementById('training-id').value = '';
        
        if (mode === 'edit' && trainingId) {
            const training = allTrainings.find(t => t.id == trainingId);
            if (training) {
                document.getElementById('training-id').value = training.id;
                document.getElementById('title').value = training.title;
                document.getElementById('description').value = training.description || '';
                document.getElementById('start-date').value = formatDateForInput(training.startDate);
                document.getElementById('end-date').value = formatDateForInput(training.endDate);
                document.getElementById('room').value = training.room.id;
                document.getElementById('status').value = training.status;
            }
        }
        
        trainingModal.classList.add('active');
    }

    function openStatusModal(trainingId) {
        const training = allTrainings.find(t => t.id == trainingId);
        if (!training) return;
        
        // Create status options dynamically
        const statusOptions = Object.values(TrainingStatus).map(status => 
            `<option value="${status}" ${training.status === status ? 'selected' : ''}>
                ${getStatusLabel(status)}
            </option>`
        ).join('');
        
        document.getElementById('confirm-title').textContent = 'Change Training Status';
        document.getElementById('confirm-message').innerHTML = `
            <p>Current status: <span class="status-badge status-${training.status.toLowerCase()}">
                ${getStatusLabel(training.status)}
            </span></p>
            <div class="form-group">
                <label for="new-status">New Status:</label>
                <select id="new-status" class="full-width">
                    ${statusOptions}
                </select>
            </div>
        `;
        
        currentAction = { type: 'status', trainingId };
        confirmModal.classList.add('active');
    }

    function confirmDeleteTraining(trainingId) {
        const training = allTrainings.find(t => t.id == trainingId);
        if (!training) return;
        
        currentAction = { type: 'delete', trainingId };
        document.getElementById('confirm-title').textContent = 'Delete Training?';
        document.getElementById('confirm-message').textContent = 
            `Are you sure you want to permanently delete "${training.title}"? This action cannot be undone.`;
        
        confirmModal.classList.add('active');
    }

    function closeModals() {
        trainingModal.classList.remove('active');
        confirmModal.classList.remove('active');
    }

    function saveTraining() {
        const trainingId = document.getElementById('training-id').value;
        const isEditMode = trainingId !== '';
        
        const trainingData = {
            title: document.getElementById('title').value.trim(),
            description: document.getElementById('description').value.trim(),
            startDate: document.getElementById('start-date').value,
            endDate: document.getElementById('end-date').value,
            room: { id: parseInt(document.getElementById('room').value) },
            status: document.getElementById('status').value
        };
        
        // Validate required fields
        if (!trainingData.title || !trainingData.startDate || !trainingData.endDate || !trainingData.room.id) {
            showToast('Please fill in all required fields', 'error');
            return;
        }
        
        // Validate dates
        const startDate = new Date(trainingData.startDate);
        const endDate = new Date(trainingData.endDate);
        
        if (startDate > endDate) {
            showToast('End date must be after start date', 'error');
            return;
        }
        
        const url = isEditMode ? `http://localhost:8080/tms/trainings/${trainingId}` : 'http://localhost:8080/tms/trainings';
        const method = isEditMode ? 'PUT' : 'POST';
        
        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(trainingData)
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw err; });
            }
            return response.json();
        })
        .then(data => {
            showToast(`Training ${isEditMode ? 'updated' : 'created'} successfully`, 'success');
            closeModals();
            fetchTrainings(); // Refresh the training list
        })
        .catch(error => {
            console.error('Error saving training:', error);
            showToast(error.message || 'Failed to save training', 'error');
        });
    }

    function deleteTraining(trainingId) {
        fetch(`http://localhost:8080/tms/trainings/${trainingId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete training');
            }
            return response.json();
        })
        .then(data => {
            showToast('Training deleted successfully', 'success');
            fetchTrainings(); // Refresh the training list
        })
        .catch(error => {
            console.error('Error deleting training:', error);
            showToast('Failed to delete training', 'error');
        });
    }

    // Training status enum for frontend
    const TrainingStatus = {
        AVAILABLE: 'AVAILABLE',
        UNAVAILABLE: 'UNAVAILABLE',
        CANCELED: 'CANCELED',
        COMPLETED: 'COMPLETED',
        LIVE: 'LIVE'
    };

    function getStatusLabel(status) {
        const statusLabels = {
            [TrainingStatus.AVAILABLE]: 'Available',
            [TrainingStatus.UNAVAILABLE]: 'Unavailable',
            [TrainingStatus.CANCELED]: 'Canceled',
            [TrainingStatus.COMPLETED]: 'Completed',
            [TrainingStatus.LIVE]: 'Live'
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

    // Confirm action (updated)
confirmActionBtn.addEventListener('click', function () {
    const { type, trainingId } = currentAction;

    if (type === 'delete') {
        deleteTraining(trainingId);
    } else if (type === 'status') {
        const newStatus = document.getElementById('new-status').value;
        if (!newStatus) {
            showToast('Please select a new status', 'error');
            return;
        }

        const training = allTrainings.find(t => t.id == trainingId);
        if (!training) {
            showToast('Training not found', 'error');
            return;
        }

        const updatedTraining = {
            ...training,
            room: { id: training.room.id }, // Keep only the room ID to match backend structure
            status: newStatus
        };

        fetch(`http://localhost:8080/tms/trainings/${trainingId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedTraining)
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw err; });
            }
            return response.json();
        })
        .then(() => {
            showToast('Training status updated successfully', 'success');
            closeModals();
            fetchTrainings();
        })
        .catch(error => {
            console.error('Error updating status:', error);
            showToast(error.message || 'Failed to update status', 'error');
        });
    }

    closeModals();
});

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