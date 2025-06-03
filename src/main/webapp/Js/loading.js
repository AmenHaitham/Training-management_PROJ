class LoadingIndicator {
    constructor() {
        this.overlay = document.createElement('div');
        this.overlay.className = 'loading-overlay';
        this.overlay.innerHTML = `
            <div class="loading-spinner"></div>
            <div class="loading-text">Loading...</div>
        `;
        document.body.appendChild(this.overlay);
    }

    show(message = 'Loading...') {
        this.overlay.querySelector('.loading-text').textContent = message;
        this.overlay.classList.add('active');
    }

    hide() {
        this.overlay.classList.remove('active');
    }
}

// Create a global instance
const loadingIndicator = new LoadingIndicator();

// Add loading indicator to fetch requests
const originalFetch = window.fetch;
window.fetch = function() {
    loadingIndicator.show();
    return originalFetch.apply(this, arguments)
        .then(response => {
            loadingIndicator.hide();
            return response;
        })
        .catch(error => {
            loadingIndicator.hide();
            throw error;
        });
}; 