document.addEventListener('DOMContentLoaded', function() {
    // Current date display
    const currentDateElement = document.getElementById('current-date');
    if (currentDateElement) {
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        currentDateElement.textContent = new Date().toLocaleDateString('en-US', options);
    }

    // Navigation management
const navItems = document.querySelectorAll('.nav li');
navItems.forEach(item => {
    item.addEventListener('click', function() {
        // Remove active class from all items
        navItems.forEach(navItem => navItem.classList.remove('active'));
        
        // Add active class to clicked item
        this.classList.add('active');
        
        // Handle logout separately
        if (this.classList.contains('logout')) {
            alert('Logging out... Redirecting to login page.');
            window.location.href = 'Login.jsp';
            return;
        }

        // Get the text content of the clicked item
        const sectionName = this.textContent.trim();
        console.log(`Navigating to ${sectionName} section`);

        // Check if the section is "Trainings"
        if (sectionName === 'Trainings') {
            window.location.href = 'Trainings.jsp'; // Navigate to Trainings.jsp
        } else {
            alert(`Loading ${sectionName} content...`);
        }
    });
});


    // Card click handlers
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        card.addEventListener('click', function() {
            const cardTitle = this.querySelector('h3').textContent;
            console.log(`Card clicked: ${cardTitle}`);
            
            // In a real app, this would navigate to the detailed view
            alert(`Showing details for ${cardTitle}`);
        });
    });

    // View All links
const viewAllLinks = document.querySelectorAll('.view-all');
viewAllLinks.forEach(link => {
    link.addEventListener('click', function(e) {
        e.preventDefault();
        const section = this.closest('section').querySelector('h2').textContent.trim();
        alert(`Loading all ${section}...`);
        
        // Check if the section is "Training Management" and navigate to Trainings.jsp
        if (section === 'Training Management') {
            console.log('Navigating to Trainings.jsp');
            window.location.href = 'Trainings.jsp'; // Navigate to Trainings.jsp
        } else {
            // In a real app, this would navigate to the full list view for other sections
            console.log(`Navigating to full ${section} view`);
        }
    });
});

    // Action buttons in tables
    const editButtons = document.querySelectorAll('.action-btn.edit');
    const deleteButtons = document.querySelectorAll('.action-btn.delete');
    
    editButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const row = this.closest('tr');
            const userName = row.querySelector('td:first-child').textContent;
            alert(`Editing user: ${userName}`);
            
            // In a real app, this would open an edit modal/form
            console.log(`Editing user ${userName}`);
        });
    });
    
    deleteButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const row = this.closest('tr');
            const userName = row.querySelector('td:first-child').textContent;
            
            if (confirm(`Are you sure you want to delete ${userName}?`)) {
                // In a real app, this would make an API call to delete
                row.remove();
                alert(`${userName} has been deleted.`);
                console.log(`Deleted user ${userName}`);
            }
        });
    });

    // Calendar navigation
    const prevMonthBtn = document.querySelector('.nav-btn.prev');
    const nextMonthBtn = document.querySelector('.nav-btn.next');
    const currentMonthElement = document.getElementById('current-month');
    
    if (prevMonthBtn && nextMonthBtn && currentMonthElement) {
        let currentDate = new Date();
        
        function updateCalendar() {
            const options = { month: 'long', year: 'numeric' };
            currentMonthElement.textContent = currentDate.toLocaleDateString('en-US', options);
            
            // In a real app, you would populate the calendar grid with dates
            console.log(`Calendar updated to ${currentMonthElement.textContent}`);
        }
        
        prevMonthBtn.addEventListener('click', function() {
            currentDate.setMonth(currentDate.getMonth() - 1);
            updateCalendar();
        });
        
        nextMonthBtn.addEventListener('click', function() {
            currentDate.setMonth(currentDate.getMonth() + 1);
            updateCalendar();
        });
        
        // Initialize calendar
        updateCalendar();
    }

    // Session modal functionality
    const sessionModal = document.getElementById('session-modal');
    const addSessionBtn = document.querySelector('.btn.primary');
    const closeModalBtn = document.querySelector('.close-modal');
    
    if (sessionModal && addSessionBtn && closeModalBtn) {
        addSessionBtn.addEventListener('click', function() {
            sessionModal.style.display = 'block';
        });
        
        closeModalBtn.addEventListener('click', function() {
            sessionModal.style.display = 'none';
        });
        
        window.addEventListener('click', function(event) {
            if (event.target === sessionModal) {
                sessionModal.style.display = 'none';
            }
        });
        
        // Session form submission
        const sessionForm = document.getElementById('session-form');
        if (sessionForm) {
            sessionForm.addEventListener('submit', function(e) {
                e.preventDefault();
                
                const title = document.getElementById('session-title').value;
                const date = document.getElementById('session-date').value;
                const time = document.getElementById('session-time').value;
                const course = document.getElementById('session-course').value;
                const instructor = document.getElementById('session-instructor').value;
                
                // In a real app, this would send data to the server
                console.log('New session created:', { title, date, time, course, instructor });
                alert('Session created successfully!');
                
                // Reset form and close modal
                sessionForm.reset();
                sessionModal.style.display = 'none';
            });
        }
    }

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Ctrl+D for Dashboard
        if (e.ctrlKey && e.key === 'd') {
            e.preventDefault();
            document.querySelector('.nav li:nth-child(1)').click();
        }
        
        // Ctrl+U for Users
        if (e.ctrlKey && e.key === 'u') {
            e.preventDefault();
            document.querySelector('.nav li:nth-child(2)').click();
        }
        
        // Ctrl+C for Courses
        if (e.ctrlKey && e.key === 'c') {
            e.preventDefault();
            document.querySelector('.nav li:nth-child(3)').click();
        }
        
        // Ctrl+L for Logout
        if (e.ctrlKey && e.key === 'l') {
            e.preventDefault();
            document.querySelector('.nav li.logout').click();
        }
        
        // Escape to close modal
        if (e.key === 'Escape' && sessionModal.style.display === 'block') {
            sessionModal.style.display = 'none';
        }
    });

    // Responsive sidebar toggle (would need additional CSS)
    const sidebarToggle = document.createElement('div');
    sidebarToggle.className = 'sidebar-toggle';
    sidebarToggle.innerHTML = '<i class="fas fa-bars"></i>';
    document.body.appendChild(sidebarToggle);
    
    sidebarToggle.addEventListener('click', function() {
        document.querySelector('.sidebar').classList.toggle('collapsed');
    });
});
