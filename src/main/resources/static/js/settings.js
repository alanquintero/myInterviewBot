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
            <label>System settings</label>
                <ul class="list-group list-group-flush text-start">
                    <li class="list-group-item"><strong>AI provider:</strong> ${settings.aiProvider}</li>
                    <li class="list-group-item"><strong>AI model:</strong> ${settings.selectedAiModel || 'N/A'}</li>
                    <li class="list-group-item"><strong>Whisper provider:</strong> ${settings.whisperProvider}</li>
                    <li class="list-group-item"><strong>Operating System:</strong> ${settings.operatingSystem}</li>
                </ul>
            </div>
        `;
    } catch (err) {
        console.error(err);
        settingsContainer.innerHTML = '<p>Error loading settings.</p>';
    }
}

export async function setupChangeSettingsSection() {
    const section = document.getElementById('changeSettingsSection');
    const aiModelSelect = document.getElementById('aiModelSelect');
    const messageContainer = document.getElementById('changeSettingsMessage');
    const alertBox = messageContainer.querySelector('.alert');
    const saveBtn = document.getElementById('saveSettingsBtn');
    const cancelBtn = document.getElementById('cancelSettingsBtn');

    try {
        const response = await fetch('/settings/v1/all');
        const settings = await response.json();

        if (!settings.aiModels || settings.aiModels.length === 0) {
            section.style.display = 'none';
            return;
        }

        // Populate AI model dropdown
        aiModelSelect.innerHTML = settings.aiModels
            .map(model => `<option value="${model}" ${model === settings.selectedAiModel ? 'selected' : ''}>${model}</option>`)
            .join('');

        section.style.display = 'block';

        // Save button logic
        saveBtn.addEventListener('click', async () => {
            const updatedSettings = {
                selectedAiModel: aiModelSelect.value,
                // Add more fields here later when you expand settings
            };

            try {
                const response = await fetch('/settings/v1/update', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(updatedSettings),
                });

                if (response.ok) {
                    showMessage('Settings updated successfully.', 'success');
                } else {
                    showMessage('Failed to update settings.', 'danger');
                }
            } catch (err) {
                console.error(err);
                showMessage('Error occurred while updating settings.', 'danger');
            }
        });

        // Cancel button logic
        cancelBtn.addEventListener('click', () => {
            aiModelSelect.value = settings.selectedAiModel;
            showMessage('Changes canceled.', 'secondary');
        });

        function showMessage(text, type) {
            alertBox.textContent = text;
            alertBox.className = `alert alert-${type}`;
            messageContainer.style.display = 'block';
            setTimeout(() => (messageContainer.style.display = 'none'), 2500);
        }
    } catch (err) {
        console.error('Error loading settings section:', err);
        section.style.display = 'none';
    }
}

