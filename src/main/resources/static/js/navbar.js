document.addEventListener('DOMContentLoaded', () => {
    const navItems = document.querySelectorAll('.navbar-nav .nav-item');

    navItems.forEach(item => {
        const link = item.querySelector('a.nav-link');
        if (!link) return;

        // When a nav link is clicked
        link.addEventListener('click', () => {
            // Remove 'active' from all nav items
            navItems.forEach(nav => nav.classList.remove('active'));

            // Add 'active' to the clicked one
            item.classList.add('active');
        });
    });

    // Auto-highlight based on current URL
    const currentPath = window.location.pathname;
    navItems.forEach(item => {
        const link = item.querySelector('a.nav-link');
        if (link && link.getAttribute('href') === currentPath) {
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');
        }
    });
});
