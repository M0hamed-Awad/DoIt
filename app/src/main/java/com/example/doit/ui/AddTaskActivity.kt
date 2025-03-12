package com.example.doit.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.doit.R
import com.example.doit.databinding.ActivityAddTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeDatePicker()

        initializeTimePicker()

        binding.selectDateLinearLayout.setOnClickListener {
            showDatePickerDialog(datePicker, calendarDate)
        }

        binding.selectTimeLinearLayout.setOnClickListener {
            showTimePickerDialog(timePicker ,calendarTime)
        }
    }

    private fun initializeDatePicker(){

        datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendarDate.set(Calendar.YEAR, year)
            calendarDate.set(Calendar.MONTH, month)
            calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateLabel(calendarDate)
        }

    }

    private fun initializeTimePicker(){

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

    @SuppressLint("SimpleDateFormat")
    private fun updateTimeLabel(calendarTime: Calendar) {
        val format = SimpleDateFormat("hh:mm aa")
        val time = format.format(calendarTime.time)
        binding.taskDeadlineTime.text = time
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun updateDateLabel(calendarDate: Calendar) {
        val day = SimpleDateFormat("DD").format(calendarDate.time)
        val month = SimpleDateFormat("MM").format(calendarDate.time)
        val year = SimpleDateFormat("YYYY").format(calendarDate.time)

        binding.taskDeadlineDate.text = "${month}/${day}/${year}"
    }
}