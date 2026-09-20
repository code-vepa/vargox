function renderAuthUI() {
    const token = localStorage.getItem("token");
    const username = localStorage.getItem("username");
    const authContainer = document.getElementById("auth-container");

    if (!authContainer) return;

    if (token && username) {
        authContainer.innerHTML = `
            <span class="welcome-text">Signed in as <strong>${username}</strong></span>
            <button id="logout-btn" class="auth-btn">Logout</button>
        `;
        document.getElementById("logout-btn").addEventListener("click", logout);
    } else {
        authContainer.innerHTML = `
            <a href="auth.html" class="auth-btn">Sign In / Sign Up</a>
        `;
    }
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");
    window.location.href = "index.html";
}

renderAuthUI();