const clearBtn = document.getElementById('clearInterviewsBtn');
const messageContainer = document.getElementById('clearMessage');
const alertBox = messageContainer.querySelector('.alert');

clearBtn.addEventListener('click', async () => {
    const confirmClear = confirm('Are you sure you want to delete all your saved interviews?');
    if (!confirmClear) return;

    try {
        const response = await fetch('/settings/v1/interviews/clear', {method: 'DELETE'});
        if (response.ok) {
            showMessage('All interviews cleared.', 'success');
        } else {
            showMessage('Failed to clear.', 'danger');
        }
    } catch (err) {
        console.error(err);
        showMessage('Error occurred.', 'danger');
    }
});

function showMessage(text, type) {
    alertBox.textContent = text;
    alertBox.className = `alert alert-${type}`;
    messageContainer.style.display = 'block';
    setTimeout(() => messageContainer.style.display = 'none', 3000);
}

export async function loadSettings() {
    const settingsContainer = document.getElementById('settingsContainer');

    try {
        const response = await fetch('/settings/v1/all');
        let settings = await response.json();

        if (!settings) {
            settingsContainer.innerHTML = '<p>No settings found.</p>';
            return;
        }

        settingsContainer.innerHTML = `
                <div class="card p-3 mt-3 shadow-sm">
                    <ul class="list-group list-group-flush text-start">
                        <li class="list-group-item"><strong>AI provider:</strong> ${settings.aiProvider}</li>
                        <li class="list-group-item"><strong>AI model:</strong> ${settings.aiModel}</li>
                        <li class="list-group-item"><strong>Whisper provider:</strong> ${settings.whisperProvider}</li>
                        <li class="list-group-item"><strong>Operating System:</strong> ${settings.operatingSystem}</li>
                    </ul>
                </div>
            `;
    } catch (err) {
        console.error(err);
        settingsContainer.innerHTML = '<p>Error loading settings.</p>';
    } finally {
    }
}
