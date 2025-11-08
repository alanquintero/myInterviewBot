export async function checkSystemRequirements() {
    console.log("checking System Requirements...");
    const systemMessage = document.getElementById("systemMessage");
    try {
        const response = await fetch('/api/v1/requirements');
        let data = await response.json();

        if (!data) {
            alert('Something went wrong. Please reload the page.');
        } else {
            if (!data.areAllSystemRequirementsMet) {
                console.log("System requirements are not met");
                systemMessage.classList.remove("hidden");
                const contentDiv = document.getElementById('systemDetailsContent');
                if (contentDiv) {
                    contentDiv.innerHTML = data.systemRequirementsMessage;
                }
            } else {
                console.log("System requirements met");
                systemMessage.classList.add("hidden");
            }
        }
    } catch (err) {
        console.error(err);
    }

    // Check Video and Mic
    checkMediaDevices().then(result => {
        console.log("Camera available:", result.hasCamera);
        console.log("Microphone available:", result.hasMicrophone);

        const mediaMessage = document.getElementById("mediaDevicesMessage");

        if (!result.hasCamera && result.hasMicrophone) {
            mediaMessage.className = "alert alert-warning alert-dismissible fade show";
            mediaMessage.innerHTML = `
    ⚠️ Your system does not have a camera. Using microphone only.
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  `;
            mediaMessage.classList.remove("d-none");
        } else if (!result.hasCamera && !result.hasMicrophone) {
            mediaMessage.className = "alert alert-danger alert-dismissible fade show";
            mediaMessage.innerHTML = `
    ⚠️ Your system does not have a camera or microphone. The app might not work properly.
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  `;
            mediaMessage.classList.remove("d-none");
        } else {
            mediaMessage.classList.add("d-none"); // hide it if everything is fine
        }

    });
}

export function checkSlowPromptResponse(promptResponse) {
    if (!promptResponse || !promptResponse.promptStats) {
        console.log("promptResponse is null!");
        return;
    }
    const promptStats = promptResponse.promptStats;
    console.log("slowPromptResponse: " + promptStats.slowPromptResponse);
    const promptMessage = document.getElementById("promptMessage");

    // Reset base alert classes
    promptMessage.className = "alert alert-dismissible fade show d-none";

    if (!promptStats.executedSuccessfully && !promptStats.exceptionDetected && !promptStats.slowPromptResponse) {
        console.log("Prompt did not execute successfully.");
        promptMessage.classList.remove("d-none");
        promptMessage.classList.add("alert-warning");
        promptMessage.innerHTML = `
        Something went wrong. Please try again.
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    } else if (promptStats.slowPromptResponse || (!promptStats.executedSuccessfully && promptStats.exceptionDetected)) {
        console.log("Slow prompt execution detected");
        promptMessage.classList.remove("d-none");
        promptMessage.classList.add("alert-danger");
        promptMessage.innerHTML = `
        ⚠️ System performance appears insufficient for local AI inference.
        Consider using a smaller model or switching to a faster system.
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    } else {
        // Hide alert if everything is fine
        promptMessage.classList.add("d-none");
    }

}

async function checkMediaDevices() {
    if (!navigator.mediaDevices || !navigator.mediaDevices.enumerateDevices) {
        console.warn("MediaDevices API not supported.");
        return {hasCamera: false, hasMicrophone: false};
    }

    try {
        const devices = await navigator.mediaDevices.enumerateDevices();
        const hasCamera = devices.some(device => device.kind === "videoinput");
        const hasMicrophone = devices.some(device => device.kind === "audioinput");

        return {hasCamera, hasMicrophone};
    } catch (err) {
        console.error("Error checking media devices:", err);
        return {hasCamera: false, hasMicrophone: false};
    }
}