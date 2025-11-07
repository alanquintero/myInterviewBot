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
    const slowSystem = document.getElementById("slowSystem");
    if (systemRequirements.slowPromptResponse) {
        console.log("Insufficient System Requirements detected");
        slowSystem.classList.remove("hidden");
    } else {
        console.log("System Requirements are met");
        slowSystem.classList.add("hidden");
    }
}