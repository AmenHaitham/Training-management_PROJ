document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const attendanceTable = document.getElementById('attendance-table');
    const attendanceSearch = document.getElementById('attendance-search');
    const sessionFilter = document.getElementById('session-filter');
    const statusFilter = document.getElementById('status-filter');
    const addAttendanceBtn = document.getElementById('add-attendance-btn');
    const attendanceModal = document.getElementById('attendance-modal');
    const confirmModal = document.getElementById('confirm-modal');
    const attendanceForm = document.getElementById('attendance-form');
    const saveAttendanceBtn = document.getElementById('save-attendance');
    const confirmActionBtn = document.getElementById('confirm-action');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageIndicator = document.getElementById('page-indicator');
    const paginationInfo = document.getElementById('pagination-info');

    // State variables
    let currentPage = 1;
    const itemsPerPage = 10;
    let totalItems = 0;
    let allAttendance = [];
    let filteredAttendance = [];
    let allTrainees = [];
    let allSessions = [];
    let currentAction = { type: '', attendanceId: '' };

    // Initialize
    fetchAttendance();
    fetchTrainees();
    fetchSessions();
    setupEventListeners();

    function setupEventListeners() {
        // Search and filter
        attendanceSearch.addEventListener('input', filterAttendance);
        sessionFilter.addEventListener('change', filterAttendance);
        statusFilter.addEventListener('change', filterAttendance);

        // Modals
        addAttendanceBtn.addEventListener('click', () => openAttendanceModal('add'));
        Array.from(document.querySelectorAll('.close-modal')).forEach(btn => {
            btn.addEventListener('click', closeModals);
        });

        // Form submission
        saveAttendanceBtn.addEventListener('click', saveAttendance);
        attendanceForm.addEventListener('submit', function(e) {
            e.preventDefault();
            saveAttendance();
        });

        // Pagination
        prevPageBtn.addEventListener('click', goToPrevPage);
        nextPageBtn.addEventListener('click', goToNextPage);
    }

    function fetchAttendance() {
    fetch('http://localhost:1010/tms/attendance')
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch attendance');
            return response.json();
        })
        .then(response => {
            if (response.status !== 'success') {
                throw new Error(response.message || 'Failed to load attendance');
            }
            
            allAttendance = response.data;
            totalItems = allAttendance.length;
            filterAttendance();
        })
        .catch(error => {
            console.error('Error fetching attendance:', error);
            showToast(error.message || 'Failed to load attendance records', 'error');
        });
}


    function fetchTrainees() {
    fetch('http://localhost:1010/tms/users')
        .then(response => response.json())
        .then(data => {
            // Filter users with role === 'TRAINEE'
            allTrainees = data.filter(user => user.role === 'TRAINEE');
            populateTraineeDropdown();
        })
        .catch(error => console.error('Error fetching users:', error));
}


    function fetchSessions() {
        fetch('http://localhost:1010/tms/session')
            .then(response => response.json())
            .then(data => {
                allSessions = data;
                populateSessionDropdown();
                populateSessionFilter();
            });
    }

    function populateTraineeDropdown() {
    return new Promise((resolve) => {
        const dropdown = document.getElementById('trainee');
        dropdown.innerHTML = '<option value="">Select Trainee</option>';
        
        allTrainees.forEach(trainee => {
            const option = document.createElement('option');
            option.value = trainee.id;
            option.textContent = `${trainee.firstName} ${trainee.lastName}`;
            dropdown.appendChild(option);
        });
        
        resolve();
    });
}
    function populateSessionDropdown() {
    return new Promise((resolve) => {
        const dropdown = document.getElementById('session');
        dropdown.innerHTML = '<option value="">Select Session</option>';
        
        allSessions.forEach(session => {
            const option = document.createElement('option');
            option.value = session.id;
            option.textContent = session.trainingCourseId.training.title + " - " + 
                              session.trainingCourseId.course.title + " (" + 
                              formatDate(session.sessionDate) + ")";
            dropdown.appendChild(option);
        });
        
        resolve();
    });
}

    function populateSessionFilter() {
        sessionFilter.innerHTML = '<option value="">All Sessions</option>';
        allSessions.forEach(session => {
            const option = document.createElement('option');
            option.value = session.id;
            option.textContent =  session.trainingCourseId.training.title + " - " +  session.trainingCourseId.course.title + " (" + formatDate(session.sessionDate) + ")";
            sessionFilter.appendChild(option);
        });
    }

    function filterAttendance() {
    const searchTerm = attendanceSearch.value.toLowerCase();
    const sessionId = sessionFilter.value;
    const status = statusFilter.value;

    filteredAttendance = allAttendance.filter(record => {
        const matchesSearch = 
            `${record.trainee.firstName} ${record.trainee.lastName}`.toLowerCase().includes(searchTerm) ||
            record.session.sessionDate.toLowerCase().includes(searchTerm) ||
            record.session.startTime.toLowerCase().includes(searchTerm);

        const matchesSession = !sessionId || record.session.id == sessionId;
        const matchesStatus = !status || record.status === status;

        return matchesSearch && matchesSession && matchesStatus;
    });

    totalItems = filteredAttendance.length;
    currentPage = 1;
    renderAttendanceTable();
    updatePagination();
}

    function renderAttendanceTable() {
    const tbody = attendanceTable.querySelector('tbody');
    tbody.innerHTML = '';

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = Math.min(startIndex + itemsPerPage, filteredAttendance.length);
    const itemsToDisplay = filteredAttendance.slice(startIndex, endIndex);

    if (itemsToDisplay.length === 0) {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td colspan="5" class="no-results">No attendance records found</td>`;
        tbody.appendChild(tr);
        return;
    }

    itemsToDisplay.forEach(record => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${record.trainee.firstName} ${record.trainee.lastName}</td>
            <td>${formatDate(record.session.sessionDate)} - ${record.session.startTime}</td>
            <td><span class="status-badge status-${record.status.toLowerCase()}">${record.status}</span></td>
            <td>${record.recordedAt}</td>
            <td class="actions">
                <button class="action-btn edit-btn" data-id="${record.id}" title="Edit">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="action-btn delete-btn" data-id="${record.id}" title="Delete">
                    <i class="fas fa-trash-alt"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    // Add event listeners to action buttons
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', () => openAttendanceModal('edit', btn.dataset.id));
    });

    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', () => confirmDeleteAttendance(btn.dataset.id));
    });
}

    function openAttendanceModal(mode, attendanceId = null) {
    document.getElementById('modal-title').textContent = 
        mode === 'add' ? 'Record Attendance' : 'Edit Attendance Record';

    // Reset form but keep dropdown options
    attendanceForm.reset();
    document.getElementById('attendance-id').value = '';

    if (mode === 'edit' && attendanceId) {
        const record = allAttendance.find(a => a.id == attendanceId);
        if (record) {
            document.getElementById('attendance-id').value = record.id;
            
            // Ensure dropdowns are populated before setting values
            populateTraineeDropdown().then(() => {
                document.getElementById('trainee').value = record.trainee.id;
            });
            
            populateSessionDropdown().then(() => {
                document.getElementById('session').value = record.session.id;
            });
            
            document.getElementById('status').value = record.status;
        }
    }

    attendanceModal.classList.add('active');
}


    function saveAttendance() {
    const attendanceId = document.getElementById('attendance-id').value;
    const isEditMode = attendanceId !== '';

    // Prepare data - ensure your field names match what the server expects
    const attendanceData = {
        trainee:{ id: parseInt(document.getElementById('trainee').value)},
        session:{ id: parseInt(document.getElementById('session').value)},
        status: document.getElementById('status').value
    };

    // Validate
    if (!attendanceData.trainee.id || !attendanceData.session.id || !attendanceData.status) {
        showToast('Please fill in all required fields', 'error');
        return;
    }

    const baseUrl = 'http://localhost:1010/tms/attendance';
    const url = isEditMode ? `${baseUrl}/${attendanceId}` : baseUrl;
    const method = isEditMode ? 'PUT' : 'POST';

    console.log('Sending request to:', url, 'with data:', attendanceData);

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            // Add authentication headers if needed
        },
        
        body: JSON.stringify(attendanceData)
    })
    .then(async response => {
        const data = await response.json();
        
        if (!response.ok) {
            console.error('Server error response:', data);
            throw new Error(data.message || `HTTP error! status: ${response.status}`);
        }
        
        
        return data;
    })
    .then(data => {
    showToast(`Attendance record ${isEditMode ? 'updated' : 'created'} successfully`, 'success');
    closeModals();
    setTimeout(fetchAttendance, 300); // short delay to let backend persist changes
})
    .catch(error => {
        console.error('Error saving attendance:', error);
        showToast(error.message || 'Failed to save attendance record', 'error');
    });
}


    function confirmDeleteAttendance(attendanceId) {
    try {
        // Find the attendance record
        const record = allAttendance.find(a => a.id == attendanceId);
        if (!record) {
            showToast('Attendance record not found', 'error');
            return;
        }

        // Since your JSON structure includes nested trainee and session objects,
        // we can access them directly from the record
        const traineeName = record.trainee ? 
            `${record.trainee.firstName} ${record.trainee.lastName}` : 'N/A';
        
        const sessionInfo = ' ( ' + record.session.sessionDate + ' ) ';

        // Set up the confirmation modal
        currentAction = { 
            type: 'delete', 
            attendanceId: record.id 
        };
        
        document.getElementById('confirm-title').textContent = 'Delete Attendance Record?';
        document.getElementById('confirm-message').textContent = 
            `Are you sure you want to delete the attendance record for ${traineeName} at ${sessionInfo}?`;

        // Show the modal
        confirmModal.classList.add('active');

    } catch (error) {
        console.error('Error in confirmDeleteAttendance:', error);
        showToast('Failed to prepare delete confirmation', 'error');
    }
}
    // Confirm action (delete)
    confirmActionBtn.addEventListener('click', function() {
        const { type, attendanceId } = currentAction;
        
        if (type === 'delete') {
            deleteAttendance(attendanceId);
        }
        
        closeModals();
    });

    function deleteAttendance(attendanceId) {
    fetch(`http://localhost:1010/tms/attendance/${attendanceId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                throw new Error(err.message || 'Failed to delete attendance');
            });
        }
        return response.json();
    })
    .then(response => {
        showToast('Attendance record deleted successfully', 'success');
        fetchAttendance();
    })
    .catch(error => {
        console.error('Error deleting attendance:', error);
        showToast(error.message || 'Failed to delete attendance record', 'error');
    });
}
    // Pagination functions (same as users.js)
    function updatePagination() {
        const totalPages = Math.ceil(totalItems / itemsPerPage);
        
        pageIndicator.textContent = currentPage;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;
        
        const startItem = (currentPage - 1) * itemsPerPage + 1;
        const endItem = Math.min(currentPage * itemsPerPage, totalItems);
        
        paginationInfo.textContent = totalItems === 0 ? 'No records found' : 
            `Showing ${startItem}-${endItem} of ${totalItems} records`;
    }

    function goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderAttendanceTable();
            updatePagination();
        }
    }

    function goToNextPage() {
        const totalPages = Math.ceil(totalItems / itemsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderAttendanceTable();
            updatePagination();
        }
    }

    function formatDate(isoDateStr) {
    const date = new Date(isoDateStr);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function formatDateTime(isoDateStr) {
    const date = new Date(isoDateStr);
    return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

    function closeModals() {
        attendanceModal.classList.remove('active');
        confirmModal.classList.remove('active');
    }

    function showToast(message, type = 'info') {
        console.log(`${type.toUpperCase()}: ${message}`);
        alert(`${type.toUpperCase()}: ${message}`);
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