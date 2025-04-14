# DoIt - Task Management App

DoIt is a modern task management Android application designed to help users stay productive by organizing their tasks efficiently. Built using MVVM architecture, Room Database, LiveData, Coroutines, and Material Design, the app ensures a smooth and scalable experience.

## Features

- Add, Edit, Delete Tasks
- Set Task Deadline (Date & Time Picker)
- Tasks organized by Status: In Progress, Completed, Overdue
- Automatic status update when deadlines pass
- Real-time UI updates with Room + LiveData
- Beautiful Material UI with:
  - Bottom Navigation Bar
  - Floating Action Button (FAB)
  - Custom Popup Menus
  - TabLayout + ViewPager2
- Offline-First Storage using Room DB

## Architecture

- MVVM (Model-View-ViewModel)
- Repository Pattern
- Room Database + TypeConverters
- LiveData + ViewModel + Coroutines


## Technologies Used

- Kotlin
- Room Database
- LiveData
- ViewModel
- Coroutine
- MVVM Architecture
- Material Design Components
- ViewPager2 + TabLayout
- Popup Menu
- DatePicker & TimePicker



## How to Run

1. Clone the project:
   git clone https://github.com/your-username/DoIt.git

2. Open in Android Studio

3. Build the project and Run on emulator/device

## Future Improvements (Optional)

- Add Notifications/Reminders for Deadlines
- Firebase Integration (Auth + Backup)
- Dark Mode Theme Switcher
- Task Categories / Priorities
