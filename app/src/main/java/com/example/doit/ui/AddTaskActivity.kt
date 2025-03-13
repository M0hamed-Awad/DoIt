package com.example.doit.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.doit.R
import com.example.doit.database.DatabaseProvider
import com.example.doit.databinding.ActivityAddTaskBinding
import com.example.doit.models.TaskModel
import com.example.doit.repository.TaskRepository
import com.example.doit.utils.HelperFunctions
import com.example.doit.utils.HelperFunctions.Companion.validateEmptyField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var datePicker: DatePickerDialog.OnDateSetListener
    private lateinit var timePicker: TimePickerDialog.OnTimeSetListener

    private val calendarDate = Calendar.getInstance()
    private val calendarTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_task)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_scroll_view)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dao = DatabaseProvider.getDatabase(this).getTaskDao()
        val repository = TaskRepository(dao)

        binding.addTaskActivityConfirmBtn.setOnClickListener {
            if (validateForm()){
                addNewTask(repository)
            }
        }

        binding.addTaskActivityCancelBtn.setOnClickListener { finish() }

        initializeDatePicker()
        initializeTimePicker()

        binding.selectDateLinearLayout.setOnClickListener {
            showDatePickerDialog(datePicker, calendarDate)
        }

        binding.selectTimeLinearLayout.setOnClickListener {
            showTimePickerDialog(timePicker, calendarTime)
        }
    }

    private fun initializeDatePicker() {

        datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendarDate.set(Calendar.YEAR, year)
            calendarDate.set(Calendar.MONTH, month)
            calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateLabel(calendarDate)
        }

    }

    private fun initializeTimePicker() {

        timePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendarTime.set(Calendar.MINUTE, minute)

            calendarTime.timeZone = TimeZone.getDefault()

            updateTimeLabel(calendarTime)
        }

    }

    private fun showDatePickerDialog(
        datePickerDialog: DatePickerDialog.OnDateSetListener,
        calendarDate: Calendar
    ) {
        DatePickerDialog(
            this,
            datePickerDialog,
            calendarDate.get(Calendar.YEAR),
            calendarDate.get(Calendar.MONTH),
            calendarDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }


    private fun showTimePickerDialog(
        timePickerDialog: TimePickerDialog.OnTimeSetListener,
        calendarTime: Calendar
    ) {
        TimePickerDialog(
            this,
            timePickerDialog,
            calendarTime.get(Calendar.HOUR_OF_DAY),
            calendarTime.get(Calendar.MINUTE),
            false,
        ).show()
    }

    private fun updateTimeLabel(calendarTime: Calendar) {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = format.format(calendarTime.time)
        binding.addTaskActivityTaskDeadlineTimeTv.text = time
    }

    private fun updateDateLabel(calendarDate: Calendar) {
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val date = format.format(calendarDate.time)
        binding.addTaskActivityTaskDeadlineDateTv.text = date
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val title = binding.addTaskActivityTaskTitleEt
        val description = binding.addTaskActivityTaskDescEt

        // Validate Task Title and Description as not empty
        val isTitleNotEmpty =
            title.validateEmptyField(binding.titleTextInputLayout, "Title is required")
        val isDescriptionNotEmpty =
            description.validateEmptyField(binding.descTextInputLayout, "Description is required")

        if (!isTitleNotEmpty || !isDescriptionNotEmpty) {
            isValid = false
        }

        val date = binding.addTaskActivityTaskDeadlineDateTv.text.toString()
        val time = binding.addTaskActivityTaskDeadlineTimeTv.text.toString()

        // Validate Task Deadline
        if (!isTaskDeadlineValid(date, time)) {
            isValid = false
        }

        return isValid
    }

    private fun isTaskDeadlineValid(date: String, time: String): Boolean {
        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(baseContext, "Please select deadline date and time", Toast.LENGTH_SHORT)
                .show()
            return false
        } else {
            try {
                val deadline = HelperFunctions.parseDeadline(date, time)

                if (deadline.isBefore(LocalDateTime.now())) {
                    Toast.makeText(
                        baseContext,
                        "Task Deadline must be in the future",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }

                return true

            } catch (e: Exception) {
                Toast.makeText(baseContext, "Invalid date/time format", Toast.LENGTH_SHORT).show()
                return false
            }
        }
    }

    private fun addNewTask(repository: TaskRepository) {
        val taskTitle = binding.addTaskActivityTaskTitleEt.text.toString()
        val taskDescription = binding.addTaskActivityTaskDescEt.text.toString()

        val date = binding.addTaskActivityTaskDeadlineDateTv.text.toString().trim()
        val time = binding.addTaskActivityTaskDeadlineTimeTv.text.toString().trim()

        val taskDeadline = HelperFunctions.parseDeadline(date, time)

        lifecycleScope.launch(Dispatchers.IO) {
            repository.insertTask(
                TaskModel(
                    title = taskTitle,
                    description = taskDescription,
                    deadline = taskDeadline,
                )
            )
        }

        finish()
    }
}