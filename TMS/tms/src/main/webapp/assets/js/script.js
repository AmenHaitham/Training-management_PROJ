// Training Management System - Client-side JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Bootstrap tooltips initialization
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Form validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
    
    // Delete confirmation
    const deleteButtons = document.querySelectorAll('.btn-delete');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            if (!confirm('Are you sure you want to delete this item? This action cannot be undone.')) {
                event.preventDefault();
            }
        });
    });
    
    // Date inputs - set min date to today for start dates
    const startDateInputs = document.querySelectorAll('input[name="startDate"]');
    const today = new Date().toISOString().split('T')[0];
    startDateInputs.forEach(input => {
        input.setAttribute('min', today);
    });
    
    // End date validation - ensure end date is after start date
    const endDateInputs = document.querySelectorAll('input[name="endDate"]');
    endDateInputs.forEach(endDateInput => {
        const form = endDateInput.closest('form');
        const startDateInput = form.querySelector('input[name="startDate"]');
        
        if (startDateInput) {
            startDateInput.addEventListener('change', function() {
                endDateInput.setAttribute('min', startDateInput.value);
            });
            
            // Set initial min value if start date has a value
            if (startDateInput.value) {
                endDateInput.setAttribute('min', startDateInput.value);
            }
        }
    });
    
    // Filter form submission on input change
    const filterForms = document.querySelectorAll('.filter-form');
    filterForms.forEach(form => {
        const inputs = form.querySelectorAll('select, input:not([type="submit"])');
        inputs.forEach(input => {
            input.addEventListener('change', function() {
                form.submit();
            });
        });
    });
}); 