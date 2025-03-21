package com.example.doit.utils

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.doit.R
import com.example.doit.databinding.EditTaskBottomSheetBinding
import com.example.doit.models.TaskStatus
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HelperFunctions {
    companion object {
        fun getTaskStatuesIcon(taskStatus: String): Int {
            return when (taskStatus) {
                TaskStatus.IN_PROGRESS.status -> R.drawable.ic_in_progress_task_40
                TaskStatus.COMPLETED.status -> R.drawable.ic_completed_task_40
                else -> R.drawable.ic_overdue_task_40
            }
        }

        fun getTaskStatuesColor(context: Context, taskStatus: String): Int {
            return when (taskStatus) {
                TaskStatus.IN_PROGRESS.status -> ContextCompat.getColor(
                    context,
                    R.color.in_progress_task_color
                )

                TaskStatus.COMPLETED.status -> ContextCompat.getColor(
                    context,
                    R.color.completed_task_color
                )

                else -> ContextCompat.getColor(context, R.color.overdue_task_color)
            }
        }

        fun parseDeadline(date: String, time: String): LocalDateTime {
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
            return LocalDateTime.parse("$date $time", formatter)
        }

        fun TextInputEditText.validateEmptyField(
            layout: TextInputLayout,
            errorMsg: String
        ): Boolean {
            return if (this.text.isNullOrBlank()) {
                layout.error = errorMsg
                false
            } else {
                layout.error = null
                true
            }
        }


        fun validateEditTaskForm(binding: EditTaskBottomSheetBinding): Boolean {
            var isValid = true

            val title = binding.editTaskBottomSheetTaskTitleEt
            val description = binding.editTaskBottomSheetTaskDescEt

            // Validate Task Title and Description as not empty
            val isTitleNotEmpty =
                title.validateEmptyField(
                    binding.editTaskBottomSheetTitleTextInputLayout,
                    "Title is required"
                )
            val isDescriptionNotEmpty =
                description.validateEmptyField(
                    binding.editTaskBottomSheetDescTextInputLayout,
                    "Description is required"
                )

            if (!isTitleNotEmpty || !isDescriptionNotEmpty) {
                isValid = false
            }

            val date = binding.editTaskBottomSheetTaskDeadlineDateTv.text.toString()
            val time = binding.editTaskBottomSheetTaskDeadlineTimeTv.text.toString()

            // Validate Task Deadline
            if (!isTaskDeadlineValid(date, time, binding.root.context)) {
                isValid = false
            }

            return isValid
        }

        private fun isTaskDeadlineValid(date: String, time: String, context: Context): Boolean {
            if (date.isEmpty() || time.isEmpty()) {
                Toast.makeText(
                    context,
                    "Please select deadline date and time",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            } else {
                try {
                    val deadline = parseDeadline(date, time)

                    if (deadline.isBefore(LocalDateTime.now())) {
                        Toast.makeText(
                            context,
                            "Task Deadline must be in the future",
                            Toast.LENGTH_SHORT
                        ).show()
                        return false
                    }

                    return true

                } catch (e: Exception) {
                    Toast.makeText(context, "Invalid date/time format", Toast.LENGTH_SHORT)
                        .show()
                    return false
                }
            }
        }
    }
}