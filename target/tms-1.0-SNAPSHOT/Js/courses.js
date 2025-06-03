document.addEventListener('DOMContentLoaded', function() {
    // View toggle functionality
    const toggleButtons = document.querySelectorAll('.toggle-btn');
    const gridView = document.getElementById('gridView');
    const listView = document.getElementById('listView');
    
    toggleButtons.forEach(button => {
        button.addEventListener('click', function() {
            toggleButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            
            if (this.dataset.view === 'grid') {
                gridView.classList.remove('hidden');
                listView.classList.add('hidden');
            } else {
                gridView.classList.add('hidden');
                listView.classList.remove('hidden');
            }
        });
    });

    // Initialize progress circles in list view
    const progressCircles = document.querySelectorAll('.progress-circle');
    
    progressCircles.forEach(circle => {
        const progress = circle.getAttribute('data-progress');
        const progressRing = circle.querySelector('.progress-ring-circle');
        const radius = progressRing.getAttribute('r');
        const circumference = 2 * Math.PI * radius;
        
        progressRing.style.strokeDasharray = circumference;
        progressRing.style.strokeDashoffset = circumference - (progress / 100) * circumference;
        progressRing.style.stroke = getComputedStyle(document.documentElement).getPropertyValue('--primary-color');
    });

    // Course details modal
    const courseModal = document.getElementById('courseModal');
    const viewDetailsButtons = document.querySelectorAll('.course-actions .outline, .course-card .outline');
    const closeModal = document.querySelector('.close-modal');
    
    viewDetailsButtons.forEach(button => {
        button.addEventListener('click', function() {
            courseModal.classList.add('active');
        });
    });
    
    closeModal.addEventListener('click', function() {
        courseModal.classList.remove('active');
    });
    
    // Close modal when clicking outside
    courseModal.addEventListener('click', function(e) {
        if (e.target === courseModal) {
            courseModal.classList.remove('active');
        }
    });

    // Tab functionality in course modal
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const tabId = this.dataset.tab;
            
            // Update active tab button
            tabButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            
            // Show corresponding tab content
            tabContents.forEach(content => {
                content.classList.remove('active');
                if (content.id === tabId) {
                    content.classList.add('active');
                }
            });
        });
    });

    // Enroll button functionality
    const enrollButtons = document.querySelectorAll('.course-actions .primary:not(:disabled)');
    
    enrollButtons.forEach(button => {
        if (button.textContent.includes('Enroll')) {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                // In a real app, this would enroll the user in the course
                const courseCard = this.closest('.course-card');
                const courseTitle = courseCard.querySelector('h3').textContent;
                
                // Simulate enrollment
                courseCard.querySelector('.course-badge').textContent = 'Enrolled';
                courseCard.querySelector('.course-badge').className = 'course-badge upcoming';
                this.textContent = 'Start Learning';
                
                alert(`You have successfully enrolled in "${courseTitle}"`);
            });
        }
    });

    // Filter functionality
    document.getElementById('applyFilters').addEventListener('click', function() {
        // In a real app, this would filter the courses
        const status = document.getElementById('statusFilter').value;
        const category = document.getElementById('categoryFilter').value;
        console.log(`Filtering by status: ${status}, category: ${category}`);
    });
    
    document.getElementById('resetFilters').addEventListener('click', function() {
        document.getElementById('statusFilter').value = 'all';
        document.getElementById('categoryFilter').value = 'all';
        console.log('Filters reset');
    });

    // Print and export buttons
    document.getElementById('printCourses').addEventListener('click', function() {
        window.print();
    });
    
    document.getElementById('exportCourses').addEventListener('click', function() {
        // In a real app, this would export the courses list
        console.log('Export courses');
    });
});