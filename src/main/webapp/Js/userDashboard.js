document.addEventListener('DOMContentLoaded', function() {
    // Sidebar toggle functionality
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar = document.querySelector('.sidebar');
    
    sidebarToggle.addEventListener('click', function() {
        sidebar.classList.toggle('active');
    });
    
    // Initialize progress circles
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
    
    // Initialize attendance chart
    const attendanceCtx = document.getElementById('attendanceChart').getContext('2d');
    const attendanceChart = new Chart(attendanceCtx, {
        type: 'doughnut',
        data: {
            labels: ['Present', 'Absent', 'Late'],
            datasets: [{
                data: [85, 8, 7],
                backgroundColor: [
                    getComputedStyle(document.documentElement).getPropertyValue('--primary-color'),
                    getComputedStyle(document.documentElement).getPropertyValue('--neutral-600'),
                    getComputedStyle(document.documentElement).getPropertyValue('--secondary-color')
                ],
                borderWidth: 0
            }]
        },
        options: {
            cutout: '70%',
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        usePointStyle: true,
                        padding: 20
                    }
                }
            },
            maintainAspectRatio: false
        }
    });
    
    // Simulate loading data (in a real app, this would be API calls)
    setTimeout(() => {
        // This would be replaced with actual data fetching
        console.log('Loading trainee data...');
    }, 1000);
    
    // Add click events for buttons (would be connected to actual functionality)
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!this.classList.contains('sidebar-toggle')) {
                e.preventDefault();
                console.log('Button clicked:', this.textContent.trim());
                // In a real app, this would trigger specific actions
            }
        });
    });
    
    // Responsive adjustments
    window.addEventListener('resize', function() {
        // Redraw chart on resize to maintain responsiveness
        attendanceChart.resize();
    });
});