const API_BASE_URL = "http://localhost:8081/api";

const tabSignin = document.getElementById("tab-signin");
const tabSignup = document.getElementById("tab-signup");
const signinForm = document.getElementById("signin-form");
const signupForm = document.getElementById("signup-form");
const errorEl = document.getElementById("auth-error");
const successEl = document.getElementById("auth-success");

tabSignin.addEventListener("click", () => switchTab("signin"));
tabSignup.addEventListener("click", () => switchTab("signup"));

function switchTab(tab) {
    errorEl.style.display = "none";
    successEl.style.display = "none";

    if (tab === "signin") {
        tabSignin.classList.add("active");
        tabSignup.classList.remove("active");
        signinForm.style.display = "flex";
        signupForm.style.display = "none";
    } else {
        tabSignup.classList.add("active");
        tabSignin.classList.remove("active");
        signupForm.style.display = "flex";
        signinForm.style.display = "none";
    }
}

signinForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    errorEl.style.display = "none";

    const username = document.getElementById("signin-username").value;
    const password = document.getElementById("signin-password").value;

    try {
        const response = await fetch(`${API_BASE_URL}/users/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error("Invalid username or password");
        }

        const data = await response.json();

        // Store the token so future requests can use it
        localStorage.setItem("token", data.token);
        localStorage.setItem("username", data.username);
        localStorage.setItem("role", data.role);

        window.location.href = "index.html";

    } catch (err) {
        errorEl.style.display = "block";
        errorEl.textContent = err.message;
    }
});

signupForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    errorEl.style.display = "none";
    successEl.style.display = "none";

    const username = document.getElementById("signup-username").value;
    const password = document.getElementById("signup-password").value;

    try {
        const response = await fetch(`${API_BASE_URL}/users/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            const errData = await response.json().catch(() => null);
            throw new Error(errData?.error || "Registration failed");
        }

        successEl.style.display = "block";
        successEl.textContent = "Account created! You can now sign in.";
        signupForm.reset();
        switchTab("signin");

    } catch (err) {
        errorEl.style.display = "block";
        errorEl.textContent = err.message;
    }
});