class ShortcutButton {
    constructor() {
        this.button = null;
    }

    init() {
        const targetElement = document.querySelector('.main-content');
        if (!targetElement) {
            console.warn('Target element not found');
            return;
        }

        try {
            // Create a new button element
            this.button = document.createElement('button');
            if (!(this.button instanceof Node)) {
                throw new Error('Failed to create button element');
            }
            
            this.button.className = 'shortcut-button';
            this.button.innerHTML = '<i class="fas fa-bolt"></i>';
            
            // Append the new button
            targetElement.appendChild(this.button);
        } catch (error) {
            console.warn('Error appending shortcut button:', error);
        }
    }
}

// Export the class for use in other files
window.ShortcutButton = ShortcutButton;

// Initialize the shortcut button when the DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    const shortcutButton = new ShortcutButton();
    shortcutButton.init();
}); 