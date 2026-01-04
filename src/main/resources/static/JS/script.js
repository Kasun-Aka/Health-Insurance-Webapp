/***** Cookie Management *****/
function acceptAllCookies() {
    document.getElementById('cookieModal')?.classList.remove('show');
    sessionStorage.setItem('cookieConsent', 'accepted');
    sessionStorage.setItem('cookieConsentDate', new Date().toISOString());
    showNotification('All cookies accepted', 'success');
}

function rejectAllCookies() {
    document.getElementById('cookieModal')?.classList.remove('show');
    sessionStorage.setItem('cookieConsent', 'rejected');
    sessionStorage.setItem('cookieConsentDate', new Date().toISOString());
    showNotification('All cookies rejected', 'info');
}

function showCookieSettings() {
    showNotification('Cookie settings would open here', 'info');
}

function showCookiePolicy() {
    const w = window.open('', '_blank', 'width=600,height=800,scrollbars=yes,resizable=yes');
    w.document.write(`<!doctype html>
  <html><head><title>Cookie Policy — CeyMed</title>
  <style>body{font-family:Arial,sans-serif;padding:20px;line-height:1.6}h1{color:#1976d2}h2{color:#424242;margin-top:30px}p{margin-bottom:15px}</style>
  </head><body>
    <h1>Cookie Policy</h1>
    <h2>What are cookies?</h2><p>Cookies are small text files stored on your device.</p>
    <h2>How we use cookies</h2><p>Improve experience, analytics, personalization.</p>
    <h2>Types of cookies</h2><p><strong>Essential</strong>, <strong>Analytics</strong>, <strong>Marketing</strong></p>
    <h2>Managing cookies</h2><p>Use your browser settings or our preferences center.</p>
  </body></html>`);
}

/***** Toast helper *****/
function showNotification(message, type = 'success') {
    document.querySelectorAll('.notification').forEach(n => n.remove());
    const notification = document.createElement('div');
    notification.className = 'notification';
    const colors = {success: '#4caf50', error: '#f44336', info: '#2196f3', warning: '#ff9800'};
    notification.style.cssText = `
    position: fixed; top: 20px; right: 20px;
    background: ${colors[type] || colors.info}; color: #fff;
    padding: 1rem 1.25rem; border-radius: 8px; z-index: 10000;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15); font-weight: 500; max-width: 360px;`;
    notification.textContent = message;
    document.body.appendChild(notification);
    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(100%)';
        setTimeout(() => notification.remove(), 300);
    }, 3500);
}



/***** API + Paths *****/
const API_BASE = 'http://localhost:8080/api/auth';
const DASHBOARD_URL = '../Dashboard/Dashboard.html';

/***** Modal + Forms (API-backed auth) *****/
function showLogin() {
    // If already open, don't duplicate
    if (document.querySelector('.modal-overlay')) return;

    const modal = document.createElement('div');
    modal.className = 'modal-overlay';
    modal.style.cssText = `
    position:fixed; inset:0; background:rgba(0,0,0,.5);
    display:flex; justify-content:center; align-items:center;
    z-index:10000; backdrop-filter:blur(5px);`;

    modal.innerHTML = `
  <div style="background:#fff; padding: 1.75rem; border-radius: 16px; width: 92%; max-width: 440px; position:relative; box-shadow:0 10px 30px rgba(0,0,0,.25)">
    <button type="button" aria-label="Close" style="position:absolute; top:10px; right:15px; background:none; border:none; font-size:24px; cursor:pointer; color:#666;">×</button>

    <div style="display:flex; gap:.5rem; margin-bottom:1rem; border:1px solid #e0e0e0; border-radius:12px; padding:.25rem;">
      <button id="tabLogin" class="tab-btn" style="flex:1; border:none; background:#1976d2; color:#fff; padding:.65rem; border-radius:10px; font-weight:600; cursor:pointer">Login</button>
      <button id="tabRegister" class="tab-btn" style="flex:1; border:none; background:transparent; color:#1976d2; padding:.65rem; border-radius:10px; font-weight:600; cursor:pointer">Register</button>
      <button id="tabAdminLogin" class="tab-btn" style="flex:1; border:none; background:transparent; color:#1976d2; padding:.65rem; border-radius:10px; font-weight:600; cursor:pointer">Admin Login</button>
    </div>

    <!-- LOGIN -->
    <form id="loginForm" autocomplete="on" style="display:block">
      <label style="display:block; margin:.5rem 0 .25rem; font-weight:600;">Gmail</label>
      <input type="email" id="loginEmail" placeholder="example@gmail.com" required
             style="width:100%; padding:.7rem .9rem; border:1px solid #cfd8dc; border-radius:10px; outline:none;">
      <label style="display:block; margin:.75rem 0 .25rem; font-weight:600;">Password</label>
      <input type="password" id="loginPassword" placeholder="••••••••" required minlength="6"
             style="width:100%; padding:.7rem .9rem; border:1px solid #cfd8dc; border-radius:10px; outline:none;">
      <button type="submit" class="btn btn-primary" style="margin-top:1rem; width:100%; padding:.8rem; border:none; border-radius:12px; background:linear-gradient(45deg,#1976d2,#42a5f5); color:#fff; font-weight:700; cursor:pointer;">Login</button>
    </form>

    <!-- REGISTER -->
    <form id="registerForm" autocomplete="on" style="display:none">
      <label style="display:block; margin:.5rem 0 .25rem; font-weight:600;">Username</label>
      <input type="text" id="regUsername" placeholder="Your name" required
             style="width:100%; padding:.7rem .9rem; border:1px solid #cfd8dc; border-radius:10px;">
      <label style="display:block; margin:.75rem 0 .25rem; font-weight:600;">Gmail</label>
      <input type="email" id="regEmail" placeholder="example@gmail.com" required
             style="width:100%; padding:.7rem .9rem; border:1px solid #cfd8dc; border-radius:10px;">
      <label style="display:block; margin:.75rem 0 .25rem; font-weight:600;">Password</label>
      <input type="password" id="regPassword" placeholder="min 6 characters" required minlength="6"
             style="width:100%; padding:.7rem .9rem; border:1px solid #cfd8dc; border-radius:10px;">
      <button type="submit" class="btn btn-primary" style="margin-top:1rem; width:100%; padding:.8rem; border:none; border-radius:12px; background:linear-gradient(45deg,#1976d2,#42a5f5); color:#fff; font-weight:700; cursor:pointer;">Create Account</button>
    </form>

    <!--ADMIN LOGIN -->
        <form id="adminLoginForm" autocomplete="on" style="display:none">
          <button type="submit" class="btn btn-primary" style="margin-top:1rem; width:100%; padding:.8rem; border:none; border-radius:12px; background:linear-gradient(45deg,#1976d2,#42a5f5); color:#fff; font-weight:700; cursor:pointer;">Admin Portal</button>
        </form>
  </div>`;

    document.body.appendChild(modal);

    // events
    const closeBtn = modal.querySelector('button[aria-label="Close"]');
    const tabLogin = modal.querySelector('#tabLogin');
    const tabAdminLogin = modal.querySelector('#tabAdminLogin');
    const tabRegister = modal.querySelector('#tabRegister');
    const loginForm = modal.querySelector('#loginForm');
    const registerForm = modal.querySelector('#registerForm');
    const adminLoginForm = modal.querySelector('#adminLoginForm');

    // switch tabs
    tabLogin.addEventListener('click', () => {
        tabLogin.style.background = '#1976d2';
        tabLogin.style.color = '#fff';
        tabRegister.style.background = 'transparent';
        tabRegister.style.color = '#1976d2';
        tabAdminLogin.style.background = 'transparent';
        tabAdminLogin.style.color = '#1976d2';
        loginForm.style.display = 'block';
        registerForm.style.display = 'none';
        adminLoginForm.style.display = 'none';
    });
    tabRegister.addEventListener('click', () => {
        tabRegister.style.background = '#1976d2';
        tabRegister.style.color = '#fff';
        tabLogin.style.background = 'transparent';
        tabLogin.style.color = '#1976d2';
        tabAdminLogin.style.background = 'transparent';
        tabAdminLogin.style.color = '#1976d2';
        loginForm.style.display = 'none';
        registerForm.style.display = 'block';
        adminLoginForm.style.display = 'none';
    });
    tabAdminLogin.addEventListener('click', () => {
            tabAdminLogin.style.background = '#1976d2';
            tabAdminLogin.style.color = '#fff';
            tabRegister.style.background = 'transparent';
            tabRegister.style.color = '#1976d2';
            tabLogin.style.background = 'transparent';
            tabLogin.style.color = '#1976d2';
            adminLoginForm.style.display = 'block';
            registerForm.style.display = 'none';
            loginForm.style.display = 'none';
        });

    // close
    closeBtn.addEventListener('click', () => modal.remove());
    modal.addEventListener('click', (e) => {
        if (e.target === modal) modal.remove();
    });

    /***** LOGIN — API backed *****/
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = modal.querySelector('#loginEmail').value.trim();
        const password = modal.querySelector('#loginPassword').value;

        try {
            const res = await fetch(`${API_BASE}/login`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
                body: JSON.stringify({email, password})
            });

            const data = await res.json().catch(() => ({}));
            if (!res.ok || !data.success || !data.token) {
                const msg = (data?.user && data.user.message) || data?.message || 'Login failed';
                throw new Error(msg);
            }
// Save session info from server (updated format for viewclaims compatibility)
            localStorage.setItem('authToken', data.token);  // Changed from 'auth_token'
            localStorage.setItem('auth_token', data.token); // Keep this for backward compatibility

// Create userSession object with userId
            const userSession = {
                userId: data.user.id,  // Make sure your API returns user.id
                username: data.user.name || data.user.username || 'User',
                email: data.user.email
            };
            localStorage.setItem('userSession', JSON.stringify(userSession));  // New format

// Keep existing format for backward compatibility
            localStorage.setItem('auth_user', JSON.stringify(data.user));
            localStorage.setItem('currentUserName', data.user.name || data.user.username || 'User');
            localStorage.setItem('currentUserEmail', data.user.email);

            showNotification('Login successful. Redirecting...', 'success');
            setTimeout(() => {
                modal.remove();
                window.location.href = "Dashboard.html";
            }, 800);
        } catch (err) {
            showNotification(err.message || 'Login failed', 'error');
        }
    });

    /***** REGISTER — API backed *****/
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = modal.querySelector('#regUsername').value.trim();
        const email = modal.querySelector('#regEmail').value.trim();
        const password = modal.querySelector('#regPassword').value;

        if (!username || !email || !password) {
            showNotification('Please fill all fields', 'warning');
            return;
        }

        try {
            const res = await fetch(`${API_BASE}/register`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
                body: JSON.stringify({username, email, password})
            });

            const data = await res.json().catch(() => ({}));
            if (!res.ok || !data.success) {
                throw new Error(data?.message || 'Registration failed');
            }

            showNotification('Account created successfully. Please login.', 'success');
            modal.querySelector('#regUsername').value = '';
            modal.querySelector('#regEmail').value = '';
            modal.querySelector('#regPassword').value = '';
            tabLogin.click();
        } catch (err) {
            showNotification(err.message || 'Registration failed', 'error');
        }
    });

    /***** ADMIN LOGIN — API backed *****/
    adminLoginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            window.location.href = "AdminDashboard.html";
    });
}



/***** Placeholder handlers for your header buttons *****/
function showFAQ() {
    showNotification('FAQ page coming soon.', 'info');
}

function showContact() {
    showNotification('Contact form coming soon.', 'info');
}

function showPolicies() {
    window.location.href = "policy.html";
}

/***** Optional: show cookie banner once on first load *****/
window.addEventListener('DOMContentLoaded', () => {
    const consent = sessionStorage.getItem('cookieConsent');
    const modal = document.getElementById('cookieModal');
    if (!consent && modal) modal.classList.add('show');
});
