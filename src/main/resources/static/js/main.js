// Profession
const professionEl = document.getElementById("profession");
const professionCard = document.getElementById("professionCard");
const professionCardEl = document.getElementById("selectedProfession");

// Question
const getQuestionBtn = document.getElementById("getQuestionBtn");
const questionEl = document.getElementById("question");
const questionCard = document.getElementById("questionCard");
const anotherQuestionCard = document.getElementById("anotherQuestionCard");
const getAnotherQuestionBtn = document.getElementById("getAnotherQuestionBtn");

// Recording
const recordingContainer = document.getElementById("recordingContainer");
const recordingSection = document.getElementById("recordingSection");
const videoEl = document.getElementById("video");
const videoPlaceholder = document.getElementById("videoPlaceholder");
const recordBtn = document.getElementById("recordBtn");
const recordBtnImg = document.getElementById("recordBtnImg");
const recordLabel = document.getElementById("recordLabel");
const recordingOverlay = document.getElementById("recordingOverlay");
const recordingIndicator = document.getElementById("recordingIndicator");
const recordingTimer = document.getElementById("recordingTimer");
const playbackSection = document.getElementById("playbackSection");
const playbackEl = document.getElementById("playback");
const sendResponseBtn = document.getElementById("sendResponseBtn");

// Feedback
const feedbackContainer = document.getElementById("feedbackContainer");
const generateFeedbackSection = document.getElementById("generateFeedbackSection");
const feedbackEl = document.getElementById("feedback");
const feedbackSection = document.getElementById("feedbackSection");
const feedbackSpinner = document.getElementById("feedbackLoading");
const transcriptSection = document.getElementById("transcriptSection");
const transcriptEl = document.getElementById("transcript");

// Reset
const resetSectionUp = document.getElementById("resetSectionUp");
const resetButtonUp = document.getElementById("resetBtnUp");
const resetSectionDown = document.getElementById("resetSectionDown");
const resetButtonDown = document.getElementById("resetBtnDown");

const MAX_RECORDING_TIME = 150; // in seconds (2 minutes 30 seconds)
let timerInterval;
let mediaRecorder;
let currentStream = null;
let recordedChunks = [];
let isRecording = false;

// Request a question
getQuestionBtn.addEventListener("click", async () => {
    console.log("Generating Question");
    const profession = professionEl.value || "Software Engineer";
    console.log("Profession: ", profession);

    // Hide elements and show loading GIF
    hideElements();
    document.getElementById("questionLoading").classList.remove("hidden");

    await generateQuestion(profession);
});

getAnotherQuestionBtn.addEventListener("click", async () => {
    console.log("Generating Another Question");
    const profession = professionCardEl.textContent || "Software Engineer";
    console.log("Profession: ", profession);

    // Hide elements and show loading GIF
    questionEl.textContent = "...";
    recordingContainer.classList.add("hidden");
    document.getElementById("questionLoading").classList.remove("hidden");

    await generateQuestion(profession);
});

async function generateQuestion(profession) {
    try {
        const res = await fetch(`/api/v1/question?profession=${encodeURIComponent(profession)}`);
        const data = await res.json();
        questionEl.textContent = data.question || "No question returned";
        // show question card
        questionCard.classList.remove("hidden");
        anotherQuestionCard.classList.remove("hidden");

        if (!data.question || data.question.trim() === '') {
            alert('No question was generated. Please try again.');
        } else {
            professionCard.classList.remove("hidden");
            professionCardEl.textContent = profession;
            resetSectionUp.classList.remove("hidden");
            showRecordingSection();
        }
    } catch (err) {
        console.error(err);
        questionEl.textContent = "Error fetching question";
    } finally {
        document.getElementById("questionLoading").classList.add("hidden"); // hide loading GIF
    }
}

resetButtonDown.addEventListener("click", async () => {
    console.log("Click on reset button down");
    reset();
});

resetButtonUp.addEventListener("click", async () => {
    console.log("Click on reset button up");
    reset();
});

function reset() {
    hideElements();
    stopCamera();
    // Reset other UI states
    recordedChunks = [];
}

sendResponseBtn.addEventListener("click", async () => {
    if (recordedChunks.length === 0) {
        console.log("No recording found.");
        return;
    }
    console.log("Generating feedback...");
    const blob = new Blob(recordedChunks, {type: "video/webm"});
    await sendVideo(blob);
});

recordBtn.addEventListener("click", async () => {
    if (!isRecording) {
        console.log("Click on Start recording...");
        // Hide Playback and Generate Feedback sections
        playbackSection.classList.add("hidden");
        generateFeedbackSection.classList.add("hidden");

        // Start Recording
        try {
            if (!currentStream) {
                console.log("currentStream is null!");
                return;
            }
            recordedChunks = [];
            mediaRecorder = new MediaRecorder(currentStream, {mimeType: 'video/webm; codecs=vp8,opus'});

            mediaRecorder.ondataavailable = e => {
                if (e.data.size > 0) recordedChunks.push(e.data);
            };

            mediaRecorder.onstop = () => {
                console.log("Stop recording...");
                const blob = new Blob(recordedChunks, {type: "video/webm"});
                playbackEl.src = URL.createObjectURL(blob);

                // Show Playback and Generate Feedback sections
                playbackSection.classList.remove("hidden");
                generateFeedbackSection.classList.remove("hidden");
            };

            mediaRecorder.start();
            isRecording = true;
            recordBtnImg.src = "img/button/stop.gif";
            recordLabel.textContent = "";
            startRecordingUI(); // countdown and gif handled here

        } catch (err) {
            console.error(err);
            alert("Failed to start recording. Make sure microphone and camera are allowed.");
        }
    } else {
        console.log("Click on Stop recording...");
        // Stop Recording manually
        if (mediaRecorder && mediaRecorder.state === "recording") {
            mediaRecorder.stop();
        }
        isRecording = false;
        recordBtnImg.src = "img/button/record.png";
        recordLabel.textContent = "Record again";
        stopRecordingUI();
    }
});

function showRecordingSection() {
    console.log("Showing recording section...");
    recordingSection.classList.remove("hidden");
    recordingContainer.classList.remove("hidden");
    recordBtn.disabled = false;
    videoPlaceholder.classList.remove("hidden");

    // Add fade-in effect
    recordingContainer.classList.add("fade-in");
    setTimeout(() => {
        recordingContainer.classList.add("show");
    }, 500); // small delay to trigger CSS transition

    initCamera(); // start camera preview immediately
}

async function initCamera() {
    console.log("Init camera...");
    try {
        videoPlaceholder.classList.add("hidden");
        currentStream = await navigator.mediaDevices.getUserMedia({video: true, audio: true});
        videoEl.srcObject = currentStream;
        videoEl.play(); // start preview
        videoEl.classList.remove("hidden");
    } catch (err) {
        console.error("Could not access camera/microphone:", err);
        alert("Please allow camera and microphone access.");
    }
}

function stopCamera() {
    console.log("Stop camera...");
    videoEl.pause();
    if (currentStream) {
        currentStream.getTracks().forEach(track => track.stop());
        currentStream = null;
    }
    videoEl.srcObject = null; // remove reference
    videoEl.classList.add("hidden"); // hide video
    videoPlaceholder.classList.remove("hidden"); // show placeholder
}

// Send recorded video to the backend
async function sendVideo(blob) {
    console.log("Sending video...");
    const formData = new FormData();
    formData.append("file", blob, "answer.webm");
    formData.append("profession", professionCardEl.textContent)
    formData.append("question", questionEl.textContent)

    feedbackSpinner.style.display = "inline-block";
    sendResponseBtn.disabled = true;
    recordBtn.disabled = true;
    recordLabel.textContent = "";
    stopCamera();

    try {
        const res = await fetch("/api/v1/feedback", {
            method: "POST",
            body: formData
        });
        const data = await res.json();

        transcriptSection.classList.remove("hidden");
        feedbackContainer.classList.remove("hidden");
        feedbackSection.classList.remove("hidden");
        resetSectionDown.classList.remove("hidden");

        if (!data.feedback || data.feedback.trim() === '') {
            alert('No feedback was generated. Please try again.');
        }

        transcriptEl.innerText = data.transcript || "";
        feedbackEl.innerText = data.feedback || "No feedback returned";
    } catch (err) {
        console.error(err);
        feedbackEl.innerText = "Error sending video for feedback";
    } finally {
        feedbackSpinner.style.display = "none";
    }
}

function startRecordingUI() {
    console.log("start Recording UI...");
    recordingIndicator.classList.remove("hidden");
    recordingTimer.classList.remove("hidden");
    recordingOverlay.classList.add("active");

    let remaining = MAX_RECORDING_TIME; // 150 seconds
    updateTimerDisplay(remaining);

    timerInterval = setInterval(() => {
        remaining--;
        updateTimerDisplay(remaining);

        if (remaining <= 0) {
            // Stop recording automatically
            if (mediaRecorder && mediaRecorder.state === "recording") {
                mediaRecorder.stop();
            }
            isRecording = false;
            recordBtnImg.src = "img/button/record.png";
            recordLabel.textContent = "Record again";
            stopRecordingUI();
            clearInterval(timerInterval);
        }
    }, 1000);
}

function stopRecordingUI() {
    console.log("stop Recording UI...");
    recordingIndicator.classList.add("hidden");
    recordingTimer.classList.add("hidden");
    recordingOverlay.classList.remove("active");
    sendResponseBtn.disabled = false;
    clearInterval(timerInterval);
}

function updateTimerDisplay(seconds) {
    const minutes = Math.floor(seconds / 60).toString().padStart(2, "0");
    const secs = (seconds % 60).toString().padStart(2, "0");
    recordingTimer.textContent = `${minutes}:${secs}`;
}

function hideElements() {
    console.log("hide elements...");
    questionCard.classList.add("hidden");
    anotherQuestionCard.classList.add("hidden");
    recordingSection.classList.add("hidden");
    recordingContainer.classList.add("hidden");
    playbackSection.classList.add("hidden");
    generateFeedbackSection.classList.add("hidden");
    resetSectionDown.classList.add("hidden");
    transcriptSection.classList.add("hidden");
    feedbackContainer.classList.add("hidden");
    feedbackSection.classList.add("hidden");
    resetSectionUp.classList.add("hidden");
}
