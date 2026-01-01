// Dashboard.js - hooks for your Dashboard.html

// -------------------- 1. Data Retrieval Functions --------------------

// Fetches the username from the structured userSession in localStorage,
// falling back to 'currentUserName' if 'userSession' isn't available/formatted,
// and finally defaulting to 'User'.
function getCurrentUserName() {
    const userSessionData = localStorage.getItem('userSession');
    if (userSessionData) {
        try {
            const user = JSON.parse(userSessionData);
            // Use 'username' key as confirmed by your data structure
            return user.username || localStorage.getItem('currentUserName') || 'User';
        } catch (e) {
            console.error("Error parsing userSession:", e);
            // Fallback if parsing fails
            return localStorage.getItem('currentUserName') || 'User';
        }
    }
    return localStorage.getItem('currentUserName') || 'User';
}

function getCurrentUserId() {
    const userSessionData = localStorage.getItem("userSession");
    if (userSessionData) {
        try {
            const user = JSON.parse(userSessionData);
            return user.userId;
        } catch (e) {
            console.error("Error parsing userSession for ID:", e);
            return null;
        }
    }
    return null;
}

// -------------------- 2. Policy Fetching Function --------------------

function fetchAndDisplayUserPolicy() {
    const userId = getCurrentUserId();

    // Default display elements
    const planEl = document.getElementById('activePlanName');
    const premiumEl = document.getElementById('monthlyPremium');

    if (!userId || isNaN(Number(userId))) {
        planEl.textContent = 'Please Log In';
        premiumEl.textContent = 'N/A';
        return;
    }

    // Set initial loading state
    planEl.textContent = 'Loading Policy...';
    premiumEl.textContent = '...';

    // Call the backend endpoint: /api/policies/user/{userId}/active
    fetch(`/api/policies/user/${userId}/active`)
        .then(res => {
            if (res.status === 404) {
                // User has no active policy (Expected for new users)
                return null;
            }
            if (!res.ok) {
                // Catch any other server errors (e.g., 500)
                throw new Error(`Failed to fetch policy: ${res.status}`);
            }
            return res.json();
        })
        .then(policyData => {
            if (policyData) {
                // Policy found, update DOM with fetched data
                planEl.textContent = policyData.policyName;

                // Use Intl.NumberFormat for cleaner currency display (LKR)
                const formatter = new Intl.NumberFormat('en-US', {
                    style: 'currency',
                    currency: 'LKR',
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                });

                premiumEl.textContent = `${formatter.format(policyData.rate)}/month`;
            } else {
                // Policy not found (404 response)
                planEl.textContent = 'No Active Plan Selected';
                premiumEl.textContent = 'LKR 0.00/month';
            }
        })
        .catch(error => {
            console.error("Error fetching user policy:", error);
            planEl.textContent = 'Error loading plan';
            premiumEl.textContent = 'Error';
        });
}

// -------------------- 3. Navigation Functions --------------------

function showProfile() {
    window.location.href = 'Profile.html';
}

function logout() {
    // Clear all session/user related data from localStorage
    localStorage.removeItem('userSession');
    localStorage.removeItem('currentUserEmail');
    localStorage.removeItem('currentUserName');
    window.location.href = 'index.html';
}

function Payment1() {
    window.location.href = "../my-policies.html";
}

function Reviews() {
    window.location.href = "../Reviews.html";
}

function changePlan() {
    // This is the link to your policy selection page
    window.location.href = "../policy.html";
}

function viewPayment() {
    window.location.href = "../make-payment.html";
}

function goToAdmin() {
    // Redirect to admin dashboard
    window.location.href = 'AdminDashboard.html';
}

// -------------------- 4. Initialization --------------------

// Runs when the DOM is fully loaded to set up the dashboard
document.addEventListener('DOMContentLoaded', () => {
    // Set the welcome text
    const nameSpan = document.getElementById('username');
    const userName = getCurrentUserName();
    if (nameSpan) nameSpan.textContent = userName;

    // Fetch and display policy data on load
    fetchAndDisplayUserPolicy();
});