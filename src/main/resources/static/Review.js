const API_BASE = 'http://localhost:8080/reviews';
let currentUserId = null;

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

async function registerUser() {
    const nameField = document.getElementById('regName');
    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const name = nameField ? nameField.value : username;

    if (!username || !email || !password) {
        showAlert('registerAlert', 'Please fill all fields', 'error');
        return;
    }

    if (nameField && !name) {
        showAlert('registerAlert', 'Please enter your name', 'error');
        return;
    }

    try {
        console.log('Sending registration request...');
        console.log('Data:', {name, username, email});

        const response = await fetch(`${API_BASE}/addUser`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({name, username, email, password})
        });

        console.log('Response status:', response.status);

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Registration failed:', errorText);
            throw new Error(errorText || `Server error: ${response.status}`);
        }

        const result = await response.json();
        console.log('Registration successful:', result);

        showAlert('registerAlert', `User registered successfully: ${result.username}`, 'success');

        if (nameField) nameField.value = '';
        document.getElementById('regUsername').value = '';
        document.getElementById('regEmail').value = '';
        document.getElementById('regPassword').value = '';
    } catch (error) {
        console.error('Registration error:', error);
        showAlert('registerAlert', 'Error: ' + error.message, 'error');
    }
}

async function loginUser() {
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    if (!username || !password) {
        showAlert('loginAlert', 'Please enter username and password', 'error');
        return;
    }

    try {
        console.log('Attempting login for:', username);

        const response = await fetch(`${API_BASE}/login?username=${username}&password=${password}`, {
            method: 'POST'
        });

        console.log('Response status:', response.status);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Login failed');
        }

        const user = await response.json();
        console.log('Login response:', user);

        if (user && user.id) {
            currentUserId = user.id;
            showAlert('loginAlert', `Welcome ${user.username}!`, 'success');
            document.getElementById('currentUser').textContent = user.username;
            document.getElementById('userInfoDiv').style.display = 'block';
            console.log('Login successful, User ID:', currentUserId);
        } else {
            showAlert('loginAlert', 'Invalid credentials', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showAlert('loginAlert', 'Error: ' + error.message, 'error');
    }
}

async function addReview() {
    const packageName = document.getElementById('reviewPackageName').value;
    const rating = document.getElementById('reviewRating').value;
    const comment = document.getElementById('reviewComment').value;

    if (!currentUserId) {
        showAlert('addReviewAlert', 'Please login first', 'error');
        return;
    }

    if (!packageName || !rating || !comment) {
        showAlert('addReviewAlert', 'Please fill all fields', 'error');
        return;
    }

    if (rating < 1 || rating > 5) {
        showAlert('addReviewAlert', 'Rating must be between 1 and 5', 'error');
        return;
    }

    try {
        console.log('Adding review...');

        const response = await fetch(`${API_BASE}/add?userId=${currentUserId}&packageName=${encodeURIComponent(packageName)}&comment=${encodeURIComponent(comment)}&rating=${rating}`, {
            method: 'POST'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Failed to add review');
        }

        const result = await response.json();
        console.log('Review added:', result);

        showAlert('addReviewAlert', 'Review added successfully!', 'success');
        document.getElementById('reviewPackageName').value = '';
        document.getElementById('reviewRating').value = '';
        document.getElementById('reviewComment').value = '';
        document.getElementById('ratingStars').textContent = '';
    } catch (error) {
        console.error('Review error:', error);
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
        const username = review.user ? review.user.username : 'Unknown';
        const packageName = review.insurancePackage ? review.insurancePackage.packageName : 'Unknown';

        return `
            <div class="review-card">
                <div class="review-header">
                    <div>
                        <strong>${username}</strong> | Package: ${packageName}
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