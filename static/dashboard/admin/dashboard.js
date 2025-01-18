// Constants
const ITEMS_PER_PAGE = 10;
let currentPage = 1;
let totalPages = 1;
let filteredTasks = [];

// Sample Data (In production, this would come from an API)
let tasks = [
    {
        id: "T1001",
        title: "Data Entry Task",
        category: "data_entry",
        priority: "high",
        status: "pending",
        assignedTo: null,
        description: "Process customer feedback forms"
    },
    // Add more sample tasks as needed
];

let users = [
    {
        id: "U1001",
        name: "John Smith",
        email: "john@afrikai.com",
        role: "user",
        status: "active",
        taskCount: 3
    },
    // Add more sample users as needed
];

// Initialize Dashboard
document.addEventListener('DOMContentLoaded', () => {
    initializeDashboard();
    setupEventListeners();
});

function initializeDashboard() {
    loadTasks();
    setupModals();
    setupFilters();
    updatePagination();
}

// Event Listeners Setup
function setupEventListeners() {
    // Create User Button
    document.querySelector('.create-user-btn').addEventListener('click', () => {
        openModal('createUserModal');
    });

    // Create User Form
    document.getElementById('createUserForm').addEventListener('submit', (e) => {
        e.preventDefault();
        createNewUser();
    });

    // Filters
    document.getElementById('applyFilters').addEventListener('click', applyFilters);
    
    // Search Input
    document.getElementById('taskSearch').addEventListener('input', debounce(handleSearch, 300));

    // Task Selection
    document.getElementById('taskTableBody').addEventListener('click', handleTaskSelection);
}

// Task Management Functions
function loadTasks() {
    filteredTasks = [...tasks]; // In production, this would be an API call
    renderTasks();
}

function renderTasks() {
    const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
    const endIndex = startIndex + ITEMS_PER_PAGE;
    const tasksToShow = filteredTasks.slice(startIndex, endIndex);

    const tableBody = document.getElementById('taskTableBody');
    tableBody.innerHTML = tasksToShow.map(task => `
        <tr data-task-id="${task.id}">
            <td>${task.id}</td>
            <td>${task.title}</td>
            <td>${formatCategory(task.category)}</td>
            <td>
                <span class="priority-badge priority-${task.priority}">${task.priority}</span>
            </td>
            <td>
                <span class="status-badge status-${task.status}">${formatStatus(task.status)}</span>
            </td>
            <td>${task.assignedTo ? getUserName(task.assignedTo) : 'Unassigned'}</td>
            <td>
                <div class="action-buttons">
                    <button onclick="viewTask('${task.id}')" class="action-btn view-btn">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button onclick="editTask('${task.id}')" class="action-btn edit-btn">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button onclick="assignTask('${task.id}')" class="action-btn assign-btn">
                        <i class="fas fa-user-plus"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');

    updatePaginationInfo(startIndex + 1, Math.min(endIndex, filteredTasks.length), filteredTasks.length);
}

// Filter Functions
function applyFilters() {
    const status = document.getElementById('statusFilter').value;
    const category = document.getElementById('categoryFilter').value;
    const priority = document.getElementById('priorityFilter').value;

    filteredTasks = tasks.filter(task => {
        return (!status || task.status === status) &&
               (!category || task.category === category) &&
               (!priority || task.priority === priority);
    });

    currentPage = 1;
    renderTasks();
    updatePagination();
}

function handleSearch(e) {
    const searchTerm = e.target.value.toLowerCase();
    
    filteredTasks = tasks.filter(task => 
        task.id.toLowerCase().includes(searchTerm) ||
        task.title.toLowerCase().includes(searchTerm)
    );

    currentPage = 1;
    renderTasks();
    updatePagination();
}

// User Management Functions
function createNewUser() {
    const userData = {
        name: document.getElementById('userName').value,
        email: document.getElementById('userEmail').value,
        role: document.getElementById('userRole').value
    };

    // In production, this would be an API call
    const newUser = {
        id: `U${Math.floor(Math.random() * 10000)}`,
        ...userData,
        status: 'active',
        taskCount: 0
    };

    users.push(newUser);
    closeModal('createUserModal');
    showNotification('User created successfully');
}

// Task Assignment Functions
function assignTask(taskId) {
    openTaskAssignmentModal(taskId);
}

function openTaskAssignmentModal(taskId) {
    const modal = document.getElementById('taskAssignmentModal');
    const task = tasks.find(t => t.id === taskId);
    
    if (!task) return;

    // Populate available users
    const usersList = document.getElementById('availableUsersList');
    usersList.innerHTML = users
        .filter(user => user.status === 'active' && user.role === 'user')
        .map(user => `
            <div class="user-item" data-user-id="${user.id}">
                <div class="user-info">
                    <strong>${user.name}</strong>
                    <span>${user.taskCount} active tasks</span>
                </div>
                <button class="select-user-btn">Select</button>
            </div>
        `).join('');

    modal.style.display = 'block';
}

// Utility Functions
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function formatCategory(category) {
    return category.split('_').map(word => 
        word.charAt(0).toUpperCase() + word.slice(1)
    ).join(' ');
}

function formatStatus(status) {
    return status.charAt(0).toUpperCase() + status.slice(1);
}

function getUserName(userId) {
    const user = users.find(u => u.id === userId);
    return user ? user.name : 'Unknown User';
}

function showNotification(message) {
    // Implementation of notification system
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Pagination Functions
function updatePagination() {
    totalPages = Math.ceil(filteredTasks.length / ITEMS_PER_PAGE);
    const pagination = document.querySelector('.pagination');
    
    let paginationHTML = `
        <button onclick="changePage(1)" ${currentPage === 1 ? 'disabled' : ''}>First</button>
        <button onclick="changePage(${currentPage - 1})" ${currentPage === 1 ? 'disabled' : ''}>Previous</button>
    `;

    for (let i = Math.max(1, currentPage - 2); i <= Math.min(totalPages, currentPage + 2); i++) {
        paginationHTML += `
            <button onclick="changePage(${i})" class="${currentPage === i ? 'active' : ''}">${i}</button>
        `;
    }

    paginationHTML += `
        <button onclick="changePage(${currentPage + 1})" ${currentPage === totalPages ? 'disabled' : ''}>Next</button>
        <button onclick="changePage(${totalPages})" ${currentPage === totalPages ? 'disabled' : ''}>Last</button>
    `;

    pagination.innerHTML = paginationHTML;
}

function changePage(page) {
    if (page < 1 || page > totalPages) return;
    currentPage = page;
    renderTasks();
    updatePagination();
}

function updatePaginationInfo(start, end, total) {
    document.getElementById('startRecord').textContent = start;
    document.getElementById('endRecord').textContent = end;
    document.getElementById('totalRecords').textContent = total;
}

// Modal Functions
function setupModals() {
    // Close modal when clicking outside
    window.onclick = function(event) {
        if (event.target.classList.contains('modal')) {
            event.target.style.display = 'none';
        }
    };

    // Close buttons
    document.querySelectorAll('.cancel-btn').forEach(button => {
        button.onclick = function() {
            const modal = button.closest('.modal');
            if (modal) modal.style.display = 'none';
        };
    });
}

function openModal(modalId) {
    document.getElementById(modalId).style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}
