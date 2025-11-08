export async function checkSystemRequirements() {
    console.log("checking System Requirements...");
    const systemMessage = document.getElementById("systemMessage");
    try {
        const response = await fetch('/api/v1/requirements');
        let data = await response.json();

        if (!data) {
            alert('Something went wrong. Please reload the page.');
        } else {
            if(!data.areAllSystemRequirementsMet) {
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
}

export function checkSlowPromptResponse(promptResponse) {
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