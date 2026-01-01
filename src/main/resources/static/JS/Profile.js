// API + current user context
const API_BASE = 'http://localhost:8080/api/profile';
function getCurrentUserEmail() { return localStorage.getItem('currentUserEmail') || ''; }
function getCurrentUserName()  { return localStorage.getItem('currentUserName')  || 'User'; }
document.getElementById('username').textContent = getCurrentUserName();
document.getElementById('logoutBtn').addEventListener('click', () => {
    localStorage.removeItem('currentUserEmail');
    localStorage.removeItem('currentUserName');
    window.location.href = 'index.html';
});

// Sri Lanka validators (same as your original)  :contentReference[oaicite:5]{index=5}
const nicOldRegex = /^[0-9]{9}[vVxX]$/;
const nicNewRegex = /^[0-9]{12}$/;
const phoneRegex  = /^(?:0|\+94)\d{9}$/;
const setError = (id, msg='') => { const el = document.getElementById(id); if (el) el.textContent = msg; };
function validate(form) {
    let ok = true;
    setError('fullNameErr'); setError('dobErr'); setError('nicErr'); setError('phoneErr'); setError('addressErr');
    if (form.fullName.value.trim().length < 3) { setError('fullNameErr','Please enter your full name.'); ok=false; }
    if (!form.dob.value) { setError('dobErr','Please select your date of birth.'); ok=false; }
    const nic = form.nic.value.trim();
    if (!(nicOldRegex.test(nic) || nicNewRegex.test(nic))) { setError('nicErr','Enter a valid NIC.'); ok=false; }
    if (!phoneRegex.test(form.phone.value.trim())) { setError('phoneErr','Enter a valid Sri Lankan number.'); ok=false; }
    if (form.address.value.trim().length < 5) { setError('addressErr','Please enter your full address.'); ok=false; }
    return ok;
}

// UI helpers
const form = document.getElementById('profileForm');
const formCard = document.getElementById('formCard');
const pvCard = document.getElementById('previewCard');
const pv = (id, val) => document.getElementById(id).textContent = val || '—';
function showPreview(dto) {
    pv('pvName', dto.fullName);
    pv('pvDob', dto.dob || '');
    pv('pvNic', dto.nic);
    pv('pvPhone', dto.phone || '');
    pv('pvAddress', dto.address || '');
    if (dto.photoBase64) document.getElementById('pvAvatar').src = dto.photoBase64;
    pvCard.classList.remove('hidden');
    formCard.classList.add('hidden');
}

// Load existing profile
async function loadProfile() {
    const email = getCurrentUserEmail();
    if (!email) return;
    const res = await fetch(`${API_BASE}/me?email=${encodeURIComponent(email)}`);
    const payload = await res.json();
    if (payload?.success && payload.data) {
        const d = payload.data;
        document.getElementById('fullName').value = d.fullName || '';
        document.getElementById('dob').value      = d.dob || '';
        document.getElementById('nic').value      = d.nic || '';
        document.getElementById('phone').value    = d.phone || '';
        document.getElementById('address').value  = d.address || '';
        showPreview(d);  // hide form and show preview
    }
}

// Submit (save)
form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!validate(form)) return;

    const email = getCurrentUserEmail();
    const fd = new FormData();
    fd.append('email', email);
    fd.append('fullName', form.fullName.value.trim());
    fd.append('nic', form.nic.value.trim());
    fd.append('dob', form.dob.value);
    fd.append('phone', form.phone.value.trim());
    fd.append('address', form.address.value.trim());
    if (form.image.files[0]) fd.append('image', form.image.files[0]);

    const res = await fetch(`${API_BASE}/me`, { method: 'PUT', body: fd });
    const payload = await res.json();
    if (payload?.success) {
        showPreview(payload.data);
        alert('Profile saved successfully ✅');
    } else {
        alert(payload?.message || 'Failed to save profile');
    }
});

document.getElementById('clearBtn').addEventListener('click', () => {
    form.reset();
});

// Edit → show form again
document.getElementById('editBtn').addEventListener('click', () => {
    pvCard.classList.add('hidden');
    formCard.classList.remove('hidden');
});

// Init
loadProfile();
