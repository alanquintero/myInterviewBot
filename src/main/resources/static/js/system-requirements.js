export async function checkSystemRequirements() {
    console.log("checking System Requirements...");
    const slowSystem = document.getElementById("slowSystem");
    try {
        const response = await fetch('/api/v1/requirements');
        let data = await response.json();

        if (!data) {
            alert('Something went wrong. Please reload the page.');
        } else {
            checkSlowPromptResponse(data)
        }
    } catch (err) {
        console.error(err);
    }
}

export function checkSlowPromptResponse(systemRequirements) {
    console.log("slowPromptResponse: " + systemRequirements.slowPromptResponse);
    const systemMessage = document.getElementById("systemMessage");
    if (!systemRequirements.executedSuccessfully && !systemRequirements.exceptionDetected) {
        console.log("Prompt did not execute successfully.");
        systemMessage.classList.add("alert-warning");
        systemMessage.classList.remove("hidden");
        systemMessage.innerText = "Something went wrong. Please try again.";
    } else if (systemRequirements.slowPromptResponse || (!systemRequirements.executedSuccessfully && systemRequirements.exceptionDetected)) {
        console.log("Insufficient System Requirements detected");
        systemMessage.classList.add("alert-danger");
        systemMessage.classList.remove("hidden");
        systemMessage.innerText = "⚠️ System performance appears insufficient for local AI inference. Consider using a smaller model or switching to a faster system.";
    } else {
        console.log("System Requirements are met");
        systemMessage.classList.add("hidden");
    }
}