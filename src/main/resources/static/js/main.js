/* Input section */
const inputProfession = document.getElementById("inputProfession");
const inputQuestion = document.getElementById("inputQuestion");

/* Button section */
const generateQuestionBtn = document.getElementById("generateQuestionBtn");
const resetBtnTop = document.getElementById("resetBtnTop");
const readyBtn = document.getElementById("readyBtn");

/* Loading Question GIF */
const loadingQuestion = document.getElementById("loadingQuestion");

/* Recording & Playback section */
const recordingPlaybackContainer = document.getElementById("recordingPlaybackContainer");
// Recording section
const recordingSection = document.getElementById("recordingSection");
const videoEl = document.getElementById("video");
const videoPlaceholder = document.getElementById("videoPlaceholder");
const recordBtn = document.getElementById("recordBtn");
const recordBtnImg = document.getElementById("recordBtnImg");
const recordLabel = document.getElementById("recordLabel");
const recordingOverlay = document.getElementById("recordingOverlay");
const recordingIndicator = document.getElementById("recordingIndicator");
const recordingTimer = document.getElementById("recordingTimer");
// Playback section
const playbackSection = document.getElementById("playbackSection");
const playbackEl = document.getElementById("playback");
// Generate Feedback button
const generateFeedbackSection = document.getElementById("generateFeedbackSection");
const generateFeedbackBtn = document.getElementById("generateFeedbackBtn");

/* Loading Feedback GIF */
const loadingFeedback = document.getElementById("loadingFeedback");

/* Transcript & Feedback section */
const transcriptFeedbackContainer = document.getElementById("transcriptFeedbackContainer");
// Transcript section
const transcriptSection = document.getElementById("transcriptSection");
const transcriptEl = document.getElementById("transcript");
// Feedback section
const feedbackSection = document.getElementById("feedbackSection");
const feedbackEl = document.getElementById("feedback");

/* Reset section */
const resetSection = document.getElementById("resetSection");
const resetBtnBottom = document.getElementById("resetBtnBottom");

const MAX_RECORDING_TIME = 150; // in seconds (2 minutes 30 seconds)
const RECORD_VIDEO_AGAIN_TXT = "Click to record again →";
const RECORD_BTN_IMG_URL = "img/button/record.png";
const STOP_RECORD_BTN_IMG_URL = "img/button/stop.gif";
let timerInterval;
let mediaRecorder;
let currentStream = null;
let recordedChunks = [];
let isRecording = false;

// Request a generated question
generateQuestionBtn.addEventListener("click", async () => {
    console.log("Generating Question");
    // Disable elements while loading
    setElementsDisabled(true);

    setProfessionIfBlank();
    const profession = inputProfession.value;
    console.log("Profession: ", profession);

    // Hide elements if case they are present
    hideElements();

    await generateQuestion(profession);
    // Enable elements
    setElementsDisabled(false);
});

// Set the Profession to default value "Software Engineer", if Profession left in blank.
function setProfessionIfBlank() {
    if (!inputProfession.value || inputProfession.value.trim() === '') {
        console.log("Set Profession to default value");
        inputProfession.value = "Software Engineer";
    }
}

// Call API to generate a question
async function generateQuestion(profession) {
    loadingQuestion.classList.remove("hidden"); // show loading GIF
    try {
        const res = await fetch(`/api/v1/question?profession=${encodeURIComponent(profession)}`);
        const data = await res.json();

        if (!data.question || data.question.trim() === '') {
            alert('No question was generated. Please try again.');
        } else {
            inputQuestion.value = data.question;
        }
    } catch (err) {
        console.error(err);
    } finally {
        loadingQuestion.classList.add("hidden"); // hide loading GIF
    }
}

// Click on Ready button
readyBtn.addEventListener("click", async () => {
    console.log("Click on ready button");
    // Disable elements while loading
    setElementsDisabled(true);

    setProfessionIfBlank();
    const profession = inputProfession.value;
    console.log("Profession: ", profession);

    if (!inputQuestion.value || inputQuestion.value.trim() === '') {
        // Getting a generated question if question left in blank
        await generateQuestion(profession);

        if (!inputQuestion.value || inputQuestion.value.trim() === '') {
            alert('Something went wrong. Please try again.');
        } else {
            showRecordingSection();
        }
    } else {
        showRecordingSection();
    }
    // Enable Reset button
    resetBtnTop.disabled = false;
});

// Click on Reset button top
resetBtnTop.addEventListener("click", async () => {
    console.log("Click on reset button top");
    reset();
});

// Click on Ready button down
resetBtnBottom.addEventListener("click", async () => {
    console.log("Click on reset button down");
    reset();
});

// Reset to initial config
function reset() {
    hideElements();
    stopCamera();
    setElementsDisabled(false);
    // Reset other UI states
    inputQuestion.value = "";
    recordedChunks = [];
    timerInterval = null;
    mediaRecorder = null;
    currentStream = null;
    isRecording = false;
}

// Click on Generate Feedback button
generateFeedbackBtn.addEventListener("click", async () => {
    if (recordedChunks.length === 0) {
        console.log("No recording found.");
        return;
    }
    console.log("Generating feedback...");
    setResetButtonsDisabled(true);
    const blob = new Blob(recordedChunks, {type: "video/webm"});
    await sendVideo(blob);
    setResetButtonsDisabled(false);
});

// Click on Record button
recordBtn.addEventListener("click", async () => {
    if (!isRecording) {
        console.log("Clicked on Start recording...");
        // Hide Playback and Generate Feedback sections
        playbackSection.classList.add("hidden");
        generateFeedbackSection.classList.add("hidden");
        playbackEl.src = '';

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
            recordBtnImg.src = STOP_RECORD_BTN_IMG_URL;
            recordLabel.textContent = "";
            startRecordingUI(); // countdown and gif handled here

        } catch (err) {
            console.error(err);
            alert("Failed to start recording. Make sure microphone and camera are allowed.");
        }
    } else {
        console.log("Clicked on Stop recording...");
        // Stop Recording manually
        if (mediaRecorder && mediaRecorder.state === "recording") {
            mediaRecorder.stop();
        }
        isRecording = false;
        recordBtnImg.src = RECORD_BTN_IMG_URL;
        recordLabel.textContent = RECORD_VIDEO_AGAIN_TXT;
        stopRecordingUI();
    }
});

// Shows the Recoding section
function showRecordingSection() {
    console.log("Showing recording section...");
    recordingSection.classList.remove("hidden");
    // Playback should not be visible until there is a recorded video
    playbackSection.classList.add("hidden");
    recordingPlaybackContainer.classList.remove("hidden");
    recordBtn.disabled = false;
    videoPlaceholder.classList.remove("hidden");

    // Add fade-in effect
    recordingPlaybackContainer.classList.add("fade-in");
    setTimeout(() => {
        recordingPlaybackContainer.classList.add("show");
    }, 500); // small delay to trigger CSS transition

    initCamera(); // start camera preview immediately
}

// Inits the webcam
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

// Stops the webcam
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
    formData.append("profession", inputProfession.value)
    formData.append("question", inputQuestion.value)

    loadingFeedback.classList.remove("hidden");
    generateFeedbackBtn.disabled = true;
    recordBtn.disabled = true;
    recordLabel.textContent = "";
    stopCamera();

    try {
        const res = await fetch("/api/v1/feedback", {
            method: "POST",
            body: formData
        });
        const data = await res.json();

        if (!data.feedback || data.feedback.trim() === '') {
            alert('No feedback was generated. Please try again.');
            generateFeedbackBtn.disabled = false;
        } else {
            transcriptFeedbackContainer.classList.remove("hidden");
            transcriptSection.classList.remove("hidden");
            feedbackSection.classList.remove("hidden");
            resetSection.classList.remove("hidden");

            transcriptEl.innerText = data.transcript || "";
            feedbackEl.innerText = data.feedback || "No feedback returned";
        }
    } catch (err) {
        console.error(err);
        alert('No feedback was generated. Please try again.');
        generateFeedbackBtn.disabled = false;
    } finally {
        loadingFeedback.classList.add("hidden");
    }
}

// Show UI elements for when Recording is active
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
            recordBtnImg.src = RECORD_BTN_IMG_URL;
            recordLabel.textContent = RECORD_VIDEO_AGAIN_TXT;
            stopRecordingUI();
            clearInterval(timerInterval);
        }
    }, 1000);
}

// Hide UI elements for when Recording is stopped
function stopRecordingUI() {
    console.log("stop Recording UI...");
    recordingIndicator.classList.add("hidden");
    recordingTimer.classList.add("hidden");
    recordingOverlay.classList.remove("active");
    generateFeedbackBtn.disabled = false;
    clearInterval(timerInterval);
}

// Updates the recording timer
function updateTimerDisplay(seconds) {
    const minutes = Math.floor(seconds / 60).toString().padStart(2, "0");
    const secs = (seconds % 60).toString().padStart(2, "0");
    recordingTimer.textContent = `${minutes}:${secs}`;
}

// Hide UI elements
function hideElements() {
    console.log("hide elements...");
    recordingSection.classList.add("hidden");
    recordingPlaybackContainer.classList.add("hidden");
    playbackSection.classList.add("hidden");
    generateFeedbackSection.classList.add("hidden");
    resetSection.classList.add("hidden");
    transcriptSection.classList.add("hidden");
    transcriptFeedbackContainer.classList.add("hidden");
    feedbackSection.classList.add("hidden");
}

// Enables/disables UI elements
function setElementsDisabled(disabled) {
    console.log("disable elements; disabled: ", disabled);
    inputProfession.disabled = disabled;
    inputQuestion.disabled = disabled;
    generateQuestionBtn.disabled = disabled;
    readyBtn.disabled = disabled;
    resetBtnTop.disabled = disabled;
    resetBtnBottom.disabled = disabled;
}

// Enables/disables reset buttons
function setResetButtonsDisabled(disabled) {
    console.log("enable disable reset buttons; disabled: ", disabled);
    resetBtnTop.disabled = disabled;
    resetBtnBottom.disabled = disabled;
}

// Inserts the clicked question into the inputQuestion field
document.querySelectorAll('#commonQuestionsModal .list-group-item').forEach(item => {
    item.addEventListener('click', () => {
        document.getElementById('inputQuestion').value = item.textContent
            .replace(/^\d+\.\s*/, '') // remove leading number and dot
            .replace(/\r?\n|\r/g, ' ') // replace any line breaks with space
            .trim(); // remove extra spaces at start/end

        const modal = bootstrap.Modal.getInstance(document.getElementById('commonQuestionsModal'));
        modal.hide();
    });
});
