document.addEventListener("DOMContentLoaded", () => {
    // Input fields
    const idInput = document.getElementById("ins-id");
    const nameInput = document.getElementById("ins-name");
    const rateInput = document.getElementById("ins-rate");
    const durInput = document.getElementById("ins-dur");
    const desInput = document.getElementById("ins-des");
    const benInput = document.getElementById("ins-ben");
    const imgurlInput = document.getElementById("ins-imgurl");

    // Buttons
    const refreshBtn = document.querySelector(".refresh");
    const saveBtn = document.querySelector(".save");
    const deleteBtn = document.querySelector(".delete");

    // Helper function to clear all form fields
    function clearForm() {
        idInput.value = "";
        nameInput.value = "";
        rateInput.value = "";
        durInput.value = "";
        desInput.value = "";
        benInput.value = "";
        imgurlInput.value = "";
    }

    // --- Fetch policy by ID / Clear Form ---
    refreshBtn.addEventListener("click", () => {
        const id = idInput.value.trim();

        // CASE 1: Empty ID (Clear form and return)
        if (!id) {
            clearForm();
            return;
        }

        // CASE 2 & 3: ID is present (Search)
        fetch(`/api/policies/${id}`)
            .then(res => {
                // Check for non-200 status codes first
                if (!res.ok) {
                    // Throw a custom error message for the user based on 404 status
                    if (res.status === 404) {
                        throw new Error(`Error: Invalid Policy ID '${id}'. Policy not found in the database.`);
                    }
                    // Handle any other non-200 status (e.g., 500 server error)
                    throw new Error(`Error loading policy: Server returned status ${res.status} (${res.statusText}).`);
                }

                // Only execute res.json() if the status is 200 OK
                return res.json();
            })
            .then(policy => {
                // CASE 3: Valid ID (Load data)
                nameInput.value = policy.policyName;
                rateInput.value = policy.rate;
                durInput.value = policy.durationDays;
                desInput.value = policy.description;
                benInput.value = policy.benefits;
                imgurlInput.value = policy.imageUrl;

            })
            .catch(err => {
                // CASE 2: Invalid ID (Show specific error)
                alert(err.message);

                // Clear all fields except the ID, so the user knows which ID failed
                const failedId = idInput.value;
                clearForm();
                idInput.value = failedId;
            });
    });

    // --- Save (add/update) ---
    saveBtn.addEventListener("click", () => {
        const id = idInput.value.trim();

        // Basic validation check
        if (!nameInput.value.trim() || !rateInput.value.trim() || !durInput.value.trim()) {
            alert("Please fill in Policy Name, Rate, and Duration.");
            return;
        }

        const policy = {
            id: id,
            policyName: nameInput.value.trim(),
            rate: rateInput.value.trim(),
            durationDays: durInput.value.trim(),
            description: desInput.value.trim(),
            benefits: benInput.value.trim(),
            imageUrl: imgurlInput.value.trim()
        };

        const method = id ? "PUT" : "POST";
        const url = id ? `/api/policies/${id}` : "/api/policies";

        fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(policy)
        })
            .then(res => {
                if (!res.ok) throw new Error(`Error saving policy: ${res.statusText}`);
                return res.json();
            })
            .then(() => {
                alert(id ? "Policy updated successfully!" : "Policy added successfully!");
                clearForm();
            })
            .catch(err => alert(err.message));
    });


    // --- Delete policy ---
    deleteBtn.addEventListener("click", () => {
        const id = idInput.value.trim();
        if (!id) {
            alert("Enter an Insurance ID to delete!");
            return;
        }

        if (!confirm(`Are you sure you want to delete policy ID: ${id}?`)) return;

        fetch(`/api/policies/${id}`, { method: "DELETE" })
            .then(res => {
                if (!res.ok) throw new Error(`Error deleting policy: ${res.statusText}. Policy may not exist.`);
                alert("Policy deleted successfully!");
                clearForm();
            })
            .catch(err => alert(err.message));
    });
});