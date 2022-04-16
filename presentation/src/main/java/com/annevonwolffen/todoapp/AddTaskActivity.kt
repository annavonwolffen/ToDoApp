package com.annevonwolffen.todoapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.core.widget.doOnTextChanged
import com.annevonwolffen.domain.Priority
import com.annevonwolffen.todoapp.TasksActivity.Companion.ADD_TASK_KEY
import com.annevonwolffen.todoapp.TasksActivity.Companion.DELETE_TASK_KEY
import com.annevonwolffen.todoapp.model.TaskPresentationModel
import com.annevonwolffen.todoapp.utils.toCalendar
import com.annevonwolffen.todoapp.utils.toDate
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskDescriptionField: EditText
    private lateinit var deadlineDateLabel: TextView

    private var selectedPriority: Priority = Priority.UNDEFINED
    private var deadlineEnabled: Boolean = false

    private var currentTask: TaskPresentationModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_task_activity)
        setLightStatusBar()
        setUpToolbar()

        currentTask = intent.getParcelableExtra(TASK_EXTRA)
        selectedPriority = currentTask?.priority ?: Priority.UNDEFINED
        deadlineEnabled = currentTask?.deadline != null
        initViews()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(PRIORITY_STATE, selectedPriority.value)
        outState.putBoolean(DEADLINE_TOGGLE_STATE, deadlineEnabled)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedPriority =
            Priority.values().find { it.value == savedInstanceState.getInt(PRIORITY_STATE) }
                ?: Priority.UNDEFINED
        deadlineEnabled = savedInstanceState.getBoolean(DEADLINE_TOGGLE_STATE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_task_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        setSaveButtonColor(menu)
        return true
    }

    private fun setSaveButtonColor(menu: Menu) {
        val saveButton = menu.findItem(R.id.action_save)
        val spannableTitle = SpannableString(getString(R.string.save))
        val color =
            if (taskDescriptionField.text.isEmpty()) R.color.colorDisabled else R.color.colorBlue
        spannableTitle.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, color)),
            0,
            spannableTitle.length,
            0
        )
        saveButton.title = spannableTitle
        saveButton.isEnabled = taskDescriptionField.text.isNotEmpty()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else if (item.itemId == R.id.action_save) {
            setResult(
                Activity.RESULT_OK,
                Intent().putExtra(ADD_TASK_KEY, createTask())
            )
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initViews() {
        initTaskDescription()
        initPopupMenu()
        initDeadlineLabel()
        initSwitchToggle()
        initDeleteButton()
    }

    private fun initTaskDescription() {
        taskDescriptionField = findViewById(R.id.task_description_field)
        taskDescriptionField.setText(currentTask?.title.orEmpty())
        taskDescriptionField.doOnTextChanged { _, _, _, _ -> invalidateOptionsMenu() }
    }

    private fun initPopupMenu() {
        val importanceMenuTextView = findViewById<TextView>(R.id.importance_popup_menu_title)
        importanceMenuTextView.text = selectedPriority.label
        val importancePopupMenu = PopupMenu(this, importanceMenuTextView)
        importancePopupMenu.menuInflater.inflate(
            R.menu.importance_popup_menu,
            importancePopupMenu.menu
        )
        val highImportanceMenuItem: MenuItem = importancePopupMenu.menu.findItem(R.id.high)
        val spannableTitle = SpannableString(highImportanceMenuItem.title)
        spannableTitle.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorRed)),
            0,
            spannableTitle.length,
            0
        )
        highImportanceMenuItem.title = spannableTitle
        importancePopupMenu.setOnMenuItemClickListener { menuItem ->
            selectedPriority = when (menuItem.itemId) {
                R.id.none -> Priority.UNDEFINED
                R.id.low -> Priority.LOW
                R.id.high -> Priority.HIGH
                else -> Priority.UNDEFINED
            }
            importanceMenuTextView.text = selectedPriority.label
            true
        }
        findViewById<TextView>(R.id.importance_label).setOnClickListener { importancePopupMenu.show() }
    }

    private fun initDeadlineLabel() {
        val initialCalendar = currentTask?.deadline?.toCalendar() ?: Calendar.getInstance()
        val initialDay = initialCalendar.get(Calendar.DAY_OF_MONTH)
        val initialMonth = initialCalendar.get(Calendar.MONTH)
        val initialYear = initialCalendar.get(Calendar.YEAR)
        deadlineDateLabel = findViewById(R.id.deadline_date)
        if (deadlineDateLabel.text.isEmpty()) {
            deadlineDateLabel.text = getString(
                R.string.deadline_date_format,
                initialDay,
                getMonthNameByIndex(initialMonth),
                initialYear
            )
        }
        deadlineDateLabel.visibility = if (deadlineEnabled) View.VISIBLE else View.INVISIBLE
        deadlineDateLabel.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this, R.style.Theme_TodoApp_DatePickerDialog,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    deadlineDateLabel.text = getString(
                        R.string.deadline_date_format,
                        dayOfMonth,
                        getMonthNameByIndex(month),
                        year
                    )
                }, initialYear, initialMonth, initialDay
            )
            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }
    }

    private fun getMonthNameByIndex(index: Int) =
        resources.getStringArray(R.array.months_genitive)[index]

    private fun getMonthIndexByName(month: String) =
        resources.getStringArray(R.array.months_genitive).indexOf(month)

    private fun initSwitchToggle() {
        findViewById<SwitchCompat>(R.id.deadline_switch).run {
            isChecked = deadlineEnabled
            setOnCheckedChangeListener { _, isChecked ->
                deadlineEnabled = isChecked
                deadlineDateLabel.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    private fun initDeleteButton() {
        findViewById<TextView>(R.id.delete_view).run {
            isClickable = currentTask != null
            isFocusable = currentTask != null
            TextViewCompat.setTextAppearance(
                this,
                if (currentTask != null) R.style.TextAppearance_ToDoApp_Body_Accent else R.style.TextAppearance_ToDoApp_Body_Disabled
            )
            currentTask?.let { task ->
                setOnClickListener {
                    setResult(
                        Activity.RESULT_OK,
                        Intent().putExtra(DELETE_TASK_KEY, task)
                    )
                    finish()
                }
            }
        }
    }

    private fun createTask(): TaskPresentationModel {
        val description: String = taskDescriptionField.text.toString()
        val deadline: Date? =
            if (deadlineEnabled) getDateFromString(deadlineDateLabel.text.toString()) else null
        return currentTask?.copy(
            title = description,
            deadline = deadline,
            priority = selectedPriority
        )
            ?: TaskPresentationModel(
                title = description,
                deadline = deadline,
                isDone = false,
                priority = selectedPriority
            )
    }

    private fun getDateFromString(dateString: String): Date {
        val splittedDate = dateString.split(" ")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, splittedDate[0].toInt())
        calendar.set(Calendar.MONTH, getMonthIndexByName(splittedDate[1]))
        calendar.set(Calendar.YEAR, splittedDate[2].toInt())
        return calendar.toDate()
    }

    companion object {
        @JvmStatic
        fun newIntent(context: Context): Intent = Intent(context, AddTaskActivity::class.java)

        @JvmStatic
        fun newIntent(context: Context, task: TaskPresentationModel): Intent {
            val intent = Intent(context, AddTaskActivity::class.java)
            intent.putExtra(TASK_EXTRA, task)
            return intent
        }

        private const val PRIORITY_STATE = "PRIORITY_STATE"
        private const val DEADLINE_TOGGLE_STATE = "DEADLINE_TOGGLE_STATE"
        private const val TASK_EXTRA = "TASK_EXTRA"
    }
}