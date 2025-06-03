document.getElementById('signinForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();
    
    // UI Elements
    const form = e.target;
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const errorMessage = document.getElementById('errorMessage');
    const submitButton = form.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.innerHTML;
    
    // Show loading state
    submitButton.disabled = true;
    submitButton.innerHTML = 'Signing in...';
    
    // Clear previous errors
    errorMessage.textContent = '';
    errorMessage.style.display = 'none';
    emailInput.classList.remove('input-error');
    passwordInput.classList.remove('input-error');

    try {
        // Basic client-side validation
        if (!emailInput.value || !passwordInput.value) {
            throw new Error('Please fill in all fields');
        }

        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailInput.value)) {
            emailInput.classList.add('input-error');
            throw new Error('Please enter a valid email address');
        }

        if (passwordInput.value.length < 8) {
            passwordInput.classList.add('input-error');
            throw new Error('Password must be at least 8 characters');
        }

        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.AUTH}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                email: emailInput.value.trim(),
                password: passwordInput.value
            }),
            credentials: 'include' // For cookies if using HTTP-only tokens
        });
        
        if (!response.ok) {
            const error = await response.json().catch(() => ({
                message: 'Invalid email or password'
            }));
            throw new Error(error.message);
        }

        const data = await response.json();
        
        // Secure token storage
        if (data.accessToken) {
            localStorage.setItem('accessToken', data.accessToken);
        }
        if (data.refreshToken) {
            localStorage.setItem('refreshToken', data.refreshToken);
        }
        if (data.user) {
            localStorage.setItem('user', JSON.stringify(data.user));
        }

        // Role-based redirection with timeout for better UX
        setTimeout(() => {
            switch(data.user?.role) {
                case 'ADMIN':
                    window.location.href = 'adminDashboard.html';
                    break;
                case 'TRAINEE':
                    window.location.href = 'userDashboard.html';
                    break;
                default:
                    window.location.href = 'dashboard.html';
            }
        }, 500);

    } catch (err) {
        console.error('Sign-in error:', err);
        errorMessage.textContent = err.message || 'An error occurred. Please try again.';
        errorMessage.style.display = 'block';
        
        // Scroll to error for better UX
        errorMessage.scrollIntoView({ behavior: 'smooth', block: 'center' });
    } finally {
        // Reset button state
        submitButton.disabled = false;
        submitButton.innerHTML = originalButtonText;
    }
});