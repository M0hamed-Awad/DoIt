package com.example.doit.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.doit.R
import com.example.doit.databinding.TaskItemBinding
import com.example.doit.databinding.TaskOptionItemBinding
import com.example.doit.databinding.TaskOptionsPopupMenuBinding
import com.example.doit.models.TaskModel
import com.example.doit.utils.HelperFunctions
import java.time.format.DateTimeFormatter

class TasksAdapter(
    private var tasksList: List<TaskModel>,
    private val onDeleteTaskClickListener: OnDeleteTaskOptionClickListener,
    private val onCompleteTaskClickListener: OnCompleteTaskOptionClickListener,
    private val onEditTaskClickListener: OnEditTaskOptionClickListener,
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    fun interface OnDeleteTaskOptionClickListener {
        fun onDeleteTaskOptionClicked(task: TaskModel)
    }

    fun interface OnCompleteTaskOptionClickListener {
        fun onCompleteTaskOptionClicked(task: TaskModel)
    }

    fun interface OnEditTaskOptionClickListener {
        fun onEditTaskOptionClicked(task: TaskModel)
    }

    class TaskViewHolder(
        val binding: TaskItemBinding,
        private val onDeleteTaskClickListener: OnDeleteTaskOptionClickListener,
        private val onCompleteTaskClickListener: OnCompleteTaskOptionClickListener,
        private val onEditTaskClickListener: OnEditTaskOptionClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(task: TaskModel) {
            binding.apply {
                taskTitleTv.text = task.title
                taskDescTv.text = task.description

                val taskStatus = task.taskStatus.status
                taskStatuesIcon.setImageResource(HelperFunctions.getTaskStatuesIcon(taskStatus))

                // Changing Task Card Color based on the Task Statues
                main.setCardBackgroundColor(
                    HelperFunctions.getTaskStatuesColor(
                        itemView.context,
                        taskStatus
                    )
                )

                taskDeadlineDateTv.text =
                    task.deadline.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                taskDeadlineTimeTv.text =
                    task.deadline.format(DateTimeFormatter.ofPattern("hh:mm a"))

                moreOptionsButton.setOnClickListener {
                    // Inflating the Custom Popup Menu
                    val popupBinding =
                        TaskOptionsPopupMenuBinding.inflate(LayoutInflater.from(it.context))

                    // Displaying the Popup Menu
                    showCustomPopupMenu(it, popupBinding, task)
                }
            }
        }

        private fun showCustomPopupMenu(
            anchor: View,
            binding: TaskOptionsPopupMenuBinding,
            task: TaskModel
        ) {
            // Preparing the Popup Window
            val popupWindow = setupCustomPopupWindow(binding.root)

            // Preparing the Popup Menu UI
            setupCustomPopupMenuUi(binding)

            // Setup Click Listeners on Task Options Clicks
            setupPopupMenuOptionsClicksListeners(binding, task, popupWindow)

            // Display the Popup Menu
            popupWindow.showAsDropDown(anchor, 0, 0)
        }

        private fun setupPopupMenuOptionsClicksListeners(
            binding: TaskOptionsPopupMenuBinding,
            task: TaskModel,
            popupWindow: PopupWindow
        ) {
            binding.apply {
                completeTask.root.setOnClickListener {
                    onCompleteTaskClickListener.onCompleteTaskOptionClicked(task)
                    popupWindow.dismiss()
                }

                editTask.root.setOnClickListener {
                    onEditTaskClickListener.onEditTaskOptionClicked(task)
                    popupWindow.dismiss()
                }

                deleteTask.root.setOnClickListener {
                    onDeleteTaskClickListener.onDeleteTaskOptionClicked(task)
                    popupWindow.dismiss()
                }
            }
        }

        private fun setupCustomPopupWindow(root: View): PopupWindow {
            val popupWindow = PopupWindow(
                root,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // focusable
            )

            popupWindow.isOutsideTouchable = true
            popupWindow.elevation = 10f

            popupWindow.setBackgroundDrawable(
                ContextCompat.getDrawable(root.context, R.drawable.popup_menu_bg)
            )

            return popupWindow
        }

        @SuppressLint("SetTextI18n")
        private fun setupCustomPopupMenuUi(binding: TaskOptionsPopupMenuBinding) {
            binding.apply {
                completeTask.taskOptionText.text = "Complete Task"
                completeTask.taskOptionIcon.setImageResource(R.drawable.ic_check_40)

                deleteTask.taskOptionText.text = "Delete Task"
                deleteTask.taskOptionIcon.setImageResource(R.drawable.ic_delete_task_24)

                editTask.taskOptionText.text = "Edit Task"
                editTask.taskOptionIcon.setImageResource(R.drawable.ic_edit_task_24)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(
            binding,
            onDeleteTaskClickListener,
            onCompleteTaskClickListener,
            onEditTaskClickListener,
        )
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