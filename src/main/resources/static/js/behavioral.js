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
