document.addEventListener('DOMContentLoaded', function() {
    // Initialize date range picker
    flatpickr("#dateRange", {
        mode: "range",
        dateFormat: "Y-m-d",
        defaultDate: [new Date(), new Date().fp_incr(14)] // 14 days from now
    });

    // View toggle functionality
    const toggleButtons = document.querySelectorAll('.toggle-btn');
    const listView = document.getElementById('listView');
    const calendarView = document.getElementById('calendarView');
    
    toggleButtons.forEach(button => {
        button.addEventListener('click', function() {
            toggleButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            
            if (this.dataset.view === 'list') {
                listView.classList.remove('hidden');
                calendarView.classList.add('hidden');
            } else {
                listView.classList.add('hidden');
                calendarView.classList.remove('hidden');
                renderCalendar();
            }
        });
    });

    // Calendar rendering
    function renderCalendar() {
        const calendarBody = document.querySelector('.calendar-body');
        const currentMonthEl = document.querySelector('.current-month');
        const today = new Date();
        const currentMonth = today.getMonth();
        const currentYear = today.getFullYear();
        
        // Set month title
        currentMonthEl.textContent = new Date(currentYear, currentMonth).toLocaleDateString('en-US', {
            month: 'long',
            year: 'numeric'
        });
        
        // Get first day of month and total days
        const firstDay = new Date(currentYear, currentMonth, 1).getDay();
        const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
        
        // Clear calendar
        calendarBody.innerHTML = '';
        
        // Add empty cells for days before first day of month
        for (let i = 0; i < firstDay; i++) {
            const emptyDay = document.createElement('div');
            emptyDay.classList.add('calendar-day', 'empty');
            calendarBody.appendChild(emptyDay);
        }
        
        // Add days of month
        for (let day = 1; day <= daysInMonth; day++) {
            const dayElement = document.createElement('div');
            dayElement.classList.add('calendar-day');
            
            // Highlight today
            if (day === today.getDate() && currentMonth === today.getMonth() && currentYear === today.getFullYear()) {
                dayElement.classList.add('today');
            }
            
            // Add day number
            const dayHeader = document.createElement('div');
            dayHeader.classList.add('calendar-day-header');
            dayHeader.textContent = day;
            dayElement.appendChild(dayHeader);
            
            // Add sample events (in a real app, these would come from your database)
            if (day === 12) {
                addEventToCalendar(dayElement, 'Advanced JavaScript Patterns', '09:00 - 11:00', 'upcoming');
                addEventToCalendar(dayElement, 'React State Management', '13:00 - 15:00', 'completed');
                addEventToCalendar(dayElement, 'Database Indexing', '16:00 - 18:00', 'cancelled');
            } else if (day === 13) {
                addEventToCalendar(dayElement, 'Python for Data Analysis', '10:00 - 12:00', 'completed');
                addEventToCalendar(dayElement, 'Web Security Fundamentals', '14:00 - 16:00', 'upcoming');
            } else if (day === 15) {
                addEventToCalendar(dayElement, 'Database Indexing', '16:00 - 18:00', 'upcoming');
            }
            
            calendarBody.appendChild(dayElement);
        }
    }
    
    function addEventToCalendar(dayElement, title, time, status) {
        const event = document.createElement('div');
        event.classList.add('calendar-event', status);
        event.textContent = `${title} (${time})`;
        event.title = `${title} (${time})`;
        dayElement.appendChild(event);
    }
    
    // Calendar navigation
    document.getElementById('prevMonth').addEventListener('click', function() {
        // In a real app, this would decrement the month and re-render
        console.log('Previous month clicked');
    });
    
    document.getElementById('nextMonth').addEventListener('click', function() {
        // In a real app, this would increment the month and re-render
        console.log('Next month clicked');
    });
    
    document.getElementById('todayBtn').addEventListener('click', function() {
        // In a real app, this would return to current month
        console.log('Today clicked');
    });
    
    // Feedback modal
    const feedbackModal = document.getElementById('feedbackModal');
    const feedbackButtons = document.querySelectorAll('#giveFeedback');
    const closeModal = document.querySelector('.close-modal');
    const cancelFeedback = document.getElementById('cancelFeedback');
    
    feedbackButtons.forEach(button => {
        button.addEventListener('click', function() {
            feedbackModal.classList.add('active');
        });
    });
    
    closeModal.addEventListener('click', function() {
        feedbackModal.classList.remove('active');
    });
    
    cancelFeedback.addEventListener('click', function() {
        feedbackModal.classList.remove('active');
    });
    
    // Close modal when clicking outside
    feedbackModal.addEventListener('click', function(e) {
        if (e.target === feedbackModal) {
            feedbackModal.classList.remove('active');
        }
    });
    
    // Handle feedback form submission
    document.getElementById('feedbackForm').addEventListener('submit', function(e) {
        e.preventDefault();
        // In a real app, this would submit to your backend
        console.log('Feedback submitted');
        feedbackModal.classList.remove('active');
        alert('Thank you for your feedback!');
    });
    
    // Print and export buttons
    document.getElementById('printSchedule').addEventListener('click', function() {
        window.print();
    });
    
    document.getElementById('exportSchedule').addEventListener('click', function() {
        // In a real app, this would export the schedule as PDF or CSV
        console.log('Export schedule');
    });
    
    document.getElementById('printCalendar').addEventListener('click', function() {
        window.print();
    });
    
    // Filter functionality
    document.getElementById('applyFilters').addEventListener('click', function() {
        // In a real app, this would filter the sessions
        console.log('Filters applied');
    });
    
    document.getElementById('resetFilters').addEventListener('click', function() {
        // In a real app, this would reset all filters
        document.getElementById('dateRange')._flatpickr.clear();
        document.getElementById('trainingFilter').value = 'all';
        document.getElementById('statusFilter').value = 'all';
        console.log('Filters reset');
    });
    
    // Initial render
    renderCalendar();
});