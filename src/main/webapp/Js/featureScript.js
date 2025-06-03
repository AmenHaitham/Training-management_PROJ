document.addEventListener('DOMContentLoaded', function() {
    try {
        const shortcutButton = new ShortcutButton();
        shortcutButton.init();
    } catch (error) {
        console.warn('Error initializing shortcut button:', error);
    }
}); 