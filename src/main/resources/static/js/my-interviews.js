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

        if (!interviewsData.entries.length) {
            listContainer.innerHTML = '<p>No interviews found.</p>';
            return;
        }

        // Displaying Score summary
        renderScoreSummary(interviewsData.scoreSummary);

        const totalPages = Math.ceil(interviewsData.entries.length / PAGE_SIZE);
        const start = (page - 1) * PAGE_SIZE;
        const end = start + PAGE_SIZE;
        const pageItems = interviewsData.entries.slice(start, end);

        // Render interviews
        pageItems.forEach(interview => {
            const card = document.createElement('div');
            card.className = 'card mb-4 p-3';
            const date = new Date(interview.timestamp);
            const formattedTime = date.toLocaleString();

            /* Evaluation start */
            // Clarity
            const clarityScore = interview.evaluation?.clarityScore ?? "N/A";
            const clarityFeedback = interview.evaluation?.clarityFeedback && interview.evaluation.clarityFeedback.trim() !== '' ? interview.evaluation.clarityFeedback : "No feedback provided";

            // Structure
            const structureScore = interview.evaluation?.structureScore ?? "N/A";
            const structureFeedback = interview.evaluation?.structureFeedback && interview.evaluation.structureFeedback.trim() !== '' ? interview.evaluation.structureFeedback : "No feedback provided";

            // Relevance
            const relevanceScore = interview.evaluation?.relevanceScore ?? "N/A";
            const relevanceFeedback = interview.evaluation?.relevanceFeedback && interview.evaluation.relevanceFeedback.trim() !== '' ? interview.evaluation.relevanceFeedback : "No feedback provided";

            // Communication
            const communicationScore = interview.evaluation?.communicationScore ?? "N/A";
            const communicationFeedback = interview.evaluation?.communicationFeedback && interview.evaluation.communicationFeedback.trim() !== '' ? interview.evaluation.communicationFeedback : "No feedback provided";

            // Depth
            const depthScore = interview.evaluation?.depthScore ?? "N/A";
            const depthFeedback = interview.evaluation?.depthFeedback && interview.evaluation.depthFeedback.trim() !== '' ? interview.evaluation.depthFeedback : "No feedback provided";
            /* Evaluation ends */

            card.innerHTML = `
                <p><strong>Date:</strong> ${formattedTime}</p>
                <p><strong>Profession:</strong> ${interview.profession}</p>
                
                ${interview.question?.question ? `<p><strong>Question:</strong> ${interview.question.question}</p>` : ''}
                ${interview.question?.category ? `<p><strong>Category:</strong> ${interview.question.category}</p>` : ''}
                ${interview.question?.difficulty ? `<p><strong>Difficulty:</strong> ${interview.question.difficulty}</p>` : ''}
                
                <p><strong>Answer:</strong> ${interview.answer}</p>
                <p><strong>Feedback:</strong> ${interview.feedback}</p>
            
                ${interview.evaluation ? `
                <div class="evaluation mt-2">
                    <p><strong>Evaluation:</strong></p>
                    <ul class="list-unstyled ms-3">
                        <li><strong>Clarity</strong> (${clarityScore}/10): ${clarityFeedback}</li>
                        <li><strong>Structure</strong> (${structureScore}/10): ${structureFeedback}</li>
                        <li><strong>Relevance</strong> (${relevanceScore}/10): ${relevanceFeedback}</li>
                        <li><strong>Communication</strong> (${communicationScore}/10): ${communicationFeedback}</li>
                        <li><strong>Depth</strong> (${depthScore}/10): ${depthFeedback}</li>
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

function renderScoreSummary(scoreSummary) {
    if (!scoreSummary) {
        return;
    }

    const summaryContainer = document.getElementById('scoreSummary');
    summaryContainer.innerHTML = ''; // clear previous content

    // Add title dynamically
    const title = document.createElement('h4');
    title.innerText = "Average Evaluation Scores";
    summaryContainer.appendChild(title);

    // Add list for the scores
    const list = document.createElement('ul');
    list.className = 'list-group list-group-horizontal justify-content-center';

    const keys = [
        {key: 'clarityScoreAverage', label: 'Clarity'},
        {key: 'structureScoreAverage', label: 'Structure'},
        {key: 'relevanceScoreAverage', label: 'Relevance'},
        {key: 'communicationScoreAverage', label: 'Communication'},
        {key: 'depthScoreAverage', label: 'Depth'}
    ];

    keys.forEach(k => {
        const value = scoreSummary[k.key] ?? 0;
        const item = document.createElement('li');
        item.className = 'list-group-item';
        item.innerHTML = `<strong>${k.label}:</strong> ${value.toFixed(1)}/10`;
        list.appendChild(item);
    });

    summaryContainer.appendChild(list);
}
