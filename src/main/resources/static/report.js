let myChart;
const API_BASE = "/admin/reports";

document.addEventListener("DOMContentLoaded", () => {
    fetchAllStats();
    updateDate();
});

async function fetchAllStats() {
    try {
        // Parallel fetching
        const [earnings, users, topPolicy, ratings, monthlyData] = await Promise.all([
            fetch(`${API_BASE}/earnings`).then(res => res.json()),
            fetch(`${API_BASE}/users`).then(res => res.json()),
            fetch(`${API_BASE}/topPolicy`).then(res => res.json()),
            fetch(`${API_BASE}/ratings`).then(res => res.json()),
            fetch(`${API_BASE}/monthly-earnings`).then(res => res.json())
        ]);

        // 1. Update Top Cards
        document.getElementById("earningsValue").textContent = `Rs. ${earnings.toLocaleString()}`;
        document.getElementById("usersValue").textContent = users;
        document.getElementById("topPolicyValue").textContent = topPolicy || "N/A";

        // 2. Populate the NEW Ratings Table
        populateRatingTable(ratings);

        // 3. Render Chart (Fixed the variable passing)
        renderChart(monthlyData);

    } catch (error) {
        console.error("Dashboard Sync Failed:", error);
        Swal.fire("Error", "Could not sync data from server", "error");
    }
}

function populateRatingTable(ratings) {
    const tbody = document.querySelector("#ratingTable tbody");
    tbody.innerHTML = "";

    if (!ratings || ratings.length === 0) {
        tbody.innerHTML = "<tr><td colspan='2'>No ratings yet</td></tr>";
        return;
    }

    ratings.forEach(item => {
        // item[0] = Package Name, item[1] = Average Rating
        const row = `
            <tr>
                <td><strong>${item[0]}</strong></td>
                <td>
                    <span style="color: #f97316;">${'⭐'.repeat(Math.round(item[1]))}</span>
                    (${parseFloat(item[1]).toFixed(1)})
                </td>
            </tr>`;
        tbody.innerHTML += row;
    });
}

function renderChart(monthlyData) {
    const ctx = document.getElementById("analyticsChart").getContext("2d");
    if (myChart) myChart.destroy();

    // Labels: "Jan 2024", "Feb 2024", ...
    const labels = monthlyData.map(item => `${item[0]} ${item[1]}`);

    // Data: total earnings
    const dataPoints = monthlyData.map(item => item[2]);

    myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Actual Revenue (Rs.)',
                data: dataPoints,
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Revenue (Rs.)'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Month'
                    }
                }
            }
        }
    });
}


function updateDate() {
    document.getElementById("currentDate").textContent = new Date().toLocaleDateString("en-US", {
        weekday: "long", year: "numeric", month: "long", day: "numeric"
    });
}