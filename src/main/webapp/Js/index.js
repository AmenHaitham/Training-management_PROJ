class ShortcutButton {
    constructor() {
        this.button = null;
        try {
            const button = document.createElement('button');
            if (button instanceof Node) {
                button.className = 'shortcut-button';
                button.innerHTML = '<i class="fas fa-bolt"></i>';
                this.button = button;
            } else {
                console.warn('Failed to create button element');
            }
        } catch (error) {
            console.warn('Error creating shortcut button:', error);
        }
    }

    init() {
        if (!this.button) {
            console.warn('Button not properly initialized');
            return;
        }

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