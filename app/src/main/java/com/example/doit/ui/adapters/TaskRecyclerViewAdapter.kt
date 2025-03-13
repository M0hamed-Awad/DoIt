package com.example.doit.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doit.databinding.TaskItemBinding
import com.example.doit.models.TaskModel
import com.example.doit.utils.HelperFunctions
import java.time.format.DateTimeFormatter

class TaskRecyclerViewAdapter(
    private var tasksList: List<TaskModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClicked(task: TaskModel)
    }

    class TaskViewHolder(
        val binding: TaskItemBinding,
        private val listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(task: TaskModel) {
            binding.apply {
                val taskStatus = task.taskStatus.status

                taskTitleTv.text = task.title
                taskStatuesIcon.setImageResource(HelperFunctions.getTaskStatuesIcon(taskStatus))
                taskDescTv.text = task.description
                main.setCardBackgroundColor(
                    HelperFunctions.getTaskStatuesColor(
                        itemView.context,
                        taskStatus
                    )
                )

                val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

                taskDeadlineDateTv.text = task.deadline.format(dateFormatter)
                taskDeadlineTimeTv.text = task.deadline.format(timeFormatter)

                root.setOnClickListener {
                    listener.onItemClicked(task)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding  =
            TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding , listener)
    }

    override fun getItemCount() = tasksList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bindData(tasksList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTasks(newTasks: List<TaskModel>) {
        tasksList = newTasks
        notifyDataSetChanged()
    }
}