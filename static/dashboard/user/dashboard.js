// Sample data - In a real application, this would come from an API
const tasks = [
    {
        id: 1,
        title: "Data Entry Task #1234",
        description: "Enter customer information into CRM system",
        dueDate: "2025-01-15",
        priority: "High",
        status: "In Progress",
        payment: 45.00
    },
    {
        id: 2,
        title: "Content Review #5678",
        description: "Review and validate product descriptions",
        dueDate: "2025-01-14",
        priority: "Medium",
        status: "Not Started",
        payment: 30.00
    },
    // Add more tasks as needed
];

const activities = [
    {
        id: 1,
        type: "completion",
        task: "Data Entry Task #1233",
        timestamp: "2025-01-11T10:30:00",
        payment: 45.00
    },
    {
        id: 2,
        type: "assignment",
        task: "Content Review #5678",
        timestamp: "2025-01-11T09:15:00"
    }
    // Add more activities as needed
];

// DOM Elements
const taskList = document.querySelector('.task-list');
const activityFeed = document.querySelector('.activity-feed');
const searchInput = document.querySelector('.search-bar input');

// Initialize Dashboard
document.addEventListener('DOMContentLoaded', () => {
    renderTasks();
    renderActivities();
    initializeSearchListener();
});

// Render Tasks
function renderTasks() {
    taskList.innerHTML = tasks.map(task => `
        <div class="task-card">
            <div class="task-header">
                <h3>${task.title}</h3>
                <span class="task-priority ${task.priority.toLowerCase()}">${task.priority}</span>
            </div>
            <p>${task.description}</p>
            <div class="task-meta">
                <span class="due-date">Due: ${formatDate(task.dueDate)}</span>
                <span class="status ${task.status.toLowerCase().replace(' ', '-')}">${task.status}</span>
            </div>
            <div class="task-actions">
                <button onclick="viewTask(${task.id})" class="view-btn">View Details</button>
                <span class="payment">$${task.payment.toFixed(2)}</span>
            </div>
        </div>
    `).join('');
}

// Render Activities
function renderActivities() {
    activityFeed.innerHTML = activities.map(activity => `
        <div class="activity-item">
            <i class="fas ${getActivityIcon(activity.type)}"></i>
            <div class="activity-content">
                <p>${formatActivity(activity)}</p>
                <span class="activity-time">${formatTimestamp(activity.timestamp)}</span>
            </div>
        </div>
    `).join('');
}

// Search Functionality
function initializeSearchListener() {
    searchInput.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        const filteredTasks = tasks.filter(task => 
            task.title.toLowerCase().includes(searchTerm) ||
            task.description.toLowerCase().includes(searchTerm)
        );
        renderFilteredTasks(filteredTasks);
    });
}

// Helper Functions
function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
    });
}

function formatTimestamp(timestamp) {
    return new Date(timestamp).toLocaleTimeString('en-US', {
        hour: 'numeric',
        minute: 'numeric'
    });
}

function getActivityIcon(type) {
    switch(type) {
        case 'completion':
            return 'fa-check-circle';
        case 'assignment':
            return 'fa-tasks';
        default:
            return 'fa-info-circle';
    }
}

function formatActivity(activity) {
    switch(activity.type) {
        case 'completion':
            return `Completed ${activity.task} - Earned $${activity.payment.toFixed(2)}`;
        case 'assignment':
            return `New task assigned: ${activity.task}`;
        default:
            return `Activity related to ${activity.task}`;
    }
}

// View Task Details
function viewTask(taskId) {
    window.location.href = `task-details.html?id=${taskId}`;
}

// Render Filtered Tasks
function renderFilteredTasks(filteredTasks) {
    if (filteredTasks.length === 0) {
        taskList.innerHTML = `<p>No tasks found.</p>`;
        return;
    }

    taskList.innerHTML = filteredTasks.map(task => `
        <div class="task-card">
            <div class="task-header">
                <h3>${task.title}</h3>
                <span class="task-priority ${task.priority.toLowerCase()}">${task.priority}</span>
            </div>
            <p>${task.description}</p>
            <div class="task-meta">
                <span class="due-date">Due: ${formatDate(task.dueDate)}</span>
                <span class="status ${task.status.toLowerCase().replace(' ', '-')}">${task.status}</span>
            </div>
            <div class="task-actions">
                <button onclick="viewTask(${task.id})" class="view-btn">View Details</button>
                <span class="payment">$${task.payment.toFixed(2)}</span>
            </div>
        </div>
    `).join('');
}

// Notification Badge Update (Optional)
function updateNotificationBadge() {
    const notificationBadge = document.querySelector('.notification-badge');
    const pendingTasks = tasks.filter(task => task.status === "Not Started" || task.status === "In Progress");
    notificationBadge.textContent = pendingTasks.length;
}

// Run Notification Update on Load
document.addEventListener('DOMContentLoaded', updateNotificationBadge);
