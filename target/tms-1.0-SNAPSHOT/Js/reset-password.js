// Configuration
const API_BASE_URL = `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.AUTH}`;
const RESET_REDIRECT_DELAY = 2000; // 2 seconds

// State management
let currentStep = 1;
let userEmail = '';
let verificationCode = '';
let resendTimer = null;
let resendCooldown = 60; // 60 seconds cooldown for resend

// DOM Elements
const stepElements = document.querySelectorAll('.step');
const emailInput = document.getElementById('email');
const codeInput = document.getElementById('code');
const newPasswordInput = document.getElementById('newPassword');
const confirmPasswordInput = document.getElementById('confirmNewPassword');
const userEmailElement = document.getElementById('userEmail');
const resendCodeButton = document.getElementById('resendCode');
const passwordStrengthElement = document.getElementById('passwordStrength');
const passwordMatchElement = document.getElementById('passwordMatch');

// Form elements
const requestCodeForm = document.getElementById('requestCodeForm');
const verifyCodeForm = document.getElementById('verifyCodeForm');
const resetPasswordForm = document.getElementById('resetPasswordForm');

// Message elements
const errorMessages = {
    step1: document.getElementById('errorMessage1'),
    step2: document.getElementById('errorMessage2'),
    step3: document.getElementById('errorMessage3')
};
const successMessages = {
    step1: document.getElementById('successMessage1'),
    step3: document.getElementById('successMessage3')
};

// Button loading states
const submitButtons = {
    step1: requestCodeForm.querySelector('.btn-primary'),
    step2: verifyCodeForm.querySelector('.btn-primary'),
    step3: resetPasswordForm.querySelector('.btn-primary')
};

// Initialize the application
function init() {
    setupEventListeners();
    updateResendButtonState();
}

function setupEventListeners() {
    // Step 1: Request verification code
    requestCodeForm.addEventListener('submit', handleRequestCode);
    
    // Step 2: Verify code
    verifyCodeForm.addEventListener('submit', handleVerifyCode);
    resendCodeButton.addEventListener('click', handleResendCode);
    
    // Step 3: Reset password
    resetPasswordForm.addEventListener('submit', handleResetPassword);
    
    // Password validation
    newPasswordInput.addEventListener('input', validatePasswordStrength);
    confirmPasswordInput.addEventListener('input', validatePasswordMatch);
}

// Show/hide steps
function showStep(stepNumber) {
    stepElements.forEach(step => {
        step.hidden = true;
        step.classList.remove('active');
    });
    
    const currentStepElement = document.getElementById(`step${stepNumber}`);
    currentStepElement.hidden = false;
    currentStepElement.classList.add('active');
    
    // Update UI based on step
    if (stepNumber === 2) {
        userEmailElement.textContent = userEmail;
        startResendCooldown();
    }
    
    currentStep = stepNumber;
}

// Toggle loading state for buttons
function setLoading(button, isLoading) {
    const spinner = button.querySelector('.spinner');
    const buttonText = button.querySelector('.button-text');
    
    if (isLoading) {
        button.disabled = true;
        spinner.classList.remove('hidden');
        buttonText.textContent = 'Processing...';
    } else {
        button.disabled = false;
        spinner.classList.add('hidden');
        buttonText.textContent = getButtonText(button);
    }
}

function getButtonText(button) {
    if (button === submitButtons.step1) return 'Send Verification Code';
    if (button === submitButtons.step2) return 'Verify Code';
    if (button === submitButtons.step3) return 'Reset Password';
    return '';
}

// Step 1: Request verification code
async function handleRequestCode(e) {
    e.preventDefault();
    
    userEmail = emailInput.value.trim();
    hideMessages('step1');
    setLoading(submitButtons.step1, true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/request-reset`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email: userEmail })
        });
        
        if (response.ok) {
            verificationCode = ''; // Reset previous code
            successMessages.step1.textContent = 'Verification code sent to your email.';
            showMessage(successMessages.step1);
            showStep(2);
        } else {
            const error = await parseError(response);
            showMessage(errorMessages.step1, error.message);
        }
    } catch (err) {
        console.error('Request code error:', err);
        showMessage(errorMessages.step1, 'An error occurred. Please try again.');
    } finally {
        setLoading(submitButtons.step1, false);
    }
}

// Step 2: Verify code
async function handleVerifyCode(e) {
    e.preventDefault();
    
    verificationCode = codeInput.value.trim();
    hideMessages('step2');
    setLoading(submitButtons.step2, true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/verify-code`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                code: verificationCode
            })
        });
        
        if (response.ok) {
            showStep(3);
        } else {
            const error = await parseError(response);
            showMessage(errorMessages.step2, error.message);
        }
    } catch (err) {
        console.error('Verify code error:', err);
        showMessage(errorMessages.step2, 'An error occurred. Please try again.');
    } finally {
        setLoading(submitButtons.step2, false);
    }
}

// Step 2: Resend code
async function handleResendCode() {
    if (resendTimer) return;
    
    hideMessages('step2');
    resendCodeButton.disabled = true;
    
    try {
        const response = await fetch(`${API_BASE_URL}/request-reset`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email: userEmail })
        });
        
        if (response.ok) {
            showMessage(errorMessages.step2, 'New verification code sent!', 'success');
            startResendCooldown();
        } else {
            const error = await parseError(response);
            showMessage(errorMessages.step2, error.message);
        }
    } catch (err) {
        console.error('Resend code error:', err);
        showMessage(errorMessages.step2, 'An error occurred. Please try again.');
    }
}

// Step 3: Reset password
async function handleResetPassword(e) {
    e.preventDefault();
    
    const newPassword = newPasswordInput.value;
    const confirmPassword = confirmPasswordInput.value;
    hideMessages('step3');
    
    // Client-side validation
    if (newPassword !== confirmPassword) {
        showMessage(errorMessages.step3, 'Passwords do not match');
        return;
    }
    
    const passwordError = validatePasswordRequirements(newPassword);
    if (passwordError) {
        showMessage(errorMessages.step3, passwordError);
        return;
    }
    
    setLoading(submitButtons.step3, true);
    
    try {
        const response = await fetch(`${API_BASE_URL}/reset-password`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: userEmail,
                newPassword: newPassword
            })
        });
        
        if (response.ok) {
            showMessage(successMessages.step3, 'Password reset successfully! Redirecting...');
            
            // Redirect to sign-in
            setTimeout(() => {
                window.location.href = 'signin.html';
            }, RESET_REDIRECT_DELAY);
        } else {
            const error = await parseError(response);
            showMessage(errorMessages.step3, error.message);
        }
    } catch (err) {
        console.error('Reset password error:', err);
        showMessage(errorMessages.step3, 'An error occurred. Please try again.');
    } finally {
        setLoading(submitButtons.step3, false);
    }
}

// Password validation
function validatePasswordStrength() {
    const password = newPasswordInput.value;
    let strength = 'weak';
    
    if (password.length >= 12) {
        strength = 'strong';
    } else if (password.length >= 8) {
        strength = 'medium';
    }
    
    passwordStrengthElement.setAttribute('data-strength', strength);
}

function validatePasswordMatch() {
    const password = newPasswordInput.value;
    const confirmPassword = confirmPasswordInput.value;
    
    if (password && confirmPassword) {
        if (password === confirmPassword) {
            passwordMatchElement.textContent = 'Passwords match';
            passwordMatchElement.classList.add('valid');
        } else {
            passwordMatchElement.textContent = 'Passwords do not match';
            passwordMatchElement.classList.remove('valid');
        }
    } else {
        passwordMatchElement.textContent = '';
    }
}

function validatePasswordRequirements(password) {
    if (password.length < 8) {
        return 'Password must be at least 8 characters long';
    }
    
    if (!/\d/.test(password)) {
        return 'Password must contain at least one number';
    }
    
    if (!/[!@#$%^&*]/.test(password)) {
        return 'Password must contain at least one special character';
    }
    
    return null;
}

// Resend code cooldown
function startResendCooldown() {
    if (resendTimer) {
        clearInterval(resendTimer);
    }
    
    let cooldown = resendCooldown;
    updateResendButtonState(cooldown);
    
    resendTimer = setInterval(() => {
        cooldown--;
        updateResendButtonState(cooldown);
        
        if (cooldown <= 0) {
            clearInterval(resendTimer);
            resendTimer = null;
        }
    }, 1000);
}

function updateResendButtonState(seconds = 0) {
    if (seconds > 0) {
        resendCodeButton.disabled = true;
        resendCodeButton.textContent = `Resend Code (${seconds}s)`;
    } else {
        resendCodeButton.disabled = false;
        resendCodeButton.textContent = 'Resend Code';
    }
}

// Helper functions
async function parseError(response) {
    try {
        const error = await response.json();
        return {
            message: error.message || 'An unexpected error occurred',
            status: response.status
        };
    } catch {
        return {
            message: 'An unexpected error occurred',
            status: response.status
        };
    }
}

function showMessage(element, message, type = 'error') {
    element.textContent = message;
    element.style.display = 'block';
    
    if (type === 'success') {
        element.style.color = '';
        element.style.backgroundColor = '';
    }
}

function hideMessages(step) {
    if (errorMessages[step]) errorMessages[step].style.display = 'none';
    if (successMessages[step]) successMessages[step].style.display = 'none';
}

// Initialize the application
document.addEventListener('DOMContentLoaded', init);