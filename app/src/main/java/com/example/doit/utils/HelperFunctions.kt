package com.example.doit.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.doit.R
import com.example.doit.models.TaskStatus
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HelperFunctions {
    companion object {
        fun getTaskStatuesIcon(taskStatus: String) : Int{
            return when(taskStatus){
                TaskStatus.IN_PROGRESS.status -> R.drawable.ic_in_progress_task_40
                TaskStatus.COMPLETED.status -> R.drawable.ic_completed_task_40
                else -> R.drawable.ic_overdue_task_40
            }
        }

        fun getTaskStatuesColor(context: Context, taskStatus: String) : Int{
            return when(taskStatus){
                TaskStatus.IN_PROGRESS.status -> ContextCompat.getColor(context, R.color.in_progress_task_color)
                TaskStatus.COMPLETED.status -> ContextCompat.getColor(context, R.color.completed_task_color)
                else -> ContextCompat.getColor(context, R.color.overdue_task_color)
            }
        }

        fun parseDeadline(date: String, time: String): LocalDateTime {
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")
            return LocalDateTime.parse("$date $time", formatter)
        }

        fun TextInputEditText.validateEmptyField(layout: TextInputLayout, errorMsg: String): Boolean {
            return if (this.text.isNullOrBlank()) {
                layout.error = errorMsg
                false
            } else {
                layout.error = null
                true
            }
        }
    }
}