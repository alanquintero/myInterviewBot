import {checkSystemRequirements} from './main.js';
import {loadInterviews} from './my-interviews.js';
import {loadSettings} from './settings.js';

document.addEventListener('DOMContentLoaded', () => {
    const links = document.querySelectorAll('.nav-link[data-page]');
    const pages = document.querySelectorAll('.page');

    // Check system requirements when app starts
    checkSystemRequirements();

    links.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const page = link.dataset.page;

            // Update active link
            links.forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            // Show/hide pages
            pages.forEach(p => p.classList.add('hidden'));
            console.log("Page: ", page);
            document.getElementById(`${page}Page`).classList.remove('hidden');

            // Load interviews only when visiting the interviews page
            if (page === 'home') {
                console.log("Home page");
                checkSystemRequirements();
            }

            // Load interviews only when visiting the interviews page
            if (page === 'myInterviews') {
                console.log("MyInterviews page");
                loadInterviews();
            }

            if (page === "settings") {
                console.log("Settings page");
                loadSettings();
            }
        });
    });
});
