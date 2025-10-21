class MinHeap {
    constructor() {
        this.heap = [];
    }

    insert(task) {
        this.heap.push(task);
        this.heapifyUp(this.heap.length - 1);
    }

    extractMin() {
        if (this.heap.length === 0) return null;
        
        const min = this.heap[0];
        const last = this.heap.pop();
        
        if (this.heap.length > 0) {
            this.heap[0] = last;
            this.heapifyDown(0);
        }
        
        return min;
    }

    peek() {
        return this.heap.length > 0 ? this.heap[0] : null;
    }

    isEmpty() {
        return this.heap.length === 0;
    }

    size() {
        return this.heap.length;
    }

    getAllTasks() {
        return [...this.heap];
    }

    heapifyUp(index) {
        while (index > 0) {
            const parentIndex = Math.floor((index - 1) / 2);
            if (this.heap[index].priority >= this.heap[parentIndex].priority) {
                break;
            }
            this.swap(index, parentIndex);
            index = parentIndex;
        }
    }

    heapifyDown(index) {
        const size = this.heap.length;
        while (index < size) {
            let smallest = index;
            const leftChild = 2 * index + 1;
            const rightChild = 2 * index + 2;

            if (leftChild < size && this.heap[leftChild].priority < this.heap[smallest].priority) {
                smallest = leftChild;
            }

            if (rightChild < size && this.heap[rightChild].priority < this.heap[smallest].priority) {
                smallest = rightChild;
            }

            if (smallest === index) break;

            this.swap(index, smallest);
            index = smallest;
        }
    }

    swap(i, j) {
        [this.heap[i], this.heap[j]] = [this.heap[j], this.heap[i]];
    }

    // Rebuild heap from array of tasks
    buildHeap(tasks) {
        this.heap = [...tasks];
        for (let i = Math.floor(this.heap.length / 2); i >= 0; i--) {
            this.heapifyDown(i);
        }
    }
}

class TaskWeaverUI {
    constructor() {
        this.taskHeap = new MinHeap();
        this.allTasks = JSON.parse(localStorage.getItem('tasks')) || [];
        this.streak = parseInt(localStorage.getItem('streak')) || 0;
        this.currentView = 'all';
        
        // Build heap from existing tasks
        if (this.allTasks.length > 0) {
            const incompleteTasks = this.allTasks.filter(task => !task.completed);
            this.taskHeap.buildHeap(incompleteTasks);
        }
        
        this.init();
    }

    init() {
        this.bindEvents();
        this.render();
        this.updateStreak();
    }

    bindEvents() {
        document.getElementById('task-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.createTask();
        });

        document.querySelectorAll('.view-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                document.querySelectorAll('.view-btn').forEach(b => b.classList.remove('active'));
                e.target.classList.add('active');
                this.currentView = e.target.dataset.view;
                this.renderTasks();
            });
        });

        // Set minimum date to today
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('task-due-date').min = today;
    }

    createTask() {
        const title = document.getElementById('task-title').value;
        const priority = parseInt(document.getElementById('task-priority').value);
        const dueDate = document.getElementById('task-due-date').value;

        const task = {
            id: Date.now(),
            title,
            priority,
            dueDate,
            completed: false,
            createdAt: new Date().toISOString()
        };

        // Add to both heap and all tasks
        this.taskHeap.insert(task);
        this.allTasks.push(task);
        
        this.saveTasks();
        this.render();
        document.getElementById('task-form').reset();
        
        this.showNotification('Task created successfully!', 'success');
    }

    completeTask(taskId) {
        const taskIndex = this.allTasks.findIndex(t => t.id === taskId);
        if (taskIndex !== -1 && !this.allTasks[taskIndex].completed) {
            this.allTasks[taskIndex].completed = true;
            this.allTasks[taskIndex].completedAt = new Date().toISOString();
            
            // Rebuild heap with only incomplete tasks
            const incompleteTasks = this.allTasks.filter(t => !t.completed);
            this.taskHeap.buildHeap(incompleteTasks);
            
            this.updateStreak();
            this.saveTasks();
            this.render();
            this.showNotification('Task completed! Streak updated!', 'success');
        }
    }

    deleteTask(taskId) {
        if (confirm('Are you sure you want to delete this task?')) {
            this.allTasks = this.allTasks.filter(t => t.id !== taskId);
            
            // Rebuild heap with only incomplete tasks
            const incompleteTasks = this.allTasks.filter(t => !t.completed);
            this.taskHeap.buildHeap(incompleteTasks);
            
            this.saveTasks();
            this.render();
            this.showNotification('Task deleted successfully!', 'info');
        }
    }

    updateStreak() {
        const completedDates = this.allTasks
            .filter(t => t.completed && t.completedAt)
            .map(t => t.completedAt.split('T')[0])
            .filter((date, index, array) => array.indexOf(date) === index)
            .sort()
            .reverse();

        let streak = 0;
        let currentDate = new Date();
        currentDate.setHours(0, 0, 0, 0);
        
        for (let i = 0; i < completedDates.length; i++) {
            const taskDate = new Date(completedDates[i]);
            taskDate.setHours(0, 0, 0, 0);
            
            const diffTime = currentDate - taskDate;
            const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
            
            if (diffDays === i) {
                streak = i + 1;
            } else {
                break;
            }
        }

        this.streak = streak;
        localStorage.setItem('streak', streak.toString());
        this.renderStreak();
    }

    renderStreak() {
        document.getElementById('streak-count').textContent = this.streak;
    }

    getAlertLevel(dueDate) {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        const due = new Date(dueDate);
        due.setHours(0, 0, 0, 0);
        
        const timeDiff = due - today;
        const daysDiff = Math.ceil(timeDiff / (1000 * 60 * 60 * 24));

        if (daysDiff < 0) return 'red';    // Overdue
        if (daysDiff === 0) return 'red';  // Due today
        if (daysDiff <= 2) return 'orange'; // Due in 2 days
        return 'green';                    // Due in future
    }

    getFocusTask() {
        return this.taskHeap.peek();
    }

    render() {
        this.renderFocusTask();
        this.renderTasks();
        this.renderStreak();
    }

    renderFocusTask() {
        const focusTaskElement = document.getElementById('focus-task');
        const focusTask = this.getFocusTask();

        if (focusTask) {
            const alertLevel = this.getAlertLevel(focusTask.dueDate);
            focusTaskElement.innerHTML = `
                <div class="task-item ${alertLevel}">
                    <div class="task-content">
                        <div class="task-title">🎯 ${focusTask.title}</div>
                        <div class="task-meta">
                            <span><strong>Priority:</strong> ${focusTask.priority} (${this.getPriorityText(focusTask.priority)})</span>
                            <span><strong>Due:</strong> ${new Date(focusTask.dueDate).toLocaleDateString()}</span>
                            <span class="alert-badge alert-${alertLevel}">${alertLevel.toUpperCase()} ALERT</span>
                        </div>
                    </div>
                    <div class="task-actions">
                        <button class="complete-btn" onclick="taskWeaver.completeTask(${focusTask.id})">
                            Complete
                        </button>
                        <button class="delete-btn" onclick="taskWeaver.deleteTask(${focusTask.id})">
                            Delete
                        </button>
                    </div>
                </div>
            `;
        } else {
            focusTaskElement.innerHTML = '<p class="no-task">No tasks available. Add your first task!</p>';
        }
    }

    getPriorityText(priority) {
        switch(priority) {
            case 1: return 'Highest';
            case 2: return 'High';
            case 3: return 'Medium';
            case 4: return 'Low';
            case 5: return 'Lowest';
            default: return 'Unknown';
        }
    }

    renderTasks() {
        const tasksList = document.getElementById('tasks-list');
        let tasksToDisplay = [];

        switch (this.currentView) {
            case 'all':
                // For all tasks view, we want to show tasks in priority order
                // Create a temporary heap to get tasks in priority order
                const tempHeap = new MinHeap();
                const incompleteTasks = this.allTasks.filter(t => !t.completed);
                tempHeap.buildHeap(incompleteTasks);
                
                // Extract tasks in priority order
                while (!tempHeap.isEmpty()) {
                    tasksToDisplay.push(tempHeap.extractMin());
                }
                // Add completed tasks at the end
                tasksToDisplay = tasksToDisplay.concat(this.allTasks.filter(t => t.completed));
                break;
                
            case 'incomplete':
                // Get incomplete tasks in priority order using heap
                const incompleteHeap = new MinHeap();
                const incompleteTasksOnly = this.allTasks.filter(t => !t.completed);
                incompleteHeap.buildHeap(incompleteTasksOnly);
                
                while (!incompleteHeap.isEmpty()) {
                    tasksToDisplay.push(incompleteHeap.extractMin());
                }
                break;
                
            case 'urgent':
                const urgentTasks = this.allTasks.filter(t => 
                    !t.completed && this.getAlertLevel(t.dueDate) === 'red'
                );
                const urgentHeap = new MinHeap();
                urgentHeap.buildHeap(urgentTasks);
                
                while (!urgentHeap.isEmpty()) {
                    tasksToDisplay.push(urgentHeap.extractMin());
                }
                break;
                
            case 'completed':
                tasksToDisplay = this.allTasks.filter(t => t.completed);
                // Sort completed tasks by completion date (most recent first)
                tasksToDisplay.sort((a, b) => new Date(b.completedAt) - new Date(a.completedAt));
                break;
        }

        if (tasksToDisplay.length === 0) {
            tasksList.innerHTML = '<p class="no-task">No tasks found for this view.</p>';
            return;
        }

        tasksList.innerHTML = tasksToDisplay.map(task => {
            const alertLevel = this.getAlertLevel(task.dueDate);
            const completedClass = task.completed ? 'completed' : '';
            const priorityText = this.getPriorityText(task.priority);
            
            return `
                <div class="task-item ${completedClass} ${task.completed ? '' : alertLevel}">
                    <div class="task-content">
                        <div class="task-title">
                            ${task.title}
                            ${task.completed ? ' ✅' : ''}
                            ${!task.completed && task.priority === 1 ? ' 🔥' : ''}
                            ${!task.completed && task.priority === 2 ? ' ⭐' : ''}
                        </div>
                        <div class="task-meta">
                            <span><strong>Priority:</strong> ${task.priority} (${priorityText})</span>
                            <span><strong>Due:</strong> ${new Date(task.dueDate).toLocaleDateString()}</span>
                            ${!task.completed ? `
                                <span class="alert-badge alert-${alertLevel}">
                                    ${alertLevel.toUpperCase()} ALERT
                                </span>
                            ` : `
                                <span class="alert-badge" style="background: #95a5a6;">
                                    COMPLETED
                                </span>
                            `}
                        </div>
                    </div>
                    <div class="task-actions">
                        ${!task.completed ? `
                            <button class="complete-btn" onclick="taskWeaver.completeTask(${task.id})">
                                Complete
                            </button>
                        ` : ''}
                        <button class="delete-btn" onclick="taskWeaver.deleteTask(${task.id})">
                            Delete
                        </button>
                    </div>
                </div>
            `;
        }).join('');
    }

    showNotification(message, type) {
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.textContent = message;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 5px;
            color: white;
            font-weight: bold;
            z-index: 1000;
            animation: slideIn 0.3s ease;
        `;

        if (type === 'success') {
            notification.style.background = '#27ae60';
        } else if (type === 'info') {
            notification.style.background = '#3498db';
        }

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                document.body.removeChild(notification);
            }, 300);
        }, 3000);
    }

    saveTasks() {
        localStorage.setItem('tasks', JSON.stringify(this.allTasks));
    }
}

// Add CSS for notifications and priority indicators
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
    
    .priority-highlight {
        background: linear-gradient(90deg, #ff6b6b, #ff8e8e) !important;
        border-left: 5px solid #e74c3c !important;
    }
    
    .priority-high {
        background: linear-gradient(90deg, #f39c12, #f7b731) !important;
        border-left: 5px solid #f39c12 !important;
    }
`;
document.head.appendChild(style);

// Initialize the application
const taskWeaver = new TaskWeaverUI();