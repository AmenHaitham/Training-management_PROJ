class ShortcutButton {
    constructor() {
        this.button = document.createElement('button');
        this.button.className = 'shortcut-button';
        this.button.innerHTML = '<i class="fas fa-bolt"></i>';
    }

    init() {
        const targetElement = document.querySelector('.main-content');
        if (!targetElement) {
            console.warn('Target element not found');
            return;
        }

        try {
            targetElement.appendChild(this.button);
        } catch (error) {
            console.warn('Error appending shortcut button:', error);
        }
    }
}

// Initialize the shortcut button when the DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    const shortcutButton = new ShortcutButton();
    shortcutButton.init();
}); 