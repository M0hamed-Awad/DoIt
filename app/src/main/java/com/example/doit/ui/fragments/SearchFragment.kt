package com.example.doit.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.doit.R
import com.example.doit.database.DatabaseProvider
import com.example.doit.databinding.FragmentSearchBinding
import com.example.doit.models.TaskModel
import com.example.doit.models.TaskStatus
import com.example.doit.repository.TaskDao
import com.example.doit.repository.TaskRepository
import com.example.doit.ui.adapters.TasksAdapter
import com.example.doit.viewmodels.TaskViewModel
import com.example.doit.viewmodels.TaskViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    private lateinit var dao: TaskDao
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TasksAdapter

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

        // Creating View Model with it's Factory
        dao = DatabaseProvider.getDatabase(requireContext()).getTaskDao()
        val factory = TaskViewModelFactory(TaskRepository(dao))

        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

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
        TODO("Not yet implemented")
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