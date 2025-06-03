const API_CONFIG = {
    get BASE_URL() {
        const protocol = window.location.protocol;
        const hostname = window.location.hostname;
        const port = window.location.port;

        // If running on file:// protocol or Live Server (port 5500)
        if (protocol === 'file:' || (hostname === '127.0.0.1' && port === '5500')) {
            return 'http://localhost:8080';
        }
        
        // If running on server, use relative path
        return '';
    },
    ENDPOINTS: {
        AUTH: '/api/auth',
        USERS: '/api/users',
        TRAINING: '/api/trainings',
        SESSIONS: '/api/sessions',
        TRAINING_COURSES: '/api/training-courses',
        MATERIALS: '/api/materials',
        ROOMS: '/api/rooms',
        DASHBOARD: '/api/dashboard',
        TRAINEE_DASHBOARD: '/api/trainee-dashboard',
        ENROLLMENTS: '/api/enrollments',
        COURSES: '/api/courses',
        CERTIFICATES: '/api/certificates',
        ATTENDANCE: '/api/attendances'
    },
    DEFAULT_HEADERS: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
}; 