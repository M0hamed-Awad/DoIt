package com.example.doit.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.doit.R
import com.example.doit.databinding.EditTaskBottomSheetBinding
import com.example.doit.models.TaskModel
import com.example.doit.models.TaskStatus
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.github.muddz.styleabletoast.StyleableToast
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class HelperFunctions {
    companion object {
        // Task Helper Functions

        fun getTaskStatuesIcon(taskStatus: String): Int {
            return when (taskStatus) {
                TaskStatus.IN_PROGRESS.status -> R.drawable.ic_in_progress_task_40
                TaskStatus.COMPLETED.status -> R.drawable.ic_complete_task_40
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

        fun changeTaskStatus(taskStatus: TaskStatus): TaskStatus {
            return if (taskStatus == TaskStatus.COMPLETED) {
                // If the Task already Completed them make it In Progress
                TaskStatus.IN_PROGRESS
            } else {
                // Else make it Completed
                TaskStatus.COMPLETED
            }
        }

        // Validation

        fun validateBottomsSheetEditTaskForm(binding: EditTaskBottomSheetBinding): Boolean {
            var isValid = true

            val titleEt = binding.editTaskBottomSheetTaskTitleEt
            val descriptionEt = binding.editTaskBottomSheetTaskDescEt

            // Validate Task Title and Description as not empty
            val isTitleNotEmpty =
                titleEt.validateEmptyField(
                    binding.editTaskBottomSheetTitleTextInputLayout,
                    "Title is required"
                )
            val isDescriptionNotEmpty =
                descriptionEt.validateEmptyField(
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
                StyleableToast.makeText(
                    context,
                    "Please select deadline date and time",
                    Toast.LENGTH_LONG,
                    R.style.error_toast_style
                ).show()
                return false
            } else {
                try {
                    val deadline = parseDeadline(date, time)

                    if (deadline.isBefore(LocalDateTime.now())) {
                        StyleableToast.makeText(
                            context,
                            "Task Deadline must be in the future",
                            Toast.LENGTH_LONG,
                            R.style.error_toast_style
                        ).show()
                        return false
                    }

                    return true

                } catch (e: Exception) {
                    StyleableToast.makeText(
                        context,
                        "Invalid date/time format",
                        Toast.LENGTH_LONG,
                        R.style.error_toast_style
                    ).show()
                    return false
                }
            }
        }

        // Date and Time Dialogs Helper Functions for Bottom Sheet Dialog

        fun initializeDatePicker(
            calendarDate: Calendar,
            dateTextView: TextView
        ): DatePickerDialog.OnDateSetListener {
            // Setting the Date Picker with a Certain Format ( Month / Day / Year ) -> ( 3/28/2025 )
            return DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendarDate.set(Calendar.YEAR, year)
                calendarDate.set(Calendar.MONTH, month)
                calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // Updating the Date Text View Text
                updateDateLabel(dateTextView, calendarDate)
            }
        }

        private fun updateDateLabel(dateTv: TextView, calendarDate: Calendar) {
            // Preparing the Date Format
            val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            dateTv.text = format.format(calendarDate.time)
        }

        fun initializeTimePicker(
            calendarTime: Calendar,
            timeTextView: TextView
        ): TimePickerDialog.OnTimeSetListener {
            // Setting the Date Picker with a Certain Format ( Hour:Minutes AM/PM ) -> ( 10:00 AM )
            return TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendarTime.set(Calendar.MINUTE, minute)
                calendarTime.timeZone = TimeZone.getDefault()
                // Updating the Time Text View Text
                updateTimeLabel(timeTextView, calendarTime)
            }
        }

        private fun updateTimeLabel(timeTv: TextView, calendarTime: Calendar) {
            // Preparing the Time Format
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            timeTv.text = format.format(calendarTime.time)
        }

        fun populateTaskDataToViews(
            task: TaskModel,
            bottomSheetBinding: EditTaskBottomSheetBinding
        ) {
            // Getting the Task Deadline components (date - time)
            val taskDeadlineTime = task.deadline.format(DateTimeFormatter.ofPattern("hh:mm a"))
            val taskDeadlineDate = task.deadline.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

            // Putting the Task Values to their Views
            bottomSheetBinding.editTaskBottomSheetTaskTitleEt.setText(task.title)
            bottomSheetBinding.editTaskBottomSheetTaskDescEt.setText(task.description)
            bottomSheetBinding.editTaskBottomSheetTaskDeadlineDateTv.text = taskDeadlineDate
            bottomSheetBinding.editTaskBottomSheetTaskDeadlineTimeTv.text = taskDeadlineTime
        }

        fun extractTaskInfoFromTextViews(bottomSheetBinding: EditTaskBottomSheetBinding): Triple<String, String, LocalDateTime> {
            // Setting up the Task Deadline components (date - time)
            val date =
                bottomSheetBinding.editTaskBottomSheetTaskDeadlineDateTv.text.toString().trim()
                    .trim()
            val time =
                bottomSheetBinding.editTaskBottomSheetTaskDeadlineTimeTv.text.toString().trim()

            // Getting Task Values from their Views

            val taskTitle = bottomSheetBinding.editTaskBottomSheetTaskTitleEt.text.toString().trim()
            val taskDescription = bottomSheetBinding.editTaskBottomSheetTaskDescEt.text.toString().trim()

            val taskDeadline = parseDeadline(date, time)

            return Triple(taskTitle, taskDescription, taskDeadline)
        }


    }
}