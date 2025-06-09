// Wait for DOM to be loaded
document.addEventListener('DOMContentLoaded', function() {
    // Check if ShortcutButton is available
    if (typeof ShortcutButton === 'undefined') {
        console.warn('ShortcutButton class is not defined');
        return;
    }

    try {
        const shortcutButton = new ShortcutButton();
        shortcutButton.init();
    } catch (error) {
        console.warn('Error initializing shortcut button:', error);
    }
}); 