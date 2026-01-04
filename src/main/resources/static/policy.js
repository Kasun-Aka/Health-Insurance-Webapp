document.addEventListener("DOMContentLoaded", () => {
    // 1. DOM Element Selection
    const container = document.querySelector(".ins-container");
    const infoArea = document.querySelector(".ins-info");

    // Action Buttons & Containers
    const infoActionsContainer = infoArea.querySelector(".info-actions");
    const selectBtn = infoArea.querySelector(".select-btn");
    const termsBtn = infoArea.querySelector(".terms-btn");

    // Placeholder and Modal Elements
    const placeholder = infoArea.querySelector(".placeholder");
    const modal = document.getElementById("termsModal");
    const closeBtn = document.querySelector(".close-btn");
    const acceptBtn = document.getElementById("acceptTermsBtn");
    const modalPolicyTitle = document.querySelector(".modal-policy-title");

    // 2. State Variables
    let selectedBox = null;
    let currentPolicy = null;
    let termsAccepted = false;

    // 3. Button State Management Function
    function updateSelectButton() {
        if (currentPolicy && termsAccepted) {
            selectBtn.textContent = `Select ${currentPolicy.policyName}`;
            selectBtn.disabled = false;
            selectBtn.style.opacity = '1';
        } else if (currentPolicy) {
            selectBtn.textContent = `Review T&C to Select`;
            selectBtn.disabled = true;
            selectBtn.style.opacity = '0.7'; // Visually show it's disabled
        } else {
            selectBtn.textContent = `Select`;
            selectBtn.disabled = true;
            selectBtn.style.opacity = '0.7';
        }
    }

    // Initialize button state on load
    updateSelectButton();

    // 4. Fetch Policies from DB
    // Assuming /api/policies now returns policies including the 'rate' and 'durationDays' needed for the payload
    fetch("/api/policies")
        .then(res => res.json())
        .then(data => {
            container.innerHTML = ""; // clear static content
            data.forEach(policy => {
                const box = document.createElement("div");
                box.classList.add("ins-box");
                box.innerHTML = `
                    <img src="/assets/${policy.imageUrl}"
                         alt="${policy.policyName}">
                    <h3>${policy.policyName}</h3>
                `;
                container.appendChild(box);

                // Hover -> show info (temporary preview)
                box.addEventListener("mouseenter", () => {
                    if (!selectedBox) {
                        showInfo(policy);
                    }
                });

                // Click -> lock info
                box.addEventListener("click", () => {
                    if (selectedBox) {
                        selectedBox.classList.remove("selected");
                    }
                    selectedBox = box;
                    selectedBox.classList.add("selected");
                    currentPolicy = policy;
                    showInfo(policy, true);

                    // Reset acceptance state when a NEW policy is selected
                    termsAccepted = false;
                    updateSelectButton();
                });
            });
        })
        .catch(err => console.error("Error fetching policies:", err));

    // 5. Show policy info inside right panel
    function showInfo(policy, lock = false) {
        if (placeholder) {
            placeholder.style.display = 'none';
        }

        const old = infoArea.querySelector("div.policy-details");
        if (old) old.remove();

        const div = document.createElement("div");
        div.classList.add("policy-details", "active");
        div.innerHTML = `
            <h3><u>${policy.policyName}</u></h3>
            <p>${policy.description}</p>
            <p>Rate - ${policy.rate} LKR per month <br>
               Duration - ${policy.durationDays} Days <br>
               Benefits - ${policy.benefits || "N/A"}
            </p>
        `;

        infoArea.insertBefore(div, infoActionsContainer);

        if (lock) currentPolicy = policy;

        // Ensure the button state is updated after showing the details
        if (lock) updateSelectButton();
    }

    // 6. Event Listeners for Buttons

    selectBtn.addEventListener("click", () => {
        if (!currentPolicy) {
            alert("Please select a policy first!");
            return;
        }
        if (!termsAccepted) {
            alert("Error: Please review and accept the Terms and Conditions first!");
            return;
        }

        // 1. Get the user session data from localStorage
        const userSessionData = localStorage.getItem("userSession");

        if (!userSessionData) {
            alert("Error: User session not found. Please log in again.");
            return;
        }

        // 2. Parse the JSON string and extract user details
        let userId = null;
        let userName = null;
        try {
            const user = JSON.parse(userSessionData);

            userId = user.userId;
            userName = user.username;
        } catch (e) {
            alert("Error: Invalid user session data format.");
            return;
        }

        if (!userId || isNaN(Number(userId))) {
            alert("Error: User ID not found or is invalid in session data. Please ensure 'userId' is present.");
            return;
        }
        if (!userName) {
            alert("Error: User Name not found in session data. Please ensure 'username' is present.");
            return;
        }

        alert(`You have selected: ${currentPolicy.policyName}. Attempting to create new policy record for User ID: ${userId} (${userName})...`);

        // 3. Prepare the data payload for the new userPolicies record
        const userPolicyData = {
            // Policy details
            policyName: currentPolicy.policyName,
            rate: currentPolicy.rate,
            durationDays: currentPolicy.durationDays,

            // User details
            userId: Number(userId),
            userName: userName
        };

        // 4. Send POST request to create a new userPolicies record
        fetch("/api/policies/user", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userPolicyData)
        })
        .then(res => {
            if (!res.ok) {
                // Throw an error with the status for clearer debugging
                throw new Error(`Policy selection failed with status: ${res.status} (${res.statusText})`);
            }
            return res.json();
        })
        .then(data => {
            // Success Block
            alert(`Success! The ${data.policyName} policy has been linked to your account. Record ID (userPolicyId): ${data.userPolicyId}. Redirecting to dashboard...`);

            // ⬅️ NEW: Redirect the user to the dashboard
            window.location.href = "../Dashboard.html";
        })
        .catch(error => {
            console.error("Policy Selection Error:", error);
            alert(`An error occurred while selecting the policy: ${error.message}`);
        });
    });


    // Terms and Conditions Button Listener (Triggers Modal)
    termsBtn.addEventListener("click", () => {
        if (!currentPolicy) {
            alert("Please select a policy first to view its specific terms!");
            return;
        }
        // Set the policy title in the modal and show it
        modalPolicyTitle.textContent = `Terms for: ${currentPolicy.policyName}`;
        modal.style.display = "block";
    });

    // 7. Modal Logic

    // Close modal when X is clicked
    closeBtn.onclick = function() {
        modal.style.display = "none";
    }

    // Close modal and set termsAccepted when accept button is clicked
    acceptBtn.onclick = function() {
        termsAccepted = true;
        modal.style.display = "none";
        updateSelectButton();
    }

    // Close modal when clicking outside of it
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    }
});