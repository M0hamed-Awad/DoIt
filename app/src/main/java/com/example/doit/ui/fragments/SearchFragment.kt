package com.example.doit.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.doit.R

import com.example.doit.database.DatabaseProvider
import com.example.doit.databinding.EditTaskBottomSheetBinding
import com.example.doit.databinding.FragmentSearchBinding
import com.example.doit.models.TaskModel
import com.example.doit.repository.TaskDao
import com.example.doit.repository.TaskRepository
import com.example.doit.ui.adapters.TasksAdapter
import com.example.doit.utils.HelperFunctions
import com.example.doit.viewmodels.TaskViewModel
import com.example.doit.viewmodels.TaskViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(),
    TasksAdapter.OnDeleteTaskOptionClickListener,
    TasksAdapter.OnCompleteTaskOptionClickListener,
    TasksAdapter.OnEditTaskOptionClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentSearchBinding
    private lateinit var bottomSheetBinding: EditTaskBottomSheetBinding

    private lateinit var dao: TaskDao
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TasksAdapter

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
        binding = FragmentSearchBinding.inflate(layoutInflater)
        bottomSheetBinding = EditTaskBottomSheetBinding.inflate(layoutInflater)

        // Creating View Model with it's Factory
        dao = DatabaseProvider.getDatabase(requireContext()).getTaskDao()
        val factory = TaskViewModelFactory(TaskRepository(dao))

        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

        // Initializing Date & Time Pickers to be used in their Dialogs
        datePicker = HelperFunctions.initializeDatePicker(calendarDate, bottomSheetBinding)
        timePicker = HelperFunctions.initializeTimePicker(calendarTime, bottomSheetBinding)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Preparing the Recycler View Adapter
        adapter = TasksAdapter(
            emptyList(),
            this,
            this,
            this,
        )


        binding.searchTasksFragmentRecyclerView.adapter = adapter

        binding.searchInputEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                triggerSearchOnTextChanged(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    override fun onDeleteTaskOptionClicked(task: TaskModel) {
        lifecycleScope.launch(Dispatchers.IO) {
            taskViewModel.deleteTask(task)
        }
    }

    override fun onCompleteTaskOptionClicked(task: TaskModel) {
        // Changing Task Status
        task.taskStatus = HelperFunctions.changeTaskStatus(task.taskStatus)

        lifecycleScope.launch(Dispatchers.IO) {
            taskViewModel.insertTask(task)
        }
    }

    override fun onEditTaskOptionClicked(task: TaskModel) {
        // Inflating the Bottom Sheet
        bottomSheetBinding = EditTaskBottomSheetBinding.inflate(layoutInflater)

        // Fill the Views by the Task Values
        HelperFunctions.populateTaskDataToViews(task, bottomSheetBinding)

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

    private fun insertTask(taskId: Int) {
        // Getting Task Values from their Views
        val (taskTitle, taskDescription, taskDeadline) = HelperFunctions.extractTaskInfoFromTextViews(bottomSheetBinding)

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

    private fun showDialog(dialogView: View) {
        // Inflating the Bottom Sheet
        dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        dialog.setContentView(dialogView)

        // Display the Bottom Sheet
        dialog.show()
    }

    private fun observeTasks(text: String) {
        taskViewModel.searchTasks(text).observe(viewLifecycleOwner) { tasks ->
            adapter.updateTasks(tasks)
        }
    }

    private fun triggerSearchOnTextChanged(text: CharSequence?){
        val query = text.toString().trim()
        if (query.isNotEmpty()) { observeTasks(query) }
        else { adapter.updateTasks(emptyList()) }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}