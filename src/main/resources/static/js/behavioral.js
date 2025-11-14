import {appState} from "./globalState.js";

document.addEventListener('DOMContentLoaded', async () => {
    const categorySelect = document.getElementById('categorySelect');
    const difficultySelect = document.getElementById('difficultySelect');
    const categoryInfoBtn = document.getElementById('categoryInfoBtn');
    const difficultyInfoBtn = document.getElementById('difficultyInfoBtn');
    const infoModal = new bootstrap.Modal(document.getElementById('infoQuestionModal'));
    const infoModalLabel = document.getElementById('infoQuestionModalLabel');
    const infoDescription = document.getElementById('infoQuestionDescription');
    const infoExample = document.getElementById('infoQuestionExample');

    let categories = [];
    let difficultyLevels = [];

    console.log("Loading questions info...");

    try {
        const res = await fetch('/question/v1/behavior/info');
        const data = await res.json();

        categories = data.categories || [];
        difficultyLevels = data.difficultyLevels || [];

        // --- Category dropdown ---
        // Add default "None" option
        const defaultCatOpt = document.createElement('option');
        defaultCatOpt.value = "";
        defaultCatOpt.textContent = "— None —";
        categorySelect.appendChild(defaultCatOpt);

        categories.forEach(cat => {
            const opt = document.createElement('option');
            opt.value = cat.name;
            opt.textContent = cat.displayName;
            categorySelect.appendChild(opt);
        });

        // --- Difficulty dropdown ---
        const defaultDiffOpt = document.createElement('option');
        defaultDiffOpt.value = "";
        defaultDiffOpt.textContent = "— None —";
        difficultySelect.appendChild(defaultDiffOpt);

        difficultyLevels.forEach(diff => {
            const opt = document.createElement('option');
            opt.value = diff.name;
            opt.textContent = diff.displayName;
            difficultySelect.appendChild(opt);
        });

        console.log("Dropdowns loaded");
    } catch (e) {
        console.error('Failed to fetch behavior info:', e);
    }

    // --- Category info button logic ---
    categorySelect.addEventListener('change', () => {
        categoryInfoBtn.disabled = !categorySelect.value;
    });

    categoryInfoBtn.addEventListener('click', () => {
        const selected = categories.find(c => c.name === categorySelect.value);
        if (!selected) return;

        infoModalLabel.textContent = `${selected.displayName} Category`;
        infoDescription.textContent = selected.description || "No description available.";
        infoExample.textContent = selected.example || "No example available.";
        infoModal.show();
    });

    // --- Difficulty info button logic ---
    difficultySelect.addEventListener('change', () => {
        difficultyInfoBtn.disabled = !difficultySelect.value;
    });

    difficultyInfoBtn.addEventListener('click', () => {
        const selected = difficultyLevels.find(d => d.name === difficultySelect.value);
        if (!selected) return;

        infoModalLabel.textContent = `${selected.displayName} Difficulty`;
        infoDescription.textContent = selected.description || "No description available.";
        infoExample.textContent = selected.example || "No example available.";
        infoModal.show();
    });
});

export async function setupSettings() {
    const professionInput = document.getElementById('inputProfession');
    const inputProfessionLabel = document.getElementById('inputProfessionLabel');
    const categorySection = document.querySelector('#categorySelect')?.closest('.mb-4');
    const difficultySection = document.querySelector('#difficultySelect')?.closest('.mb-4');
    const savedQuestionSection = document.querySelector('#savedQuestionSelect')?.closest('.mb-4');
    const saveQuestionBtn = document.getElementById('saveQuestionBtn');

    try {
        const response = await fetch('/settings/v1/all');
        if (!response.ok) {
            console.error('Failed to load app settings');
            return;
        }

        const settings = await response.json();

        const systemSettings = settings.systemSettings;
        const appSettings = settings.appSettings;

        // Recording mode
        appState.recordingMode = systemSettings.recordingMode;
        if (navigator.mediaDevices && navigator.mediaDevices.enumerateDevices) {
            const devices = await navigator.mediaDevices.enumerateDevices();
            const hasCamera = devices.some(d => d.kind === 'videoinput');
            if (!hasCamera) {
                // Automatically force audio-only mode
                appState.recordingMode = 'audio';
            }
        }

        // Default Profession
        if (appSettings.defaultProfession && professionInput) {
            // Only set if input is empty, so user input isn’t overridden
            if (!professionInput.value.trim()) {
                professionInput.value = appSettings.defaultProfession;
                professionInput.placeholder = "e.g. " + appSettings.defaultProfession;
                inputProfessionLabel.textContent = "Profession (default: " + appSettings.defaultProfession + ")";
            }
        }

        // Show/Hide Question Category
        if (categorySection) {
            categorySection.style.display = appSettings.showQuestionCategory ? 'block' : 'none';
        }

        // Show/Hide Question Difficulty
        if (difficultySection) {
            difficultySection.style.display = appSettings.showQuestionDifficulty ? 'block' : 'none';
        }

        // Show/Hide Saved Questions
        if (savedQuestionSection) {
            savedQuestionSection.style.display = appSettings.showSavedQuestions ? 'block' : 'none';
            if (appSettings.showSavedQuestions) {
                saveQuestionBtn.classList.remove("d-none");
            } else {
                saveQuestionBtn.classList.add("d-none");
            }
        }

        console.log('App settings loaded:', appSettings);

    } catch (err) {
        console.error('Error loading app settings:', err);
    }
}
