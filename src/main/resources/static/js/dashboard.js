const API_URL = 'http://localhost:8081/api';
let token = localStorage.getItem('token');
let userRole = localStorage.getItem('role');
let students = [];
let teachers = [];

// Check if user is logged in
if (!token) {
    console.log('No token found, redirecting to login');
    localStorage.clear();
    window.location.href = 'index.html';
}

// Verify token is valid on page load
async function verifyAuthentication() {
    try {
        console.log('Verifying authentication...');
        console.log('Stored role:', userRole);
        console.log('Token:', token ? 'Present' : 'Missing');
        
        const response = await fetch(`${API_URL}/students`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (response.status === 401 || response.status === 403) {
            console.error('Authentication failed:', response.status);
            alert('Your session is invalid. Please login again.');
            localStorage.clear();
            window.location.href = 'index.html';
            return false;
        }
        
        console.log('Authentication verified successfully');
        return true;
    } catch (error) {
        console.error('Verification error:', error);
        return false;
    }
}

// Handle authentication errors
function handleAuthError(response) {
    if (response.status === 401 || response.status === 403) {
        console.error('Auth error detected:', response.status, 'on', response.url);
        console.log('Current token:', token ? token.substring(0, 20) + '...' : 'None');
        console.log('Current role:', userRole);
        showToast('Session expired or unauthorized. Redirecting to login...', 'error');
        setTimeout(() => {
            localStorage.clear();
            window.location.href = 'index.html';
        }, 1500);
        return true;
    }
    return false;
}

// Display user info
document.getElementById('userEmail').textContent = localStorage.getItem('email');
document.getElementById('userRole').textContent = userRole;

// Show/hide admin-only features
if (userRole === 'ADMIN') {
    document.getElementById('addStudentBtn').style.display = 'block';
    document.getElementById('addTeacherBtn').style.display = 'block';
    document.getElementById('assignmentsTab').style.display = 'block';
}

// Verify authentication and load initial data
(async function() {
    const isAuthenticated = await verifyAuthentication();
    if (isAuthenticated) {
        loadStudents();
        loadTeachers();
    }
})();

function showSection(section, event) {
    // Prevent default link behavior
    if (event) {
        event.preventDefault();
    }
    
    // Hide all sections
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.sidebar a').forEach(a => a.classList.remove('active'));
    
    // Show selected section
    document.getElementById(`${section}Section`).classList.add('active');
    if (event && event.target) {
        event.target.classList.add('active');
    }
    
    if (section === 'assignments') {
        loadAssignmentData();
    }
}

function showToast(message, type) {
    const toast = document.getElementById('message');
    toast.textContent = message;
    toast.className = `toast ${type}`;
    
    setTimeout(() => {
        toast.className = 'toast';
    }, 3000);
}

function logout() {
    localStorage.clear();
    window.location.href = 'index.html';
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

// Students Functions
async function loadStudents() {
    try {
        const response = await fetch(`${API_URL}/students`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (handleAuthError(response)) return;
        
        if (response.ok) {
            students = await response.json();
            displayStudents();
        } else {
            showToast('Failed to load students', 'error');
        }
    } catch (error) {
        showToast('Network error', 'error');
    }
}

function displayStudents() {
    const container = document.getElementById('studentsList');
    
    if (students.length === 0) {
        container.innerHTML = '<div class="empty-state">No students found.</div>';
        return;
    }
    
    const isAdmin = userRole === 'ADMIN';
    
    container.innerHTML = students.map(student => `
        <div class="data-item">
            <div class="data-item-header">
                <h3>${student.username}</h3>
                <span class="badge badge-student">STUDENT</span>
            </div>
            <div class="data-item-body">
                <div class="info-row">
                    <span class="info-label">Email:</span>
                    <span class="info-value">${student.email}</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Department:</span>
                    <span class="info-value">${student.department}</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Assigned Teacher:</span>
                    <span class="info-value">
                        ${student.assignedTeacher?.username || '<em>Not assigned</em>'}
                        ${isAdmin && student.assignedTeacher ? `<button onclick="unassignStudent(${student.id})" class="btn-link" title="Remove assignment">✕</button>` : ''}
                    </span>
                </div>
            </div>
            ${isAdmin ? `
            <div class="data-item-actions">
                <button onclick="showEditUserModal(${student.id}, '${student.username}', '${student.email}', '${student.department}', 'STUDENT')" class="btn btn-secondary">Edit</button>
                <button onclick="deleteStudent(${student.id})" class="btn btn-danger">Delete</button>
            </div>
            ` : ''}
        </div>
    `).join('');
}

function showAddStudentModal() {
    document.getElementById('addStudentModal').classList.add('active');
}

async function addStudent(event) {
    event.preventDefault();
    
    const username = document.getElementById('studentUsername').value;
    const email = document.getElementById('studentEmail').value;
    const password = document.getElementById('studentPassword').value;
    const department = document.getElementById('studentDepartment').value;
    
    try {
        const response = await fetch(`${API_URL}/admin/users`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password, department, role: 'STUDENT' })
        });
        
        if (handleAuthError(response)) return;
        
        if (response.ok) {
            showToast('Student added successfully', 'success');
            closeModal('addStudentModal');
            loadStudents();
            event.target.reset();
        } else {
            const error = await response.text();
            showToast(error || 'Failed to add student', 'error');
        }
    } catch (error) {
        showToast('Network error', 'error');
    }
}

async function deleteStudent(id) {
    if (!confirm('Are you sure you want to delete this student?')) return;
    
    try {
        const response = await fetch(`${API_URL}/admin/users/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (handleAuthError(response)) return;
        
        if (response.ok) {
            showToast('Student deleted successfully', 'success');
            loadStudents();
            loadTeachers(); // Refresh teachers to update student counts
        } else {
            showToast('Failed to delete student', 'error');
        }
    } catch (error) {
        showToast('Network error', 'error');
    }
}

// Teachers Functions
async function loadTeachers() {
    try {
        const response = await fetch(`${API_URL}/teachers`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (handleAuthError(response)) return;
        
        if (response.ok) {
            teachers = await response.json();
            displayTeachers();
        } else {
            showToast('Failed to load teachers', 'error');
        }
    } catch (error) {
        showToast('Network error', 'error');
    }
}

function displayTeachers() {
    const container = document.getElementById('teachersList');
    
    if (teachers.length === 0) {
        container.innerHTML = '<div class="empty-state">No teachers found.</div>';
        return;
    }
    
    const isAdmin = userRole === 'ADMIN';
    
    container.innerHTML = teachers.map(teacher => `
        <div class="data-item">
            <div class="data-item-header">
                <h3>${teacher.username}</h3>
                <span class="badge badge-teacher">TEACHER</span>
            </div>
            <div class="data-item-body">
                <div class="info-row">
                    <span class="info-label">Email:</span>
                    <span class="info-value">${teacher.email}</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Department:</span>
                    <span class="info-value">${teacher.department}</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Assigned Students:</span>
                    <span class="info-value">${teacher.assignedStudents?.length || 0}</span>
                </div>
            </div>
            ${isAdmin ? `
            <div class="data-item-actions">
                <button onclick="showEditUserModal(${teacher.id}, '${teacher.username}', '${teacher.email}', '${teacher.department}', 'TEACHER')" class="btn btn-secondary">Edit</button>
                <button onclick="deleteTeacher(${teacher.id})" class="btn btn-danger">Delete</button>
            </div>
            ` : ''}
        </div>
    `).join('');
}

function showAddTeacherModal() {
    document.getElementById('addTeacherModal').classList.add('active');
}

async function addTeacher(event) {
    event.preventDefault();
    
    const username = document.getElementById('teacherUsername').value;
    const email = document.getElementById('teacherEmail').value;
    const password = document.getElementById('teacherPassword').value;
    const department = document.getElementById('teacherDepartment').value;
    
    try {
        const response = await fetch(`${API_URL}/admin/users`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password, department, role: 'TEACHER' })
        });
        
        if (handleAuthError(response)) return;
        
        if (response.ok) {
            showToast('Teacher added successfully', 'success');
            closeModal('addTeacherModal');
            loadTeachers();
            event.target.reset();
        } else {
            const error = await response.text();
            showToast(error || 'Failed to add teacher', 'error');
        }
    } catch (error) {
        showToast('Network error', 'error');
    }
}

async function deleteTeacher(id) {
    if (!confirm('Are you sure you want to delete this teacher?')) return;
    
    try {
        const response = await fetch(`${API_URL}/admin/users/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (handleAuthError(response)) return;
        
        if (response.ok) {
            showToast('Teacher deleted successfully', 'success');
            loadTeachers();
            loadStudents(); // Refresh students to update unassigned ones
        } else {
            showToast('Failed to delete teacher', 'error');
        }
    } catch (error) {
        showToast('Network error', 'error');
    }
}

// Assignment Functions
function loadAssignmentData() {
    const studentSelect = document.getElementById('assignStudent');
    const teacherSelect = document.getElementById('assignTeacher');
    
    const unassignedStudents = students.filter(s => !s.assignedTeacher);
    
    studentSelect.innerHTML = '<option value="">Select a student</option>' +
        unassignedStudents.map(s => 
            `<option value="${s.id}">${s.username} (${s.department})</option>`
        ).join('');
    
    teacherSelect.innerHTML = '<option value="">Select a teacher</option>' +
        teachers.map(t => 
            `<option value="${t.id}">${t.username} (${t.department})</option>`
        ).join('');
}

async function assignStudent() {
    const studentId = document.getElementById('assignStudent').value;
    const teacherId = document.getElementById('assignTeacher').value;
    
    if (!studentId || !teacherId) {
        showToast('Please select both student and teacher', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/admin/assign/${studentId}/to/${teacherId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (handleAuthError(response)) return;
        
        if (response.ok) {
            showToast('Student assigned successfully', 'success');
            loadStudents();
            loadTeachers();
            loadAssignmentData();
        } else {
            showToast('Failed to assign student', 'error');
        }
    } catch (error) {
        showToast('Network error', 'error');
    }
}

// Edit User Functions
function showEditUserModal(id, username, email, department, role) {
    document.getElementById('editUserId').value = id;
    document.getElementById('editUsername').value = username;
    document.getElementById('editEmail').value = email;
    document.getElementById('editDepartment').value = department;
    document.getElementById('editUserRole').value = role;
    document.getElementById('editRoleDisplay').value = role;
    
    // Show teacher assignment dropdown only for students
    const teacherAssignmentGroup = document.getElementById('teacherAssignmentGroup');
    if (role === 'STUDENT') {
        teacherAssignmentGroup.style.display = 'block';
        populateTeacherDropdown(id);
    } else {
        teacherAssignmentGroup.style.display = 'none';
    }
    
    document.getElementById('editUserModal').classList.add('active');
}

function populateTeacherDropdown(studentId) {
    const teacherSelect = document.getElementById('editTeacherAssignment');
    
    // Find the current student to get their assigned teacher
    const student = students.find(s => s.id == studentId);
    const currentTeacherId = student?.assignedTeacher?.id || '';
    
    // Build options: None + all teachers
    teacherSelect.innerHTML = '<option value="">None (Unassigned)</option>' +
        teachers.map(t => 
            `<option value="${t.id}" ${t.id == currentTeacherId ? 'selected' : ''}>${t.username} (${t.department})</option>`
        ).join('');
}

async function updateUser(event) {
    event.preventDefault();
    
    const id = document.getElementById('editUserId').value;
    const username = document.getElementById('editUsername').value;
    const email = document.getElementById('editEmail').value;
    const department = document.getElementById('editDepartment').value;
    const role = document.getElementById('editUserRole').value;
    const teacherId = document.getElementById('editTeacherAssignment').value;
    
    try {
        // Update user basic info
        const response = await fetch(`${API_URL}/admin/users/${id}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, department, role })
        });
        
        if (handleAuthError(response)) return;
        
        if (!response.ok) {
            const error = await response.text();
            showToast(error || 'Failed to update user', 'error');
            return;
        }
        
        // Handle teacher assignment for students
        if (role === 'STUDENT') {
            if (teacherId) {
                // Assign to teacher
                const assignResponse = await fetch(`${API_URL}/admin/assign/${id}/to/${teacherId}`, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                
                if (handleAuthError(assignResponse)) return;
                
                if (!assignResponse.ok) {
                    showToast('User updated but assignment failed', 'error');
                    return;
                }
            } else {
                // Unassign from teacher
                const unassignResponse = await fetch(`${API_URL}/admin/unassign/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                
                if (handleAuthError(unassignResponse)) return;
                // Don't show error if already unassigned
            }
        }
        
        showToast('User updated successfully', 'success');
        closeModal('editUserModal');
        loadStudents();
        loadTeachers();
    } catch (error) {
        showToast('Network error', 'error');
    }
}

// Unassign Student from Teacher
async function unassignStudent(studentId) {
    if (!confirm('Are you sure you want to unassign this student from their teacher?')) return;
    
    try {
        const response = await fetch(`${API_URL}/admin/unassign/${studentId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (handleAuthError(response)) return;
        
        if (response.ok) {
            showToast('Student unassigned successfully', 'success');
            loadStudents();
            loadTeachers();
        } else {
            showToast('Failed to unassign student', 'error');
        }
    } catch (error) {
        showToast('Network error', 'error');
    }
}

// Close modal when clicking outside
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.classList.remove('active');
    }
}
