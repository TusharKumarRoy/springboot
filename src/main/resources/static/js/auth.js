const API_URL = 'http://localhost:8081/api';

function showLogin() {
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('registerForm').style.display = 'none';
    clearMessage();
}

function showRegister() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
    clearMessage();
}

function showMessage(message, type) {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = message;
    messageDiv.className = `message ${type}`;
    messageDiv.style.display = 'block';
    
    setTimeout(() => {
        clearMessage();
    }, 5000);
}

function clearMessage() {
    const messageDiv = document.getElementById('message');
    messageDiv.style.display = 'none';
    messageDiv.className = 'message';
}

async function register(event) {
    event.preventDefault();
    
    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const department = document.getElementById('regDepartment').value;
    const role = document.getElementById('regRole').value;
    
    try {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password, department, role })
        });
        
        if (response.ok) {
            showMessage('Registration successful! Please login.', 'success');
            setTimeout(() => showLogin(), 2000);
        } else {
            const error = await response.text();
            showMessage(error || 'Registration failed', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    }
}

async function login(event) {
    event.preventDefault();
    
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            
            console.log('Login successful');
            console.log('Username:', data.username);
            console.log('Email:', data.email);
            console.log('Role:', data.role);
            console.log('Token received:', data.token ? 'Yes' : 'No');
            
            // Store token and user info
            localStorage.setItem('token', data.token);
            localStorage.setItem('email', data.email);
            localStorage.setItem('role', data.role);
            
            showMessage('Login successful!', 'success');
            
            // Redirect to dashboard
            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 1000);
        } else {
            showMessage('Invalid username or password', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    }
}
