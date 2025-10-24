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
            console.log("Page: ", page);
            document.getElementById(`${page}Page`).classList.remove('hidden');

            // Load interviews only when visiting the interviews page
            if (page === 'myInterviews') {
                console.log("MyInterviews page");
                loadInterviews();
            }
        });
    });
});

async function loadInterviews() {
    const interviewsList = document.getElementById('interviewsList');
    const historyLoading = document.getElementById('historyLoading');
    interviewsList.innerHTML = "";
    historyLoading.classList.remove('hidden');

    try {
        const response = await fetch('/history/v1/all');
        const interviews = await response.json();
        historyLoading.classList.add('hidden');

        if (!interviews.length) {
            interviewsList.innerHTML = '<p>No interviews found.</p>';
            return;
        }

        interviewsList.innerHTML = '';

        interviews.forEach(interview => {
            const card = document.createElement('div');
            card.className = 'card mb-4 p-3';
            const date = new Date(interview.timestamp);
            const formattedTime = date.toLocaleString();

            card.innerHTML = `
                <p><strong>Date:</strong> ${formattedTime}</p>
                <p><strong>Profession:</strong> ${interview.profession}</p>
                <p><strong>Question:</strong> ${interview.question}</p>
                <p><strong>Answer:</strong> ${interview.answer}</p>
                <p><strong>Feedback:</strong> ${interview.feedback}</p>
                <video controls src="${interview.videoUrl}" width="320" height="240"></video>
                <button class="btn btn-danger btn-sm delete-btn" data-id="${interview.timestamp}">
                    Delete
                </button>
            `;

            interviewsList.appendChild(card);
        });

        // Attach delete listeners
        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const id = e.target.dataset.id;
                const confirmDelete = confirm('Are you sure you want to delete this interview?');
                if (!confirmDelete) return;

                try {
                    const res = await fetch(`/history/v1/delete/${id}`, {method: 'DELETE'});
                    if (res.ok) {
                        alert('Interview deleted successfully');
                        loadInterviews(); // reload after delete
                    } else {
                        alert('Failed to delete interview');
                    }
                } catch (err) {
                    console.error(err);
                    alert('Error deleting interview');
                }
            });
        });

    } catch (err) {
        console.error(err);
        interviewsList.innerHTML = '<p>Error loading interviews.</p>';
    }
}
