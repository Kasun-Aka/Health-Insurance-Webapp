let chart;

// 🧹 Clear and Add Rows
function clearTable() {
    document.querySelector("#reportTable tbody").innerHTML = "";
}

function addReportToTable(report) {
    const tbody = document.querySelector("#reportTable tbody");
    const row = document.createElement("tr");
    row.innerHTML = `
    <td>${report.id}</td>
    <td>${report.reportName}</td>
    <td>${report.reportType}</td>
    <td>${report.description}</td>
    <td>${report.earnings}</td>
    <td>${report.totalCustomers}</td>
    <td>${report.totalPolicies}</td>
    <td>${report.totalClaims}</td>`;
    tbody.appendChild(row);
}

// 📊 Create Report
async function createReport() {
    const res = await fetch("/admin/reports", { method: "POST" });
    const data = await res.json();
    clearTable();
    addReportToTable(data);
    Swal.fire("✅ Report Created!", `ID: ${data.id}`, "success");
    updateAnalytics();
}

// 📋 Get All Reports
async function getAllReports() {
    const res = await fetch("/admin/reports");
    const reports = await res.json();
    clearTable();
    reports.forEach(addReportToTable);
}

// ✏️ Update Report
async function updateReport() {
    const id = prompt("Enter Report ID to update:");
    if (!id) return;
    const reportName = prompt("Enter new report name:");
    const reportType = prompt("Enter report type:");
    const description = prompt("Enter description:");
    const earnings = parseFloat(prompt("Enter earnings:"));
    const totalCustomers = parseInt(prompt("Enter total customers:"));
    const totalPolicies = parseInt(prompt("Enter total policies:"));
    const totalClaims = parseInt(prompt("Enter total claims:"));

    const res = await fetch(`/admin/reports/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            reportName,
            reportType,
            description,
            earnings,
            totalCustomers,
            totalPolicies,
            totalClaims,
        }),
    });

    if (res.ok) {
        Swal.fire("✅ Updated!", "Report updated successfully", "success");
        getAllReports();
    } else {
        Swal.fire("❌ Error", "Could not update report", "error");
    }
}

// 🗑️ Delete Report
async function deleteReport() {
    const id = prompt("Enter Report ID to delete:");
    if (!id) return;
    const res = await fetch(`/admin/reports/${id}`, { method: "DELETE" });
    if (res.ok) {
        Swal.fire("🗑️ Deleted!", "Report removed", "success");
        getAllReports();
    } else {
        Swal.fire("❌ Error", "Could not delete report", "error");
    }
}

// 💰 View Analytics
async function viewEarnings() {
    const res = await fetch("/admin/reports/earnings");
    const data = await res.json();
    document.getElementById("earningsValue").textContent = `$${data}`;
    Swal.fire("💰 Total Earnings", `$${data}`, "info");
}

async function viewUsers() {
    const res = await fetch("/admin/reports/users");
    const data = await res.json();
    document.getElementById("usersValue").textContent = data;
    Swal.fire("👥 Total Users", `${data}`, "info");
}

async function viewClaims() {
    const res = await fetch("/admin/reports/claims");
    const data = await res.json();
    document.getElementById("claimsValue").textContent = data;
    Swal.fire("📄 Total Claims", `${data}`, "info");
}

// 📈 Chart Update
function updateAnalytics() {
    const earnings = parseFloat(document.getElementById("earningsValue").textContent.replace("$", "")) || 0;
    const users = parseInt(document.getElementById("usersValue").textContent) || 0;
    const claims = parseInt(document.getElementById("claimsValue").textContent) || 0;

    const ctx = document.getElementById("analyticsChart").getContext("2d");
    if (chart) chart.destroy();

    chart = new Chart(ctx, {
        type: "bar",
        data: {
            labels: ["Earnings", "Users", "Claims"],
            datasets: [{
                label: "Live Analytics",
                data: [earnings, users, claims],
                backgroundColor: ["#10b981", "#3b82f6", "#f97316"],
                borderRadius: 10,
            }],
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
        },
    });
}

// 🌙 Theme Toggle
document.getElementById("themeToggle").addEventListener("click", () => {
    document.body.classList.toggle("dark");
});

// 🗓️ Show Current Date
document.getElementById("currentDate").textContent = new Date().toLocaleDateString("en-US", {
    weekday: "long",
    year: "numeric",
    month: "long",
    day: "numeric",
});

function redirectToDetailsPage() {
    window.location.href = "/report-details.html";
}
