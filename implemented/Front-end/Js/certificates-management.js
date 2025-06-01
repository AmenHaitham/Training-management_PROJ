document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const certificatesTable = document.getElementById('certificates-table');
    const certificateSearch = document.getElementById('certificate-search');
    const trainingFilter = document.getElementById('training-filter');
    const generateCertificateBtn = document.getElementById('generate-certificate-btn');
    const generateModal = document.getElementById('generate-modal');
    const certificateForm = document.getElementById('certificate-form');
    const generateBtn = document.getElementById('generate-btn');
    const prevPageBtn = document.getElementById('prev-page');
    const nextPageBtn = document.getElementById('next-page');
    const pageIndicator = document.getElementById('page-indicator');
    const paginationInfo = document.getElementById('pagination-info');
    const traineeSelect = document.getElementById('trainee');
    const trainingSelect = document.getElementById('training');

    // State
    let currentPage = 1;
    const certificatesPerPage = 10;
    let totalCertificates = 0;
    let allCertificates = [];
    let filteredCertificates = [];
    let allTrainings = [];
    let allTrainees = [];

    // Initialize
    fetchTrainings();
    fetchTrainees();
    fetchCertificates();
    setupEventListeners();

    function setupEventListeners() {
        certificateSearch.addEventListener('input', filterCertificates);
        trainingFilter.addEventListener('change', filterCertificates);
        generateCertificateBtn.addEventListener('click', openGenerateModal);
        Array.from(document.querySelectorAll('.close-modal')).forEach(btn => {
            btn.addEventListener('click', closeModals);
        });
        generateBtn.addEventListener('click', generateCertificate);
        prevPageBtn.addEventListener('click', goToPrevPage);
        nextPageBtn.addEventListener('click', goToNextPage);
        window.addEventListener('click', function(e) {
            if (e.target === generateModal) {
                closeModals();
            }
        });
    }

    function fetchTrainings() {
        fetch('http://localhost:1010/tms/trainings')
            .then(response => response.json())
            .then(trainings => {
                allTrainings = trainings;
                populateTrainingFilters();
            })
            .catch(error => {
                console.error('Error fetching trainings:', error);
                showToast('Failed to load trainings. Please try again.', 'error');
            });
    }

    function fetchTrainees() {
        fetch('http://localhost:1010/tms/users?role=TRAINEE')
            .then(response => response.json())
            .then(trainees => {
                allTrainees = trainees;
                populateTraineeSelect();
            })
            .catch(error => {
                console.error('Error fetching trainees:', error);
                showToast('Failed to load trainees. Please try again.', 'error');
            });
    }

    function fetchCertificates() {
        fetch('http://localhost:1010/tms/certificates')
            .then(response => response.json())
            .then(certificates => {
                allCertificates = certificates;
                filterCertificates();
            })
            .catch(error => {
                console.error('Error fetching certificates:', error);
                showToast('Failed to load certificates. Please try again.', 'error');
            });
    }

    function populateTrainingFilters() {
        trainingFilter.innerHTML = '<option value="">All Trainings</option>';
        trainingSelect.innerHTML = '<option value="">Select Training</option>';
        
        allTrainings.forEach(training => {
            const option1 = document.createElement('option');
            option1.value = training.id;
            option1.textContent = training.title;
            trainingFilter.appendChild(option1);

            const option2 = document.createElement('option');
            option2.value = training.id;
            option2.textContent = training.title;
            trainingSelect.appendChild(option2);
        });
    }

    function populateTraineeSelect() {
        traineeSelect.innerHTML = '<option value="">Select Trainee</option>';
        allTrainees.forEach(trainee => {
            const option = document.createElement('option');
            option.value = trainee.id;
            option.textContent = `${trainee.firstName} ${trainee.lastName}`;
            traineeSelect.appendChild(option);
        });
    }

    function filterCertificates() {
        const searchTerm = certificateSearch.value.toLowerCase();
        const trainingFilterValue = trainingFilter.value;

        filteredCertificates = allCertificates.filter(certificate => {
            const matchesSearch = 
                certificate.trainee.firstName.toLowerCase().includes(searchTerm) ||
                certificate.trainee.lastName.toLowerCase().includes(searchTerm) ||
                certificate.training.title.toLowerCase().includes(searchTerm);

            const matchesTraining = trainingFilterValue === '' || certificate.training.id == trainingFilterValue;

            return matchesSearch && matchesTraining;
        });

        totalCertificates = filteredCertificates.length;
        currentPage = 1;
        renderCertificatesTable();
        updatePagination();
    }

    function renderCertificatesTable() {
        const tbody = certificatesTable.querySelector('tbody');
        tbody.innerHTML = '';

        const startIndex = (currentPage - 1) * certificatesPerPage;
        const endIndex = Math.min(startIndex + certificatesPerPage, filteredCertificates.length);
        const certificatesToDisplay = filteredCertificates.slice(startIndex, endIndex);

        if (certificatesToDisplay.length === 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td colspan="4" class="no-results">No certificates found matching your criteria</td>`;
            tbody.appendChild(tr);
            return;
        }

        certificatesToDisplay.forEach(certificate => {
            const tr = document.createElement('tr');
            
            tr.innerHTML = `
                <td>${certificate.trainee.firstName} ${certificate.trainee.lastName}</td>
                <td>${certificate.training.title}</td>
                <td>${formatDate(certificate.issuedAt)}</td>
                <td class="actions">
                    <button class="action-btn view-btn" data-id="${certificate.id}" title="View">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="action-btn download-btn" data-id="${certificate.id}" title="Download">
                        <i class="fas fa-download"></i>
                    </button>
                    <button class="action-btn delete-btn" data-id="${certificate.id}" title="Delete">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            `;
            
            tbody.appendChild(tr);
        });

        // Add event listeners to action buttons
        document.querySelectorAll('.view-btn').forEach(btn => {
            btn.addEventListener('click', () => viewCertificate(btn.dataset.id));
        });

        document.querySelectorAll('.download-btn').forEach(btn => {
            btn.addEventListener('click', () => downloadCertificate(btn.dataset.id));
        });

        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', () => confirmDeleteCertificate(btn.dataset.id));
        });
    }

    function openGenerateModal() {
        certificateForm.reset();
        generateModal.classList.add('active');
    }

    function closeModals() {
        generateModal.classList.remove('active');
    }

    function generateCertificate() {
        const traineeId = traineeSelect.value;
        const trainingId = trainingSelect.value;

        if (!traineeId || !trainingId) {
            showToast('Please select both trainee and training', 'error');
            return;
        }

        fetch('http://localhost:1010/tms/certificates', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                trainee: {id : parseInt(traineeId)},
                training: {id: parseInt(trainingId)}
            })
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw err; });
            }
            return response.json();
        })
        .then(data => {
            showToast('Certificate generated successfully', 'success');
            closeModals();
            fetchCertificates();
        })
        .catch(error => {
            console.error('Error generating certificate:', error);
            showToast(error.message || 'Failed to generate certificate', 'error');
        });
    }

    function viewCertificate(certificateId) {
        window.open(`http://localhost:1010/tms/certificates/${certificateId}/view`, '_blank');
    }

    function downloadCertificate(certificateId) {
    fetch(`http://localhost:1010/tms/certificates/${certificateId}/file`)
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `certificate_${certificateId}.pdf`; // adjust extension if needed
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        })
        .catch(err => {
            console.error('Download failed:', err);
            showToast('Failed to download certificate', 'error');
        });
}

    function confirmDeleteCertificate(certificateId) {
        const certificate = allCertificates.find(c => c.id == certificateId);
        if (!certificate) return;
        
        if (confirm(`Are you sure you want to delete certificate for ${certificate.trainee.firstName} ${certificate.trainee.lastName}?`)) {
            deleteCertificate(certificateId);
        }
    }

    function deleteCertificate(certificateId) {
        fetch(`http://localhost:1010/tms/certificates/${certificateId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete certificate');
            }
            return response.json();
        })
        .then(data => {
            showToast('Certificate deleted successfully', 'success');
            fetchCertificates();
        })
        .catch(error => {
            console.error('Error deleting certificate:', error);
            showToast('Failed to delete certificate', 'error');
        });
    }

    function updatePagination() {
        const totalPages = Math.ceil(totalCertificates / certificatesPerPage);
        
        pageIndicator.textContent = currentPage;
        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;
        
        const startItem = (currentPage - 1) * certificatesPerPage + 1;
        const endItem = Math.min(currentPage * certificatesPerPage, totalCertificates);
        
        paginationInfo.textContent = totalCertificates === 0 ? 'No certificates found' : 
            `Showing ${startItem}-${endItem} of ${totalCertificates} certificates`;
    }

    function goToPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderCertificatesTable();
            updatePagination();
        }
    }

    function goToNextPage() {
        const totalPages = Math.ceil(totalCertificates / certificatesPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderCertificatesTable();
            updatePagination();
        }
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

    function showToast(message, type = 'info') {
        // Simple fallback toast — replace with your custom toast system if needed
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
