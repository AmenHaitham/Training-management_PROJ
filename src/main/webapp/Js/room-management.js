document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const roomsTable = document.getElementById('rooms-table');
    const roomSearch = document.getElementById('room-search');
    const statusFilter = document.getElementById('status-filter');
    const capacityFilter = document.getElementById('capacity-filter');
    const addRoomBtn = document.getElementById('add-room-btn');
    const roomModal = document.getElementById('room-modal');
    const confirmModal = document.getElementById('confirm-modal');
    const roomForm = document.getElementById('room-form');
    const saveRoomBtn = document.getElementById('save-room');
    const confirmActionBtn = document.getElementById('confirm-action');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageIndicator = document.getElementById('page-indicator');
    const paginationInfo = document.getElementById('pagination-info');

    // State variables
    let currentPage = 1;
    const roomsPerPage = 10;
    let totalRooms = 0;
    let allRooms = [];
    let filteredRooms = [];
    let currentAction = { type: '', roomId: '' };

    // Initialize the page
    fetchRooms();
    setupEventListeners();

    function setupEventListeners() {
        // Search and filter events with debounce
        roomSearch.addEventListener('input', debounce(filterRooms, 300));
        statusFilter.addEventListener('change', filterRooms);
        capacityFilter.addEventListener('change', filterRooms);

        // Modal events
        addRoomBtn.addEventListener('click', () => openRoomModal('add'));
        Array.from(document.querySelectorAll('.close-modal')).forEach(btn => {
            btn.addEventListener('click', closeModals);
        });

        // Form submission
        saveRoomBtn.addEventListener('click', handleSaveRoom);
        roomForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleSaveRoom();
        });

        // Pagination
        prevPageBtn.addEventListener('click', goToPrevPage);
        nextPageBtn.addEventListener('click', goToNextPage);

        // Confirm action
        confirmActionBtn.addEventListener('click', handleConfirmAction);

        // Close modals when clicking outside
        window.addEventListener('click', function(e) {
            if (e.target === roomModal || e.target === confirmModal) {
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

    function fetchRooms() {
        showLoading(true);
        fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.ROOMS}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                allRooms = data;
                totalRooms = data.length;
                filterRooms();
            })
            .catch(error => {
                console.error('Error fetching rooms:', error);
                showToast('Failed to load rooms. Please try again.', 'error');
            })
            .finally(() => showLoading(false));
    }

    function filterRooms() {
        const searchTerm = roomSearch.value.toLowerCase();
        const statusFilterValue = statusFilter.value;
        const capacityFilterValue = capacityFilter.value;

        filteredRooms = allRooms.filter(room => {
            const matchesSearch = 
                room.location.toLowerCase().includes(searchTerm);

            const matchesStatus = statusFilterValue === '' || room.status === statusFilterValue;
            
            let matchesCapacity = true;
            if (capacityFilterValue) {
                matchesCapacity = room.capacity >= parseInt(capacityFilterValue);
            }

            return matchesSearch && matchesStatus && matchesCapacity;
        });

        totalRooms = filteredRooms.length;
        currentPage = 1;
        renderRoomsTable();
        updatePagination();
    }

    function renderRoomsTable() {
        const tbody = roomsTable.querySelector('tbody');
        tbody.innerHTML = '';

        if (filteredRooms.length === 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td colspan="7" class="no-results">No rooms found matching your criteria</td>`;
            tbody.appendChild(tr);
            return;
        }

        const startIndex = (currentPage - 1) * roomsPerPage;
        const endIndex = Math.min(startIndex + roomsPerPage, filteredRooms.length);
        const roomsToDisplay = filteredRooms.slice(startIndex, endIndex);

        roomsToDisplay.forEach(room => {
            const tr = document.createElement('tr');
            
            const statusClass = `status-${room.status.toLowerCase()}`;
            const statusText = getStatusLabel(room.status);
            
            tr.innerHTML = `
                <td>${room.id}</td>
                <td>${room.location}</td>
                <td>${room.capacity}</td>
                <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                <td>${formatDateTime(room.createdAt)}</td>
                <td>${formatDateTime(room.updatedAt)}</td>
                <td class="actions">
                    <button class="action-btn edit-btn" data-id="${room.id}" title="Edit">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="action-btn status-btn" data-id="${room.id}" title="Change Status">
                        <i class="fas fa-exchange-alt"></i>
                    </button>
                    <button class="action-btn delete-btn" data-id="${room.id}" title="Delete">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            `;
            
            tbody.appendChild(tr);
        });

        // Add event listeners to action buttons
        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', () => openRoomModal('edit', btn.dataset.id));
        });

        document.querySelectorAll('.status-btn').forEach(btn => {
            btn.addEventListener('click', () => openStatusModal(btn.dataset.id));
        });

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => confirmDeleteRoom(btn.dataset.id));
        });
    }

    function updatePagination() {
        const totalPages = Math.ceil(totalRooms / roomsPerPage);
        
        pageIndicator.textContent = currentPage;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;
        
        const startItem = (currentPage - 1) * roomsPerPage + 1;
        const endItem = Math.min(currentPage * roomsPerPage, totalRooms);
        
        paginationInfo.textContent = totalRooms === 0 ? 'No rooms found' : 
            `Showing ${startItem}-${endItem} of ${totalRooms} rooms`;
    }

    function goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderRoomsTable();
            updatePagination();
        }
    }

    function goToNextPage() {
        const totalPages = Math.ceil(totalRooms / roomsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderRoomsTable();
            updatePagination();
        }
    }

    function openRoomModal(mode, roomId = null) {
        document.getElementById('modal-title').textContent = 
            mode === 'add' ? 'Add New Room' : 'Edit Room';
        
        // Reset form
        roomForm.reset();
        document.getElementById('room-id').value = '';
        
        if (mode === 'edit' && roomId) {
            const room = allRooms.find(r => r.id == roomId);
            if (room) {
                document.getElementById('room-id').value = room.id;
                document.getElementById('location').value = room.location;
                document.getElementById('capacity').value = room.capacity;
                document.getElementById('status').value = room.status;
            }
        }
        
        roomModal.classList.add('active');
    }

    function openStatusModal(roomId) {
        const room = allRooms.find(r => r.id == roomId);
        if (!room) {
            showToast('Room not found', 'error');
            return;
        }
        
        // Create status options dynamically
        const statusOptions = Object.values(RoomStatus)
            .map(status => 
                `<option value="${status}" ${room.status === status ? 'selected' : ''}>
                    ${getStatusLabel(status)}
                </option>`
            ).join('');
        
        document.getElementById('confirm-title').textContent = 'Change Room Status';
        document.getElementById('confirm-message').innerHTML = `
            <div class="status-change-container">
                <div class="current-status">
                    <span>Current Status:</span>
                    <span class="status-badge status-${room.status.toLowerCase()}">
                        ${getStatusLabel(room.status)}
                    </span>
                </div>
                <div class="form-group">
                    <label for="new-status">New Status:</label>
                    <select id="new-status" class="form-control">
                        ${statusOptions}
                    </select>
                </div>
            </div>
        `;
        
        currentAction = { type: 'status', roomId };
        confirmModal.classList.add('active');
    }

    function handleConfirmAction() {
        const { type, roomId } = currentAction;
        
        if (type === 'delete') {
            deleteRoom(roomId);
        } else if (type === 'status') {
            updateRoomStatus(roomId);
        }
        
        closeModals();
    }

    function updateRoomStatus(roomId) {
        const newStatus = document.getElementById('new-status').value;
        const room = allRooms.find(r => r.id == roomId);
        
        if (!room || !newStatus) {
            showToast('Invalid room or status', 'error');
            return;
        }

        setButtonLoading('confirm-action', true);

        fetch(`${API_CONFIG.BASE_URL}/tms/rooms/${roomId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status: newStatus })
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.json();
        })
        .then(updatedRoom => {
            showToast(`Status updated to ${getStatusLabel(newStatus)}`, 'success');
            // Update the room in our local array
            const index = allRooms.findIndex(r => r.id == roomId);
            if (index !== -1) {
                allRooms[index] = updatedRoom;
            }
            filterRooms();
        })
        .catch(error => {
            console.error('Error updating status:', error);
            showToast('Failed to update status: ' + error.message, 'error');
        })
        .finally(() => setButtonLoading('confirm-action', false));
    }

    function confirmDeleteRoom(roomId) {
        const room = allRooms.find(r => r.id == roomId);
        if (!room) return;
        
        currentAction = { type: 'delete', roomId };
        document.getElementById('confirm-title').textContent = 'Delete Room?';
        document.getElementById('confirm-message').textContent = 
            `Are you sure you want to permanently delete "${room.location}"? This action cannot be undone.`;
        
        confirmModal.classList.add('active');
    }

    function closeModals() {
        roomModal.classList.remove('active');
        confirmModal.classList.remove('active');
    }

    function handleSaveRoom() {
        const roomId = document.getElementById('room-id').value;
        const isEditMode = roomId !== '';
        
        const roomData = {
            location: document.getElementById('location').value.trim(),
            capacity: parseInt(document.getElementById('capacity').value),
            status: document.getElementById('status').value
        };
        
        // Validate required fields
        if (!roomData.location || isNaN(roomData.capacity) || !roomData.status) {
            showToast('Please fill in all required fields', 'error');
            return;
        }
        
        // Validate capacity
        if (roomData.capacity <= 0) {
            showToast('Capacity must be greater than 0', 'error');
            return;
        }
        
        const url = isEditMode ? `${API_CONFIG.BASE_URL}/tms/rooms/${roomId}` : `${API_CONFIG.BASE_URL}/tms/rooms`;
        const method = isEditMode ? 'PUT' : 'POST';
        
        setButtonLoading('save-room', true);

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(roomData)
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw err; });
            }
            return response.json();
        })
        .then(data => {
            showToast(`Room ${isEditMode ? 'updated' : 'created'} successfully`, 'success');
            closeModals();
            fetchRooms(); // Refresh the room list
        })
        .catch(error => {
            console.error('Error saving room:', error);
            showToast(error.message || 'Failed to save room', 'error');
        })
        .finally(() => setButtonLoading('save-room', false));
    }

    function deleteRoom(roomId) {
        setButtonLoading('confirm-action', true);

        fetch(`${API_CONFIG.BASE_URL}/tms/rooms/${roomId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete room');
            }
            return response.json();
        })
        .then(data => {
            showToast('Room deleted successfully', 'success');
            fetchRooms(); // Refresh the room list
        })
        .catch(error => {
            console.error('Error deleting room:', error);
            showToast('Failed to delete room', 'error');
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

    // Room status enum for frontend
    const RoomStatus = {
        AVAILABLE: 'AVAILABLE',
        UNAVAILABLE: 'UNAVAILABLE',
        MAINTENANCE: 'MAINTENANCE'
    };

    function getStatusLabel(status) {
        const statusLabels = {
            [RoomStatus.AVAILABLE]: 'Available',
            [RoomStatus.UNAVAILABLE]: 'Unavailable',
            [RoomStatus.MAINTENANCE]: 'Maintenance'
        };
        return statusLabels[status] || status;
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