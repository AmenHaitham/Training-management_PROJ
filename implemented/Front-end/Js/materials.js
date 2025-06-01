document.addEventListener('DOMContentLoaded', function () {
    // DOM Elements
    const materialsTable = document.getElementById('materials-table');
    const materialSearch = document.getElementById('material-search');
    const sessionFilter = document.getElementById('session-filter');
    const typeFilter = document.getElementById('type-filter');
    const addMaterialBtn = document.getElementById('add-material-btn');
    const materialModal = document.getElementById('material-modal');
    const confirmModal = document.getElementById('confirm-modal');
    const materialForm = document.getElementById('material-form');
    const saveMaterialBtn = document.getElementById('save-material');
    const confirmActionBtn = document.getElementById('confirm-action');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageIndicator = document.getElementById('page-indicator');
    const paginationInfo = document.getElementById('pagination-info');
    const fileInput = document.getElementById('file');
    const fileInfo = document.getElementById('file-info');

    // State
    let currentPage = 1;
    const materialsPerPage = 10;
    let totalMaterials = 0;
    let allMaterials = [];
    let filteredMaterials = [];
    let allSessions = [];
    let currentAction = { type: '', materialId: '' };

    // Init
    fetchSessions();
    fetchMaterials();
    setupEventListeners();

    function setupEventListeners() {
        materialSearch.addEventListener('input', filterMaterials);
        sessionFilter.addEventListener('change', filterMaterials);
        typeFilter.addEventListener('change', filterMaterials);
        addMaterialBtn.addEventListener('click', () => openMaterialModal('add'));
        Array.from(document.querySelectorAll('.close-modal')).forEach(btn =>
            btn.addEventListener('click', closeModals)
        );
        saveMaterialBtn.addEventListener('click', saveMaterial);
        materialForm.addEventListener('submit', function (e) {
            e.preventDefault();
            saveMaterial();
        });
        fileInput.addEventListener('change', updateFileInfo);
        prevPageBtn.addEventListener('click', goToPrevPage);
        nextPageBtn.addEventListener('click', goToNextPage);
        window.addEventListener('click', function (e) {
            if (e.target === materialModal || e.target === confirmModal) {
                closeModals();
            }
        });
        confirmActionBtn.addEventListener('click', function () {
            if (currentAction.type === 'delete') {
                deleteMaterial(currentAction.materialId);
            }
            closeModals();
        });
    }

    function fetchSessions() {
        fetch('http://localhost:1010/tms/session')
            .then(res => res.json())
            .then(data => {
                allSessions = data;
                populateSessionFilters();
            })
            .catch(() => showToast('Failed to load sessions.', 'error'));
    }

    function fetchMaterials() {
        fetch('http://localhost:1010/tms/materials')
            .then(res => res.json())
            .then(data => {
                allMaterials = data;
                filterMaterials();
            })
            .catch(() => showToast('Failed to load materials.', 'error'));
    }

    function populateSessionFilters() {
        sessionFilter.innerHTML = '<option value="">All Sessions</option>';
        document.getElementById('session').innerHTML = '<option value="">Select Session</option>';
        allSessions.forEach(s => {
            const opt1 = new Option(( s.trainingCourseId.training.title + ' - ' +  s.trainingCourseId.course.title + ' (' + s.sessionDate + ')' ), s.id);
            const opt2 = new Option(( s.trainingCourseId.training.title + ' - ' +  s.trainingCourseId.course.title + ' (' + s.sessionDate + ')' ), s.id);
            sessionFilter.appendChild(opt1);
            document.getElementById('session').appendChild(opt2);
        });
    }

    function filterMaterials() {
        const search = materialSearch.value.toLowerCase();
        const sessionVal = sessionFilter.value;
        const typeVal = typeFilter.value;

        filteredMaterials = allMaterials.filter(m => {
            return (
                (!sessionVal || m.session.id == sessionVal) &&
                (!typeVal || m.fileType === typeVal) &&
                (m.title.toLowerCase().includes(search) || m.description.toLowerCase().includes(search))
            );
        });

        totalMaterials = filteredMaterials.length;
        currentPage = 1;
        renderMaterialsTable();
        updatePagination();
    }

    function renderMaterialsTable() {
        const tbody = materialsTable.querySelector('tbody');
        tbody.innerHTML = '';

        const start = (currentPage - 1) * materialsPerPage;
        const end = Math.min(start + materialsPerPage, filteredMaterials.length);
        const pageMaterials = filteredMaterials.slice(start, end);

        if (pageMaterials.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="no-results">No materials found</td></tr>`;
            return;
        }

        pageMaterials.forEach(material => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${material.title}</td>
                <td>${material.description || 'N/A'}</td>
                 <td>${material.session.trainingCourseId.training.title} - ${material.session.trainingCourseId.course.title } (${material.session.sessionDate})</td>
                <td>${formatFileSize(material.fileSize)}</td>
                <td>${formatDate(material.createdAt)}</td>
                <td class="actions">
                    <button class="action-btn view-btn" data-id="${material.id}"><i class="fas fa-eye"></i></button>
                    <button class="action-btn download-btn" data-id="${material.id}"><i class="fas fa-download"></i></button>
                    <button class="action-btn edit-btn" data-id="${material.id}"><i class="fas fa-edit"></i></button>
                    <button class="action-btn delete-btn" data-id="${material.id}"><i class="fas fa-trash-alt"></i></button>
                </td>`;
            tbody.appendChild(row);
        });

        document.querySelectorAll('.view-btn').forEach(btn =>
            btn.addEventListener('click', () => viewMaterial(btn.dataset.id))
        );
        document.querySelectorAll('.download-btn').forEach(btn =>
            btn.addEventListener('click', () => downloadMaterial(btn.dataset.id))
        );
        document.querySelectorAll('.edit-btn').forEach(btn =>
            btn.addEventListener('click', () => openMaterialModal('edit', btn.dataset.id))
        );
        document.querySelectorAll('.delete-btn').forEach(btn =>
            btn.addEventListener('click', () => confirmDeleteMaterial(btn.dataset.id))
        );
    }

    function updatePagination() {
        const totalPages = Math.ceil(totalMaterials / materialsPerPage);
        pageIndicator.textContent = currentPage;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages;
        paginationInfo.textContent = totalMaterials === 0 ? 'No materials found' :
            `Showing ${(currentPage - 1) * materialsPerPage + 1}-${Math.min(currentPage * materialsPerPage, totalMaterials)} of ${totalMaterials}`;
    }

    function goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderMaterialsTable();
            updatePagination();
        }
    }

    function goToNextPage() {
        const totalPages = Math.ceil(totalMaterials / materialsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderMaterialsTable();
            updatePagination();
        }
    }

    function openMaterialModal(mode, materialId = null) {
        document.getElementById('modal-title').textContent = mode === 'add' ? 'Add Material' : 'Edit Material';
        materialForm.reset();
        document.getElementById('material-id').value = '';
        fileInfo.textContent = 'No file selected';

        if (mode === 'edit' && materialId) {
            const material = allMaterials.find(m => m.id == materialId);
            if (material) {
                document.getElementById('material-id').value = material.id;
                document.getElementById('title').value = material.title;
                document.getElementById('description').value = material.description || '';
                document.getElementById('session').value = material.session.id;
                fileInfo.textContent = `${material.fileName} (${formatFileSize(material.fileSize)})`;
            }
        }

        materialModal.classList.add('active');
    }

    function closeModals() {
        materialModal.classList.remove('active');
        confirmModal.classList.remove('active');
    }

    function updateFileInfo() {
        if (fileInput.files.length > 0) {
            const file = fileInput.files[0];
            fileInfo.textContent = `${file.name} (${formatFileSize(file.size)})`;
        } else {
            fileInfo.textContent = 'No file selected';
        }
    }

    function saveMaterial() {
        const materialId = document.getElementById('material-id').value;
        const isEdit = materialId !== '';
        const title = document.getElementById('title').value.trim();
        const description = document.getElementById('description').value.trim();
        const sessionId = document.getElementById('session').value;
        const file = fileInput.files[0];

        if (!title || !sessionId || (!isEdit && !file)) {
            showToast('Please fill all required fields', 'error');
            return;
        }

        const formData = new FormData();
        formData.append('title', title);
        formData.append('description', description);
        formData.append('sessionId', sessionId);
        if (file) formData.append('file', file);

        const url = isEdit ? `http://localhost:1010/tms/materials/${materialId}` : '/tms/materials';
        const method = isEdit ? 'PUT' : 'POST';

        fetch(url, { method, body: formData })
            .then(res => res.ok ? res.json() : res.json().then(err => { throw err; }))
            .then(() => {
                showToast(`Material ${isEdit ? 'updated' : 'created'} successfully`, 'success');
                closeModals();
                fetchMaterials();
            })
            .catch(err => showToast(err.message || 'Failed to save material', 'error'));
    }

    function confirmDeleteMaterial(id) {
        const material = allMaterials.find(m => m.id == id);
        if (!material) return;
        currentAction = { type: 'delete', materialId: id };
        document.getElementById('confirm-title').textContent = 'Delete Material?';
        document.getElementById('confirm-message').textContent = `Are you sure you want to delete "${material.title}"?`;
        confirmModal.classList.add('active');
    }

    function deleteMaterial(id) {
        fetch(`http://localhost:1010/tms/materials/${id}`, { method: 'DELETE' })
            .then(res => res.ok ? res.json() : Promise.reject())
            .then(() => {
                showToast('Material deleted', 'success');
                fetchMaterials();
            })
            .catch(() => showToast('Failed to delete material', 'error'));
    }

    function viewMaterial(id) {
        window.open(`http://localhost:1010/tms/materials/${id}/view`, '_blank');
    }

    function downloadMaterial(id) {
        // ✅ CORRECTED URL HERE:
        window.location.href = `http://localhost:1010/tms/materials/${id}/file`;
    }

    function formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    function formatDate(str) {
        const d = new Date(str);
        return isNaN(d.getTime()) ? 'N/A' : d.toLocaleDateString('en-US', {
            year: 'numeric', month: 'short', day: 'numeric'
        });
    }

    function showToast(message, type = 'info') {
        alert(`${type.toUpperCase()}: ${message}`); // You can replace with toast UI
    }
});
