package com.example.doit.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.doit.R
import com.example.doit.databinding.TaskItemBinding
import com.example.doit.models.TaskModel
import com.example.doit.utils.HelperFunctions
import java.time.format.DateTimeFormatter

class TaskRecyclerViewAdapter(
    private var tasksList: List<TaskModel>,
    private val onItemClickListener: OnItemClickListener,
    private val onDeleteTaskClickListener: OnDeleteTaskOptionClickListener,
    private val onCompleteTaskClickListener: OnCompleteTaskOptionClickListener,
    private val onEditTaskClickListener: OnEditTaskOptionClickListener,
) : RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClicked(task: TaskModel)
    }

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
        private val onItemClickListener: OnItemClickListener,
        private val onDeleteTaskClickListener: OnDeleteTaskOptionClickListener,
        private val onCompleteTaskClickListener: OnCompleteTaskOptionClickListener,
        private val onEditTaskClickListener: OnEditTaskOptionClickListener
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

                moreOptionsButton.setOnClickListener {
                    val popupMenu = PopupMenu(it.context, it)
                    popupMenu.inflate(R.menu.task_options_menu)

                    forceMenuIconsToAppear(popupMenu)

                    handleMenuOptionsClicks(popupMenu, task)

                    popupMenu.show()
                }

                root.setOnClickListener {
                    onItemClickListener.onItemClicked(task)
                }
            }
        }

        private fun forceMenuIconsToAppear(popupMenu: PopupMenu) {
            try {
                val fields = popupMenu.javaClass.declaredFields
                for (field in fields) {
                    if (field.name == "mPopup") {
                        field.isAccessible = true
                        val menuPopupHelper = field.get(popupMenu)
                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons =
                            classPopupHelper.getMethod("setForceShowIcon", Boolean::class.java)
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun handleMenuOptionsClicks(popupMenu: PopupMenu, task: TaskModel) {
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit_task -> {
                        onEditTaskClickListener.onEditTaskOptionClicked(task)
                        true
                    }

                    R.id.delete_task -> {
                        onDeleteTaskClickListener.onDeleteTaskOptionClicked(task)
                        true
                    }

                    R.id.complete_task -> {
                        onCompleteTaskClickListener.onCompleteTaskOptionClicked(task)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(
            binding,
            onItemClickListener,
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