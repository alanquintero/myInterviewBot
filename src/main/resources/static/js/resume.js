/* Resume section */
const resumeSection = document.getElementById("resumeSection");

document.getElementById("uploadResumeBtn").addEventListener("click", () => {
    const fileInput = document.getElementById("resumeInput");
    const file = fileInput.files[0];
    const feedback = document.getElementById("uploadFeedback");

    if (!file) {
        feedback.textContent = "Please select a file to upload.";
        feedback.className = "text-danger";
        return;
    }

    const formData = new FormData();
    formData.append("resume", file);

    fetch("/upload-resume", {
        method: "POST",
        body: formData
    })
        .then(res => res.json())
        .then(data => {
            feedback.textContent = data.message;
            feedback.className = data.success ? "text-success" : "text-danger";
        })
        .catch(err => {
            console.error(err);
            feedback.textContent = "Error uploading file.";
            feedback.className = "text-danger";
        });
});

