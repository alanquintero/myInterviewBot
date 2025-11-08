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
                    <li class="list-group-item"><strong>AI provider:</strong> ${settings.systemSettings.aiProvider}</li>
                    <li class="list-group-item"><strong>AI model:</strong> ${settings.systemSettings.selectedAiModel || 'N/A'}</li>
                    <li class="list-group-item"><strong>Whisper provider:</strong> ${settings.systemSettings.whisperProvider}</li>
                    <li class="list-group-item"><strong>Operating System:</strong> ${settings.systemSettings.operatingSystem}</li>
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
    // System settings
    const aiModelSelect = document.getElementById('aiModelSelect');
    // App settings
    const defaultProfession = document.getElementById('defaultProfession');
    const showCategorySwitch = document.getElementById('showCategorySwitch');
    const showDifficultySwitch = document.getElementById('showDifficultySwitch');

    const messageContainer = document.getElementById('changeSettingsMessage');
    const alertBox = messageContainer.querySelector('.alert');
    const saveBtn = document.getElementById('saveSettingsBtn');
    const cancelBtn = document.getElementById('cancelSettingsBtn');

    try {
        const response = await fetch('/settings/v1/all');
        const settings = await response.json();

        if (!settings.systemSettings.aiModels || settings.systemSettings.aiModels.length === 0) {
            section.style.display = 'none';
            return;
        }

        // System Settings
        // Populate AI model dropdown
        aiModelSelect.innerHTML = settings.systemSettings.aiModels
            .map(model => `<option value="${model}" ${model === settings.systemSettings.selectedAiModel ? 'selected' : ''}>${model}</option>`)
            .join('');

        // App Settings
        // Populate Profession
        defaultProfession.value = settings.appSettings.defaultProfession ?? 'Software Engineer';
        // Switches
        showCategorySwitch.checked = settings.appSettings.showQuestionCategory;
        showDifficultySwitch.checked = settings.appSettings.showQuestionDifficulty;

        section.style.display = 'block';

        // Save button logic
        saveBtn.addEventListener('click', async () => {
            const updatedSettings = {
                systemSettings: {
                    selectedAiModel: aiModelSelect.value,
                },
                appSettings: {
                    defaultProfession: defaultProfession.value,
                    showQuestionCategory: showCategorySwitch.checked,
                    showDifficultySwitch: showDifficultySwitch.checked,
                }
            };

            try {
                const response = await fetch('/settings/v1/update', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
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
            aiModelSelect.value = settings.systemSettings.selectedAiModel;
            defaultProfession.value = settings.appSettings.defaultProfession ?? 'Software Engineer';
            showCategorySwitch.checked = settings.appSettings.showQuestionCategory;
            showDifficultySwitch.checked = settings.appSettings.showQuestionDifficulty;

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

