document.getElementById('signupForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();
    
    // UI Elements
    const form = e.target;
    const submitButton = form.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.innerHTML;
    const errorMessage = document.getElementById('errorMessage');
    const successMessage = document.getElementById('successMessage');
    
    // Form Data
    const formData = {
        firstName: document.getElementById('firstName').value.trim(),
        lastName: document.getElementById('lastName').value.trim(),
        email: document.getElementById('email').value.trim(),
        phone: document.getElementById('phone').value.trim(),
        gender: document.getElementById('gender').value,
        address: document.getElementById('address').value.trim(),
        role: document.getElementById('role').value,
        password: document.getElementById('password').value,
        confirmPassword: document.getElementById('confirmPassword').value,
    };

    // Show loading state
    submitButton.disabled = true;
    submitButton.innerHTML = 'Creating account...';
    
    // Clear previous messages
    errorMessage.textContent = '';
    errorMessage.style.display = 'none';
    successMessage.style.display = 'none';

    try {
        // Client-side validation
        validateFormData(formData);

        // Prepare request body
        const requestBody = {
            firstName: formData.firstName,
            lastName: formData.lastName,
            email: formData.email,
            password: formData.password,
            phoneNumber: formData.phone,
            gender: formData.gender,
            address: formData.address,
            role: formData.role
        };

        console.log(requestBody);

        const response = await fetch('http://localhost:1010/auth/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({
                message: 'Registration failed. Please try again.'
            }));
            throw new Error(error.message || 'Registration failed');
        }

        const data = await response.json();
        
        // Show success message
        successMessage.textContent = 'Account created successfully! Redirecting to login...';
        successMessage.style.display = 'block';
        
        // Store tokens if available
        if (data.accessToken) {
            localStorage.setItem('accessToken', data.accessToken);
        }
        if (data.refreshToken) {
            localStorage.setItem('refreshToken', data.refreshToken);
        }
        
        // Reset form and redirect
        form.reset();
        
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);

    } catch (err) {
        console.error('Sign-up error:', err);
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

/**
 * Validate form data before submission
 * @param {Object} formData 
 * @throws {Error} With validation error message
 */
function validateFormData(formData) {
    // Required fields
    const requiredFields = [
        { field: 'firstName', name: 'First Name' },
        { field: 'lastName', name: 'Last Name' },
        { field: 'email', name: 'Email' },
        { field: 'phone', name: 'Phone Number' },
        { field: 'gender', name: 'Gender' },
        { field: 'address', name: 'Address' },
        { field: 'role', name: 'Role' },
        { field: 'password', name: 'Password' },
        { field: 'confirmPassword', name: 'Confirm Password' }
    ];

    for (const { field, name } of requiredFields) {
        if (!formData[field]) {
            throw new Error(`${name} is required`);
        }
    }


    // Email validation
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
        throw new Error('Please enter a valid email address');
    }

    // Phone validation (basic)
    if (!/^[\d\s+\-()]{8,}$/.test(formData.phone)) {
        throw new Error('Please enter a valid phone number');
    }

    // Password validation
    if (formData.password.length < 8) {
        throw new Error('Password must be at least 8 characters');
    }

    if (!/(?=.*[A-Z])/.test(formData.password)) {
        throw new Error('Password must contain at least one uppercase letter');
    }

    if (!/(?=.*\d)/.test(formData.password)) {
        throw new Error('Password must contain at least one number');
    }

    if (!/(?=.*[!@#$%^&*])/.test(formData.password)) {
        throw new Error('Password must contain at least one special character');
    }

    // Password match
    if (formData.password !== formData.confirmPassword) {
        throw new Error('Passwords do not match');
    }
}

// Password visibility toggle
document.querySelectorAll('.toggle-password').forEach(button => {
    button.addEventListener('click', function() {
        const input = this.previousElementSibling;
        const isPassword = input.type === 'password';
        
        input.type = isPassword ? 'text' : 'password';
        this.setAttribute('aria-label', isPassword ? 'Hide password' : 'Show password');
        
        // Toggle SVG icon
        const icon = this.querySelector('svg');
        if (icon) {
            icon.style.display = 'none';
            setTimeout(() => {
                icon.style.display = '';
            }, 10);
        }
    });
});

// Real-time password strength check
document.getElementById('password')?.addEventListener('input', function() {
    const password = this.value;
    const meter = document.querySelector('.strength-meter');
    const text = document.querySelector('.strength-text span');
    const requirements = document.querySelectorAll('.password-requirements li');
    
    if (!password) {
        meter.style.width = '0%';
        meter.style.backgroundColor = 'transparent';
        text.textContent = '';
        return;
    }
    
    // Check requirements
    const hasLength = password.length >= 8;
    const hasUppercase = /[A-Z]/.test(password);
    const hasNumber = /\d/.test(password);
    const hasSpecial = /[!@#$%^&*]/.test(password);
    
    // Update requirement indicators
    requirements.forEach(li => {
        const requirement = li.dataset.requirement;
        const fulfilled = 
            (requirement === 'length' && hasLength) ||
            (requirement === 'uppercase' && hasUppercase) ||
            (requirement === 'number' && hasNumber) ||
            (requirement === 'special' && hasSpecial);
        
        li.classList.toggle('fulfilled', fulfilled);
    });
    
    // Calculate strength
    let strength = 0;
    if (hasLength) strength += 25;
    if (hasUppercase) strength += 25;
    if (hasNumber) strength += 25;
    if (hasSpecial) strength += 25;
    
    // Update meter and text
    meter.style.width = `${strength}%`;
    
    if (strength < 50) {
        meter.style.backgroundColor = 'var(--error)';
        text.textContent = 'Weak';
    } else if (strength < 75) {
        meter.style.backgroundColor = 'var(--warning)';
        text.textContent = 'Moderate';
    } else {
        meter.style.backgroundColor = 'var(--success)';
        text.textContent = 'Strong';
    }
});

// Confirm password match check
document.getElementById('confirmPassword')?.addEventListener('input', function() {
    const password = document.getElementById('password').value;
    const confirmPassword = this.value;
    const matchIndicator = document.querySelector('.password-match');
    
    if (!confirmPassword) {
        matchIndicator.textContent = '';
        return;
    }
    
    if (password === confirmPassword) {
        matchIndicator.textContent = '✓ Passwords match';
        matchIndicator.style.color = 'var(--success)';
    } else {
        matchIndicator.textContent = '✗ Passwords do not match';
        matchIndicator.style.color = 'var(--error)';
    }
});