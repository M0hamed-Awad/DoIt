package com.example.doit.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.doit.database.DatabaseProvider
import com.example.doit.databinding.FragmentOverdueTasksBinding
import com.example.doit.models.TaskModel
import com.example.doit.repository.TaskRepository
import com.example.doit.ui.adapters.TaskRecyclerViewAdapter
import com.example.doit.viewmodels.TaskViewModel
import com.example.doit.viewmodels.TaskViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OverdueTasksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverdueTasksFragment : Fragment(), TaskRecyclerViewAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentOverdueTasksBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskRecyclerViewAdapter

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
        binding = FragmentOverdueTasksBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = DatabaseProvider.getDatabase(requireContext()).getTaskDao()
        val factory = TaskViewModelFactory(TaskRepository(dao))

        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

        adapter = TaskRecyclerViewAdapter(emptyList(), this)

        binding.overdueTasksFragmentRecyclerView.adapter = adapter

        observeTasks()
    }


    private fun observeTasks() {
        taskViewModel.overdueTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.updateTasks(tasks)
        }
    }

    override fun onItemClicked(task: TaskModel) {
        TODO("Not yet implemented")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OverdueTasksFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OverdueTasksFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}