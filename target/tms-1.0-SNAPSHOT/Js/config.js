const API_CONFIG = {
    BASE_URL: window.location.protocol === 'file:' 
        ? 'http://localhost:8080'  // When opening files directly
        : window.location.origin,  // When using a server
    ENDPOINTS: {
        AUTH: '/api/auth',
        USERS: '/api/users',
        TRAINING: '/api/training',
        SESSIONS: '/api/sessions',
        TRAINING_COURSES: '/api/training-courses',
        MATERIALS: '/api/materials',
        ROOMS: '/api/rooms',
        DASHBOARD: '/api/dashboard',
        TRAINEE_DASHBOARD: '/api/trainee-dashboard',
        ENROLLMENTS: '/api/enrollments',
        COURSES: '/api/courses',
        CERTIFICATES: '/api/certificates',
        ATTENDANCE: '/api/attendance'
    },
    DEFAULT_HEADERS: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
}; 