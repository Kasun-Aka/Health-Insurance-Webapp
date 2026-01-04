const API_BASE = 'http://localhost:8080/reviews';
let currentUserId = null;
let currentUserName = null;

// New helper to read from local storage (matching your dashboard logic)
function syncUserFromStorage() {
    const userSessionData = localStorage.getItem('userSession');
    if (userSessionData) {
        try {
            const user = JSON.parse(userSessionData);
            currentUserId = user.userId;
            currentUserName = user.username || 'User';

            console.log("Review Page synced with User ID:", currentUserId);
        } catch (e) {
            console.error("Error parsing userSession in reviews page:", e);
        }
    }
}

async function loadPolicyDropdown() {
    const dropdown = document.getElementById('reviewPackageName');
    try {
        // Replace this URL with your actual endpoint that returns all policies
        const response = await fetch('http://localhost:8080/api/policies/active');
        const policies = await response.json();

        dropdown.innerHTML = '<option value="">-- Choose a Policy --</option>';
        policies.forEach(policy => {
            // Use policy.policyName or policy.packageName based on your Policy Entity
            const name = policy.policyName || policy.packageName;
            const option = document.createElement('option');
            option.value = name;
            option.textContent = name;
            dropdown.appendChild(option);
        });
    } catch (error) {
        console.error("Error loading policies:", error);
        dropdown.innerHTML = '<option value="">Error loading packages</option>';
    }
}

async function initializeApp() {
    // 1. Get data from local storage instead of a fetch call
    syncUserFromStorage();

    // 2. Update UI to show who is logged in
    const userInfo = document.getElementById('userInfoDiv');
    if (userInfo && currentUserId) {
        userInfo.style.display = 'block';
        document.getElementById('currentUser').textContent = currentUserName;
    }

    // 3. Auto-load the reviews and policy dropdown
    await loadPolicyDropdown();
    await loadAllReviews();
}

// Call the function immediately
initializeApp();

async function showTab(tabName, element) {
    document.querySelectorAll('.content').forEach(c => c.classList.remove('active'));
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.getElementById(tabName).classList.add('active');
    element.classList.add('active');

    if (tabName === 'reviews') {
        await loadAllReviews();
    }
}

function showAlert(elementId, message, type) {
    const alert = document.getElementById(elementId);
    alert.className = `alert alert-${type} show`;
    alert.textContent = message;
    setTimeout(() => alert.classList.remove('show'), 5000);
}

async function addReview() {
    const packageName = document.getElementById('reviewPackageName').value;
    const rating = document.getElementById('reviewRating').value;
    const comment = document.getElementById('reviewComment').value;

    if (!currentUserId) {
        showAlert('addReviewAlert', 'System busy: Fetching user data...', 'error');
        return;
    }

    const reviewData = {
        userId: currentUserId, // Automatically filled from initializeApp()
        packageName: packageName,
        comment: comment,
        rating: parseInt(rating)
    };

    try {
        const response = await fetch(`${API_BASE}/add`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(reviewData)
        });

        if (response.ok) {
            showAlert('addReviewAlert', 'Review added successfully!', 'success');
            // ... clear fields ...
        }
    } catch (error) {
        showAlert('addReviewAlert', 'Error: ' + error.message, 'error');
    }
}

async function loadAllReviews() {
    try {
        console.log('Loading all reviews...');

        const response = await fetch(`${API_BASE}/all`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const reviews = await response.json();
        console.log('Loaded reviews:', reviews.length);

        displayReviews(reviews);
    } catch (error) {
        console.error('Error loading reviews:', error);
        document.getElementById('reviewsList').innerHTML = `<p style="color: red;">Error loading reviews: ${error.message}</p>`;
    }
}

async function filterReviews() {
    const packageName = document.getElementById('filterPackageName').value;
    if (!packageName) {
        alert('Please enter a package name');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/package/name/${encodeURIComponent(packageName)}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const reviews = await response.json();
        displayReviews(reviews);
    } catch (error) {
        console.error('Error filtering reviews:', error);
        document.getElementById('reviewsList').innerHTML = `<p style="color: red;">Error loading reviews: ${error.message}</p>`;
    }
}

function displayReviews(reviews) {
    const container = document.getElementById('reviewsList');
    if (reviews.length === 0) {
        container.innerHTML = '<p>No reviews found.</p>';
        return;
    }

    container.innerHTML = reviews.map((review) => {
        const username = review.user ? review.user.username : 'Unknown User';

        // FIX: Try policyName first, then packageName (matching your Java Entity field)
        const pkg = review.insurancePackage;
        const packageName = pkg ? (pkg.policyName || pkg.packageName || 'Unknown Package') : 'Unknown Package';

        return `
            <div class="review-card">
                <div class="review-header">
                    <div>
                        <strong>${username}</strong> | <span class="badge">Package: ${packageName}</span>
                        <div class="review-rating">${'⭐'.repeat(review.rating)}</div>
                    </div>
                    <div class="review-actions">
                        <button class="btn btn-warning" onclick="editReview(${review.id}, '${review.comment.replace(/'/g, "\\'")}', ${review.rating})">Edit</button>
                        <button class="btn btn-danger" onclick="deleteReview(${review.id})">Delete</button>
                    </div>
                </div>
                <p>${review.comment}</p>
            </div>
        `;
    }).join('');
}

async function editReview(reviewId, currentComment, currentRating) {
    const newComment = prompt('Edit your comment:', currentComment);
    const newRatingInput = prompt('Edit your rating (1-5):', currentRating);
    const newRating = parseInt(newRatingInput);

    if (newComment !== null && newRatingInput !== null && !isNaN(newRating) && newRating >= 1 && newRating <= 5) {
        await updateReview(reviewId, newComment, newRating);
    }
}

async function updateReview(reviewId, comment, rating) {
    try {
        const response = await fetch(`${API_BASE}/update/${reviewId}?comment=${encodeURIComponent(comment)}&rating=${rating}`, {
            method: 'PUT'
        });

        if (!response.ok) {
            throw new Error('Update failed');
        }

        const result = await response.json();
        alert('Review updated successfully');
        await loadAllReviews();
    } catch (error) {
        console.error('Update error:', error);
        alert('Error updating review: ' + error.message);
    }
}

async function deleteReview(reviewId) {
    if (!confirm('Are you sure you want to delete this review?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/delete/${reviewId}`, {
            method: 'DELETE'
        });

        const result = await response.text();
        alert(result);
        await loadAllReviews();
    } catch (error) {
        console.error('Delete error:', error);
        alert('Error deleting review: ' + error.message);
    }
}

document.getElementById('reviewRating').addEventListener('input', function() {
    const rating = parseInt(this.value) || 0;
    document.getElementById('ratingStars').textContent = '⭐'.repeat(Math.min(Math.max(rating, 0), 5));
});