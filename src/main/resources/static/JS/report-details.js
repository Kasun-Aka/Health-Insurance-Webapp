let mode = "create"; // default mode

const createModeBtn = document.getElementById("createModeBtn");
const updateModeBtn = document.getElementById("updateModeBtn");
const fetchSection = document.getElementById("fetchSection");
const reportForm = document.getElementById("reportForm");

// Mode buttons
createModeBtn.addEventListener("click", () => switchMode("create"));
updateModeBtn.addEventListener("click", () => switchMode("update"));
document.getElementById("fetchBtn").addEventListener("click", fetchReport);
reportForm.addEventListener("submit", submitForm);

// Switch between create and update
function switchMode(newMode) {
    mode = newMode;
    if (newMode === "create") {
        fetchSection.classList.add("hidden");
        createModeBtn.classList.add("active");
        updateModeBtn.classList.remove("active");
        clearForm();
    } else {
        fetchSection.classList.remove("hidden");
        updateModeBtn.classList.add("active");
        createModeBtn.classList.remove("active");
    }
}

// Clear all form inputs
function clearForm() {
    document.querySelectorAll("#reportForm input, #reportForm textarea").forEach(el => el.value = "");
}

// Fetch report data for update mode
async function fetchReport() {
    const id = document.getElementById("reportIdInput").value.trim();
    if (!id) {
        Swal.fire("⚠️ Please enter a Report ID");
        return;
    }

    try {
        const response = await fetch(`/admin/reports/${id}`);
        if (response.ok) {
            const report = await response.json();
            document.getElementById("reportName").value = report.reportName;
            document.getElementById("reportType").value = report.reportType;
            document.getElementById("description").value = report.description;
            Swal.fire("✅ Report loaded successfully!");
        } else {
            Swal.fire("❌ Report not found!");
        }
    } catch (error) {
        console.error("Error fetching report:", error);
        Swal.fire("❌ Failed to fetch report");
    }
}

// Handle form submission for create/update
async function submitForm(event) {
    event.preventDefault();

    const reportData = {
        reportName: document.getElementById("reportName").value.trim(),
        reportType: document.getElementById("reportType").value.trim(),
        description: document.getElementById("description").value.trim()
    };

    if (!reportData.reportName || !reportData.reportType || !reportData.description) {
        Swal.fire("⚠️ Please fill out all fields before submitting!");
        return;
    }

    try {
        let response;

        if (mode === "create") {
            // Send to /manual so backend fetches earnings, customers, etc. automatically
            response = await fetch("/admin/reports/manual", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(reportData)
            });
        } else {
            const id = document.getElementById("reportIdInput").value.trim();
            if (!id) {
                Swal.fire("⚠️ Please enter a valid Report ID to update.");
                return;
            }

            response = await fetch(`/admin/reports/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(reportData)
            });
        }

        if (response.ok) {
            const msg = mode === "create" ? "✅ Report created successfully!" : "✅ Report updated successfully!";
            Swal.fire({
                title: msg,
                icon: "success",
                showConfirmButton: false,
                timer: 1500
            }).then(() => {
                window.location.href = "/report-dashboard.html"; // Redirect back after success
            });
        } else {
            const msg = mode === "create" ? "❌ Failed to create report" : "❌ Failed to update report";
            Swal.fire(msg);
        }
    } catch (error) {
        console.error("Error saving report:", error);
        Swal.fire("❌ An unexpected error occurred while connecting to the server.");
    }
}
