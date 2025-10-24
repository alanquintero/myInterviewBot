document.addEventListener('DOMContentLoaded', () => {
    const links = document.querySelectorAll('.nav-link[data-page]');
    const pages = document.querySelectorAll('.page');

    links.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const page = link.dataset.page;

            // Update active link
            links.forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            // Show/hide pages
            pages.forEach(p => p.classList.add('hidden'));
            document.getElementById(`${page}Page`).classList.remove('hidden');
        });
    });
});
