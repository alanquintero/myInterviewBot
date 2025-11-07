document.addEventListener('DOMContentLoaded', () => {
    const listItems = document.querySelectorAll('.list-group-item');

    listItems.forEach(item => {
        const link = item.querySelector('a.nav-link');
        if (!link) return;

        // When the link is clicked
        link.addEventListener('click', () => {
            // Remove 'active' class from all items
            listItems.forEach(li => li.classList.remove('active'));

            // Add 'active' class to the clicked item
            item.classList.add('active');
        });
    });

    // Auto-highlight based on current URL
    const currentPath = window.location.pathname;
    listItems.forEach(item => {
        const link = item.querySelector('a.nav-link');
        if (link && link.getAttribute('href') === currentPath) {
            listItems.forEach(li => li.classList.remove('active'));
            item.classList.add('active');
        }
    });
});
