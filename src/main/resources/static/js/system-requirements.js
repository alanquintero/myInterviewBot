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

        if (!result.hasCamera || !result.hasMicrophone) {
            document.getElementById("mediaDevicesMessage").innerHTML = `
            <div class="alert alert-warning">
                ⚠️ Your system does not have a ${!result.hasCamera ? 'camera' : ''}${!result.hasCamera && !result.hasMicrophone ? ' or ' : ''}${!result.hasMicrophone ? 'microphone' : ''}.
            </div>`;
            document.getElementById("mediaDevicesMessage").classList.remove("hidden");
        }
    });
}

export function checkSlowPromptResponse(promptResponse) {
    if(!promptResponse) {
        console.log("promptResponse is null!");
        return;
    }
    console.log("slowPromptResponse: " + promptResponse.slowPromptResponse);
    const promptMessage = document.getElementById("promptMessage");
    if (!promptResponse.executedSuccessfully && !promptResponse.exceptionDetected && !promptResponse.slowPromptResponse) {
        console.log("Prompt did not execute successfully.");
        promptMessage.classList.add("alert-warning");
        promptMessage.classList.remove("hidden");
        promptMessage.innerText = "Something went wrong. Please try again.";
    } else if (promptResponse.slowPromptResponse || (!promptResponse.executedSuccessfully && promptResponse.exceptionDetected)) {
        console.log("Slow prompt execution detected");
        promptMessage.classList.add("alert-danger");
        promptMessage.classList.remove("hidden");
        promptMessage.innerText = "⚠️ System performance appears insufficient for local AI inference. Consider using a smaller model or switching to a faster system.";
    } else {
        console.log("Prompt execution is good!");
        promptMessage.classList.add("hidden");
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