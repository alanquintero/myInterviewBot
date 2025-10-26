export async function loadInterviews(page = 1) {
    const listContainer = document.getElementById('interviewsList');
    const paginationTop = document.getElementById('paginationTop');
    const paginationBottom = document.getElementById('paginationBottom');
    const loading = document.getElementById('historyLoading');
    const PAGE_SIZE = 10;
    let currentPage = 1;
    let interviewsData = [];

    loading.classList.remove('hidden');
    listContainer.innerHTML = '';
    paginationTop.innerHTML = '';
    paginationBottom.innerHTML = '';

    try {
        const response = await fetch('/history/v1/all');
        interviewsData = await response.json();

        if (!interviewsData.length) {
            listContainer.innerHTML = '<p>No interviews found.</p>';
            return;
        }

        const totalPages = Math.ceil(interviewsData.length / PAGE_SIZE);
        const start = (page - 1) * PAGE_SIZE;
        const end = start + PAGE_SIZE;
        const pageItems = interviewsData.slice(start, end);

        // Render interviews
        pageItems.forEach(interview => {
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
            
                ${interview.evaluation ? `
                <div class="evaluation mt-2">
                    <p><strong>Evaluation:</strong></p>
                    <ul class="list-unstyled ms-3">
                        <li><strong>Clarity (${interview.evaluation.clarityScore}/10):</strong> ${interview.evaluation.clarityFeedback}</li>
                        <li><strong>Structure (${interview.evaluation.structureScore}/10):</strong> ${interview.evaluation.structureFeedback}</li>
                        <li><strong>Relevance (${interview.evaluation.relevanceScore}/10):</strong> ${interview.evaluation.relevanceFeedback}</li>
                        <li><strong>Communication (${interview.evaluation.communicationScore}/10):</strong> ${interview.evaluation.communicationFeedback}</li>
                        <li><strong>Depth (${interview.evaluation.depthScore}/10):</strong> ${interview.evaluation.depthFeedback}</li>
                    </ul>
                </div>
                ` : ''}
            
                <video controls src="${interview.videoUrl}" width="320" height="240"></video>
                <button class="btn btn-danger btn-sm delete-btn" data-id="${interview.timestamp}">Delete</button>
            `;

            listContainer.appendChild(card);
        });

        // Helper to create pagination buttons
        const createPagination = (container) => {
            for (let i = 1; i <= totalPages; i++) {
                const li = document.createElement('li');
                li.className = `page-item ${i === page ? 'active' : ''}`;
                li.innerHTML = `<a class="page-link" href="#">${i}</a>`;
                li.addEventListener('click', (e) => {
                    e.preventDefault();
                    loadInterviews(i);
                });
                container.appendChild(li);
            }
        };

        // Render pagination at top and bottom
        createPagination(paginationTop);
        createPagination(paginationBottom);

        // Attach delete listeners
        document.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const id = e.target.dataset.id;
                if (!confirm('Are you sure you want to delete this interview?')) return;

                try {
                    const res = await fetch(`/history/v1/delete/${id}`, {method: 'DELETE'});
                    if (res.ok) {
                        alert('Interview deleted successfully');
                        loadInterviews(currentPage);
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
        listContainer.innerHTML = '<p>Error loading interviews.</p>';
    } finally {
        loading.classList.add('hidden');
        currentPage = page;
    }
}