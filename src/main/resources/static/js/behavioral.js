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

export async function setupAppSettings() {
    const professionInput = document.getElementById('inputProfession');
    const inputProfessionLabel = document.getElementById('inputProfessionLabel');
    const categorySection = document.querySelector('#categorySelect')?.closest('.mb-4');
    const difficultySection = document.querySelector('#difficultySelect')?.closest('.mb-4');

    try {
        const response = await fetch('/settings/v1/app/all');
        if (!response.ok) {
            console.error('Failed to load app settings');
            return;
        }

        const appSettings = await response.json();

        // 1️⃣ Default Profession
        if (appSettings.defaultProfession && professionInput) {
            // Only set if input is empty, so user input isn’t overridden
            if (!professionInput.value.trim()) {
                professionInput.value = appSettings.defaultProfession;
                professionInput.placeholder = "e.g. " + appSettings.defaultProfession;
                inputProfessionLabel.textContent = "Profession (default: " + appSettings.defaultProfession + ")";
            }
        }

        // 2️⃣ Show/Hide Question Category
        if (categorySection) {
            categorySection.style.display = appSettings.showQuestionCategory ? 'block' : 'none';
        }

        // 3️⃣ Show/Hide Question Difficulty
        if (difficultySection) {
            difficultySection.style.display = appSettings.showQuestionDifficulty ? 'block' : 'none';
        }

        console.log('App settings loaded:', appSettings);

    } catch (err) {
        console.error('Error loading app settings:', err);
    }
}
