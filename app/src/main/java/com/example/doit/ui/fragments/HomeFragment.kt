package com.example.doit.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.doit.R
import com.example.doit.database.DatabaseProvider
import com.example.doit.databinding.EditTaskBottomSheetBinding
import com.example.doit.databinding.FragmentHomeBinding
import com.example.doit.models.TaskDao
import com.example.doit.models.TaskModel
import com.example.doit.models.TaskStatus
import com.example.doit.repository.TaskRepository
import com.example.doit.ui.adapters.HomePagerAdapter
import com.example.doit.ui.adapters.TaskRecyclerViewAdapter
import com.example.doit.utils.Constants
import com.example.doit.utils.HelperFunctions
import com.example.doit.viewmodels.TaskViewModel
import com.example.doit.viewmodels.TaskViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class HomeFragment : Fragment(),
    TaskRecyclerViewAdapter.OnItemClickListener,
    TaskRecyclerViewAdapter.OnDeleteTaskOptionClickListener,
    TaskRecyclerViewAdapter.OnCompleteTaskOptionClickListener,
    TaskRecyclerViewAdapter.OnEditTaskOptionClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var bottomSheetBinding: EditTaskBottomSheetBinding

    private lateinit var dao: TaskDao
    protected lateinit var taskViewModel: TaskViewModel
    protected lateinit var adapter: TaskRecyclerViewAdapter

    private lateinit var dialog: BottomSheetDialog
    private lateinit var datePicker: DatePickerDialog.OnDateSetListener
    private lateinit var timePicker: TimePickerDialog.OnTimeSetListener
    private val calendarDate = Calendar.getInstance()
    private val calendarTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bottomSheetBinding = EditTaskBottomSheetBinding.inflate(layoutInflater)
        binding = FragmentHomeBinding.inflate(inflater)

        // Creating View Model with it's Factory
        dao = DatabaseProvider.getDatabase(requireContext()).getTaskDao()
        val factory = TaskViewModelFactory(TaskRepository(dao))

        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

        // Initializing Date & Time Pickers to be used in their Dialogs
        initializeDatePicker()
        initializeTimePicker()

        // Creating Page Tabs
        setUpTabLayoutWithViewPager()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Preparing the Recycler View Adapter
        adapter = TaskRecyclerViewAdapter(
            emptyList(),
            this,
            this,
            this,
            this,
        )
    }

    override fun onResume() {
        super.onResume()
        // Checking for Overdue Tasks
        taskViewModel.updateOverdueTasks()
    }

    override fun onItemClicked(task: TaskModel) {}

    override fun onDeleteTaskOptionClicked(task: TaskModel) {
        lifecycleScope.launch(Dispatchers.IO) {
            taskViewModel.deleteTask(task)
        }
    }

    override fun onCompleteTaskOptionClicked(task: TaskModel) {

        // Changing Task Status
        task.taskStatus = changeTaskStatus(task.taskStatus)

        lifecycleScope.launch(Dispatchers.IO) {
            taskViewModel.insertTask(task)
        }
    }

    private fun changeTaskStatus(taskStatus: TaskStatus): TaskStatus {
        return if (taskStatus == TaskStatus.COMPLETED) {
            // If the Task already Completed them make it In Progress
            TaskStatus.IN_PROGRESS
        } else {
            // Else make it Completed
            TaskStatus.COMPLETED
        }
    }

    override fun onEditTaskOptionClicked(task: TaskModel) {
        // Inflating the Bottom Sheet
        bottomSheetBinding = EditTaskBottomSheetBinding.inflate(layoutInflater)

        // Fill the Views by the Task Values
        populateTaskDataToViews(task)

        // Preparing Click Listeners for Button and Date & Time Pickers
        setupClickListeners(task)

        // Displaying the Dialog
        showDialog(bottomSheetBinding.root)
    }

    private fun setupClickListeners(task: TaskModel) {
        // On Confirm Button Pressed
        bottomSheetBinding.editTaskBottomSheetConfirmBtn.setOnClickListener {
            if (HelperFunctions.validateEditTaskForm(bottomSheetBinding)) {
                insertTask(task.id)
                dialog.dismiss()
            }
        }

        // On Cancel Button Pressed
        bottomSheetBinding.editTaskBottomSheetCancelBtn.setOnClickListener { dialog.dismiss() }

        // On Clicking on the Date Picker
        bottomSheetBinding.editTaskBottomSheetSelectDateLinearLayout.setOnClickListener {
            showDatePickerDialog(datePicker, calendarDate)
        }

        // On Clicking on the Time Picker
        bottomSheetBinding.editTaskBottomSheetSelectTimeLinearLayout.setOnClickListener {
            showTimePickerDialog(timePicker, calendarTime)
        }
    }

    private fun setUpTabLayoutWithViewPager() {
        binding.viewPager.adapter = HomePagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.customView = createCustomTab(position)
        }.attach()
    }

    @SuppressLint("InflateParams")
    private fun createCustomTab(position: Int): View {
        val customTabView =
            LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)

        // Getting the Tab Icon & Title Views
        val tabIcon = customTabView.findViewById<ImageView>(R.id.tab_icon)
        val tabTitle = customTabView.findViewById<TextView>(R.id.tab_title)

        // Getting the Task Status and Putting it as the Tab Title
        val taskStatus = Constants.tabs.keys.elementAt(position)
        tabTitle.text = taskStatus

        // Setting Tabs Icons
        Constants.tabs[taskStatus]?.let { tabIcon.setImageResource(it) }

        return customTabView
    }

    private fun showDialog(dialogView: View) {
        // Inflating the Bottom Sheet
        dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)

        // Display the Bottom Sheet
        dialog.show()
    }

    private fun initializeDatePicker() {

        // Setting the Date Picker with a Certain Format ( Month / Day / Year ) -> ( 3/28/2025 )
        datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendarDate.set(Calendar.YEAR, year)
            calendarDate.set(Calendar.MONTH, month)
            calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Updating the Date Text View Text
            updateDateLabel(bottomSheetBinding.editTaskBottomSheetTaskDeadlineDateTv)
        }

    }

    private fun initializeTimePicker() {

        // Setting the Date Picker with a Certain Format ( Hour:Minutes AM/PM ) -> ( 10:00 AM )
        timePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendarTime.set(Calendar.MINUTE, minute)

            calendarTime.timeZone = TimeZone.getDefault()

            // Updating the Time Text View Text
            updateTimeLabel(bottomSheetBinding.editTaskBottomSheetTaskDeadlineTimeTv)
        }

    }

    private fun showDatePickerDialog(
        datePickerDialog: DatePickerDialog.OnDateSetListener,
        calendarDate: Calendar
    ) {
        DatePickerDialog(
            requireContext(),
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
            requireContext(),
            timePickerDialog,
            calendarTime.get(Calendar.HOUR_OF_DAY),
            calendarTime.get(Calendar.MINUTE),
            false,
        ).show()
    }

    private fun updateTimeLabel(timeTv: TextView) {
        // Preparing the Time Format
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val time = format.format(calendarTime.time)

        timeTv.text = time
    }

    private fun updateDateLabel(dateTv: TextView) {
        // Preparing the Date Format
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val date = format.format(calendarDate.time)

        dateTv.text = date
    }

    private fun insertTask(taskId: Int) {
        // Getting Task Values from their Views
        val (taskTitle, taskDescription, taskDeadline) = extractTaskInfoFromTextViews()

        lifecycleScope.launch(Dispatchers.IO) {
            taskViewModel.insertTask(
                TaskModel(
                    id = taskId,
                    title = taskTitle,
                    description = taskDescription,
                    deadline = taskDeadline,
                )
            )
        }
    }

    private fun extractTaskInfoFromTextViews(): Triple<String, String, LocalDateTime> {
        // Setting up the Task Deadline components (date - time)
        val date = bottomSheetBinding.editTaskBottomSheetTaskDeadlineDateTv.text.toString().trim()
        val time = bottomSheetBinding.editTaskBottomSheetTaskDeadlineTimeTv.text.toString().trim()

        // Getting Task Values from their Views
        val taskTitle = bottomSheetBinding.editTaskBottomSheetTaskTitleEt.text.toString()
        val taskDescription = bottomSheetBinding.editTaskBottomSheetTaskDescEt.text.toString()

        val taskDeadline = HelperFunctions.parseDeadline(date, time)

        return Triple(taskTitle, taskDescription, taskDeadline)
    }

    private fun populateTaskDataToViews(task: TaskModel) {
        // Getting the Task Deadline components (date - time)
        val taskDeadlineTime = task.deadline.format(DateTimeFormatter.ofPattern("hh:mm a"))
        val taskDeadlineDate = task.deadline.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

        // Putting the Task Values to their Views
        bottomSheetBinding.editTaskBottomSheetTaskTitleEt.setText(task.title)
        bottomSheetBinding.editTaskBottomSheetTaskDescEt.setText(task.description)
        bottomSheetBinding.editTaskBottomSheetTaskDeadlineDateTv.text = taskDeadlineDate
        bottomSheetBinding.editTaskBottomSheetTaskDeadlineTimeTv.text = taskDeadlineTime
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}