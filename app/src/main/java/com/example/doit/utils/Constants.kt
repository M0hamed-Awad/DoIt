package com.example.doit.utils

import com.example.doit.R
import com.example.doit.models.TaskStatus

class Constants {
    companion object {
        val tabs = mapOf(
            TaskStatus.IN_PROGRESS.status to R.drawable.ic_in_progress_task_40,
            TaskStatus.COMPLETED.status to R.drawable.ic_complete_task_40,
            TaskStatus.OVERDUE.status to R.drawable.ic_overdue_task_40,
        )
    }
}