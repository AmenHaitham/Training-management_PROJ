// user_management_app.js

document.addEventListener('DOMContentLoaded', function () {
    const usersTable = document.getElementById('users-table');
    const userSearch = document.getElementById('user-search');
    const roleFilter = document.getElementById('role-filter');
    const statusFilter = document.getElementById('status-filter');
    const addUserBtn = document.getElementById('add-user-btn');
    const userModal = document.getElementById('user-modal');
    const confirmModal = document.getElementById('confirm-modal');
    const userForm = document.getElementById('user-form');
    const saveUserBtn = document.getElementById('save-user');
    const confirmActionBtn = document.getElementById('confirm-action');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageIndicator = document.getElementById('page-indicator');
    const paginationInfo = document.getElementById('pagination-info');
    const passwordFields = document.querySelector('.password-fields');

    let currentPage = 1;
    const usersPerPage = 10;
    let totalUsers = 0;
    let allUsers = [];
    let filteredUsers = [];
    let currentAction = { type: '', userId: '' };

    fetchUsers();
    setupEventListeners();

    function setupEventListeners() {
        userSearch.addEventListener('input', debounce(filterUsers, 300));
        roleFilter.addEventListener('change', filterUsers);
        statusFilter.addEventListener('change', filterUsers);

        addUserBtn.addEventListener('click', () => openUserModal('add'));
        Array.from(document.querySelectorAll('.close-modal')).forEach(btn => {
            btn.addEventListener('click', closeModals);
        });

        saveUserBtn.addEventListener('click', saveUser);
        userForm.addEventListener('submit', function (e) {
            e.preventDefault();
            saveUser();
        });

        prevPageBtn.addEventListener('click', goToPrevPage);
        nextPageBtn.addEventListener('click', goToNextPage);

        document.getElementById('role').addEventListener('change', function () {
            const isEditMode = document.getElementById('user-id').value !== '';
            passwordFields.classList.toggle('active', !isEditMode);
        });

        window.addEventListener('click', function (e) {
            if (e.target === userModal || e.target === confirmModal) closeModals();
        });

        confirmActionBtn.addEventListener('click', function () {
            const { type, userId } = currentAction;
            if (type === 'toggle') toggleUserStatus(userId);
            else if (type === 'delete') deleteUser(userId);
            closeModals();
        });
    }

    function fetchUsers() {
        fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.USERS}`)
            .then(handleResponse)
            .then(data => {
                allUsers = Array.isArray(data) ? data : data.data || data.users || [];
                totalUsers = allUsers.length;
                filterUsers();
            })
            .catch(error => {
                console.error('Error fetching users:', error);
                showToast('Failed to load users. Please try again.', 'error');
            });
    }

    async function handleResponse(response) {
        if (!response.ok) {
            const errorData = await response.json().catch(() => null);
            throw new Error(errorData?.message || `HTTP error! status: ${response.status}`);
        }
        return response.json();
    }

    function filterUsers() {
        const searchTerm = userSearch.value.toLowerCase();
        const roleFilterValue = roleFilter.value;
        const statusFilterValue = statusFilter.value;

        filteredUsers = allUsers.filter(user => {
            const matchesSearch =
                user.firstName.toLowerCase().includes(searchTerm) ||
                user.lastName.toLowerCase().includes(searchTerm) ||
                user.email.toLowerCase().includes(searchTerm) ||
                (user.phoneNumber && user.phoneNumber.toLowerCase().includes(searchTerm));
            const matchesRole = roleFilterValue === '' || user.role === roleFilterValue;
            const matchesStatus = statusFilterValue === '' || user.status.toString() === statusFilterValue;
            return matchesSearch && matchesRole && matchesStatus;
        });

        totalUsers = filteredUsers.length;
        currentPage = 1;
        renderUsersTable();
        updatePagination();
    }

    function renderUsersTable() {
        const tbody = usersTable.querySelector('tbody');
        tbody.innerHTML = '';

        const startIndex = (currentPage - 1) * usersPerPage;
        const endIndex = Math.min(startIndex + usersPerPage, filteredUsers.length);
        const usersToDisplay = filteredUsers.slice(startIndex, endIndex);

        if (usersToDisplay.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="no-results">No users found matching your criteria</td></tr>`;
            return;
        }

        usersToDisplay.forEach(user => {
            const tr = document.createElement('tr');
            const statusClass = user.status ? 'status-active' : 'status-inactive';
            const statusText = user.status ? 'Active' : 'Inactive';
            const roleClass = `role-${user.role.toLowerCase()}`;
            tr.innerHTML = `
                <td>${user.firstName} ${user.lastName}</td>
                <td>${user.email}</td>
                <td>${user.phoneNumber || 'N/A'}</td>
                <td><span class="role-badge ${roleClass}">${user.role}</span></td>
                <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                <td>${formatDate(user.createdAt)}</td>
                <td class="actions">
                    <button class="action-btn edit-btn" data-id="${user.id}"><i class="fas fa-edit"></i></button>
                    <button class="action-btn toggle-btn" data-id="${user.id}"><i class="fas ${user.status ? 'fa-toggle-on' : 'fa-toggle-off'}"></i></button>
                    <button class="action-btn delete-btn" data-id="${user.id}"><i class="fas fa-trash-alt"></i></button>
                </td>`;
            tbody.appendChild(tr);
        });

        document.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', () => openUserModal('edit', btn.dataset.id));
        });
        document.querySelectorAll('.toggle-btn').forEach(btn => {
            btn.addEventListener('click', () => confirmToggleStatus(btn.dataset.id));
        });
        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => confirmDeleteUser(btn.dataset.id));
        });
    }

    function updatePagination() {
        const totalPages = Math.ceil(totalUsers / usersPerPage);
        pageIndicator.textContent = currentPage;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;
        const startItem = (currentPage - 1) * usersPerPage + 1;
        const endItem = Math.min(currentPage * usersPerPage, totalUsers);
        paginationInfo.textContent = totalUsers === 0 ? 'No users found' : `Showing ${startItem}-${endItem} of ${totalUsers} users`;
    }

    function goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderUsersTable();
            updatePagination();
        }
    }

    function goToNextPage() {
        const totalPages = Math.ceil(totalUsers / usersPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderUsersTable();
            updatePagination();
        }
    }

    function openUserModal(mode, userId = null) {
        document.getElementById('modal-title').textContent = mode === 'add' ? 'Add New User' : 'Edit User';
        userForm.reset();
        document.getElementById('user-id').value = '';
        passwordFields.classList.toggle('active', mode === 'add');

        if (mode === 'edit' && userId) {
            const user = allUsers.find(u => u.id == userId);
            if (user) {
                document.getElementById('user-id').value = user.id;
                document.getElementById('first-name').value = user.firstName;
                document.getElementById('last-name').value = user.lastName;
                document.getElementById('email').value = user.email;
                document.getElementById('phone').value = user.phoneNumber || '';
                document.getElementById('gender').value = user.gender || 'MALE';
                document.getElementById('address').value = user.address || '';
                document.getElementById('role').value = user.role;
            }
        }
        userModal.classList.add('active');
    }

    function confirmToggleStatus(userId) {
        const user = allUsers.find(u => u.id == userId);
        if (!user) return;
        currentAction = { type: 'toggle', userId };
        document.getElementById('confirm-title').textContent = user.status ? 'Deactivate User?' : 'Activate User?';
        document.getElementById('confirm-message').textContent = `Are you sure you want to ${user.status ? 'deactivate' : 'activate'} ${user.firstName} ${user.lastName}?`;
        confirmModal.classList.add('active');
    }

    function confirmDeleteUser(userId) {
        const user = allUsers.find(u => u.id == userId);
        if (!user) return;
        currentAction = { type: 'delete', userId };
        document.getElementById('confirm-title').textContent = 'Delete User?';
        document.getElementById('confirm-message').textContent = `Are you sure you want to permanently delete ${user.firstName} ${user.lastName}? This action cannot be undone.`;
        confirmModal.classList.add('active');
    }

    function closeModals() {
        userModal.classList.remove('active');
        confirmModal.classList.remove('active');
    }

    async function saveUser() {
        const userId = document.getElementById('user-id').value;
        const isEditMode = userId !== '';
        const userData = {
            firstName: document.getElementById('first-name').value.trim(),
            lastName: document.getElementById('last-name').value.trim(),
            email: document.getElementById('email').value.trim(),
            phoneNumber: document.getElementById('phone').value.trim(),
            gender: document.getElementById('gender').value,
            password: document.getElementById('password').value,
            address: document.getElementById('address').value.trim(),
            role: document.getElementById('role').value
        };

        if (!userData.firstName || !userData.lastName || !userData.email || !userData.role) {
            showToast('Please fill in all required fields', 'error');
            return;
        }

        if (!isEditMode) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirm-password').value;

            if (password.length < 6) {
                showToast('Password must be at least 6 characters', 'error');
                return;
            }
            if (password !== confirmPassword) {
                showToast('Passwords do not match', 'error');
                return;
            }
        }

        // Show loading spinner
        const originalButtonText = saveUserBtn.innerHTML;
        saveUserBtn.disabled = true;
        saveUserBtn.innerHTML = `
            <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            Saving...
        `;

        const url = isEditMode ? `${API_CONFIG.BASE_URL}/tms/users/${userId}` : `${API_CONFIG.BASE_URL}/tms/users`;
        const method = isEditMode ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method,
                body: JSON.stringify(userData),
                headers: { 'Content-Type': 'application/json' }
            });

            if (!response.ok) {
                const errorText = await response.text().catch(() => response.statusText);
                throw new Error(`Server responded ${response.status}: ${errorText}`);
            }

            showToast('User saved successfully', 'success');
            closeModals();
            await fetchUsers();
        } catch (error) {
            console.error('Error saving user:', error.message);
            showToast(error.message.includes('Failed to fetch') 
                ? 'Cannot reach server. Is the backend running?'
                : error.message, 'error');
        } finally {
            // Restore original button state
            saveUserBtn.disabled = false;
            saveUserBtn.innerHTML = originalButtonText;
        }
    }

    async function toggleUserStatus(userId) {
        const user = allUsers.find(u => u.id == userId);
        if (!user) return;

        // Show loading on confirm button
        const originalButtonText = confirmActionBtn.innerHTML;
        confirmActionBtn.disabled = true;
        confirmActionBtn.innerHTML = `
            <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            Processing...
        `;

        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}/tms/users/${userId}/status`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: !user.status })
            });

            if (!response.ok) {
                const errorText = await response.text().catch(() => response.statusText);
                throw new Error(`Server responded ${response.status}: ${errorText}`);
            }

            const data = await response.json();
            showToast(`User ${data.status ? 'activated' : 'deactivated'} successfully`, 'success');
            await fetchUsers();
        } catch (error) {
            console.error('Error toggling user status:', error);
            showToast(error.message.includes('Failed to fetch') 
                ? 'Cannot reach server. Is the backend running?'
                : error.message, 'error');
        } finally {
            // Restore original button state
            confirmActionBtn.disabled = false;
            confirmActionBtn.innerHTML = originalButtonText;
        }
    }

    async function deleteUser(userId) {
        // Show loading on confirm button
        const originalButtonText = confirmActionBtn.innerHTML;
        confirmActionBtn.disabled = true;
        confirmActionBtn.innerHTML = `
            <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            Deleting...
        `;

        try {
            const response = await fetch(`${API_CONFIG.BASE_URL}/tms/users/${userId}`, { 
                method: 'DELETE' 
            });

            if (!response.ok) {
                const errorText = await response.text().catch(() => response.statusText);
                throw new Error(`Server responded ${response.status}: ${errorText}`);
            }

            showToast('User deleted successfully', 'success');
            await fetchUsers();
        } catch (error) {
            console.error('Error deleting user:', error);
            showToast(error.message.includes('Failed to fetch') 
                ? 'Cannot reach server. Is the backend running?'
                : error.message, 'error');
        } finally {
            // Restore original button state
            confirmActionBtn.disabled = false;
            confirmActionBtn.innerHTML = originalButtonText;
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
                    day: 'numeric' 
                });
        } catch (e) {
            return 'N/A';
        }
    }

    function showToast(message, type = 'info') {
        const container = document.querySelector('.toast-container') || createToastContainer();
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        
        // Icons for different toast types
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

    function debounce(func, wait) {
        let timeout;
        return function() {
            const context = this, args = arguments;
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(context, args), wait);
        };
    }
});