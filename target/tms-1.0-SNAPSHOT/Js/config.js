// Define API_CONFIG
const API_CONFIG = {
    get BASE_URL() {
        const protocol = window.location.protocol;
        const hostname = window.location.hostname;
        const port = window.location.port;
        return `${protocol}//${hostname}${port ? ':' + port : ''}`;
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
        ATTENDANCE: '/api/attendances',
        FEEDBACKS: '/api/feedbacks'
    },
    DEFAULT_HEADERS: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
};

// Export API_CONFIG to window object
window.API_CONFIG = API_CONFIG;

// Log API_CONFIG for debugging
console.log('API_CONFIG initialized:', API_CONFIG); 