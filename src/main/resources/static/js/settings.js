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
