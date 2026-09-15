# FocusTask 🎯

FocusTask is an Android-based task management application developed using **Java and Android Studio**. It is designed to help users organize their daily tasks, set priorities and deadlines, track progress, and stay focused on their work.

## 📱 Project Overview

Managing multiple tasks can become difficult without a proper organization system. FocusTask provides a simple and user-friendly mobile application for creating, managing, prioritizing, and tracking tasks.

The application allows users to create tasks with descriptions, assign priorities and deadlines, mark tasks as completed, edit or delete tasks, filter tasks based on different conditions, and monitor overall progress through a dashboard.

## ✨ Features

### 📝 Task Management

- ➕ Add new tasks
- 📄 Add optional task descriptions
- ✏️ Edit existing tasks
- 🗑️ Delete tasks with confirmation
- ✅ Mark tasks as completed or active
- 💾 Save tasks locally

### ⭐ Priority Management

Tasks can be assigned one of three priority levels:

- 🔴 High
- 🟡 Medium
- 🟢 Low

### 📅 Deadline Management

- Set a deadline date for a task
- Add a specific time to the deadline
- Option to create a task with **No Deadline**
- Edit existing deadlines

### 🔍 Task Filtering

Users can filter tasks based on:

- All Tasks
- Active Tasks
- Completed Tasks
- High Priority
- Medium Priority
- Low Priority

### 📊 Dashboard & Statistics

FocusTask provides an overview of task progress through:

- Total Tasks
- Completed Tasks
- Active Tasks
- Completion Rate
- High Priority Tasks

The statistics are updated dynamically as tasks are added, completed, edited, or deleted.

### 🌙 Dark Mode

The application supports both:

- Light Mode
- Dark Mode

The selected theme is saved so that the user's preference is maintained.

### 💾 Local Data Persistence

FocusTask uses **SharedPreferences** to store task information locally on the device.

Task data includes:

- Task name
- Task description
- Completion status
- Priority
- Deadline

Tasks remain available after closing and reopening the application.

## 🛠️ Technologies Used

- **Java**
- **Android Studio**
- **XML**
- **Android SDK**
- **RecyclerView / Android UI Components**
- **SharedPreferences**
- **Material Components**
- **Git & GitHub**

## 🏗️ Project Structure

The main components of FocusTask include:

- `MainActivity.java` – Handles task management, dashboard statistics, filters, theme selection, and user interactions.
- `activity_main.xml` – Defines the main application interface.
- `task_item.xml` – Defines the layout of individual task cards.
- `task_background.xml` – Provides the background styling for task items.
- `SharedPreferences` – Handles local task and theme data persistence.

## 🔄 Application Workflow

```text
Launch Application
       ↓
   Dashboard
       ↓
 Enter Task Details
       ↓
 Select Priority
       ↓
 Set Deadline
       ↓
    Add Task
       ↓
 Task Appears in List
       ↓
 ┌───────────────┐
 │ Manage Task   │
 └───────────────┘
       ↓
Complete / Edit / Delete
       ↓
 Dashboard Statistics Updated
       ↓
 Data Saved Locally
