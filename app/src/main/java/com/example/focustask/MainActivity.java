package com.example.focustask;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // Main views
    EditText etTask;
    EditText etDescription;
    Button btnAddTask;
    TextView tvTaskCount;
    LinearLayout taskContainer;

    Spinner spinnerPriority;
    Spinner spinnerFilter;

    // Dashboard views
    TextView tvTotalTasks;
    TextView tvCompletedTasks;
    TextView tvActiveTasks;
    TextView tvCompletionRate;
    TextView tvHighPriority;

    SwitchMaterial switchTheme;

    SharedPreferences sharedPreferences;

    int taskCount = 0;

    String selectedDeadline = "No deadline";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(
                "FocusTaskData",
                MODE_PRIVATE
        );

        // Load saved dark mode
        boolean darkMode =
                sharedPreferences.getBoolean(
                        "dark_mode",
                        false
                );

        // Apply saved theme
        if (darkMode) {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );

        } else {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }

        setContentView(R.layout.activity_main);

        // Main views
        etTask = findViewById(R.id.etTask);
        etDescription = findViewById(R.id.etDescription);
        btnAddTask = findViewById(R.id.btnAddTask);
        tvTaskCount = findViewById(R.id.tvTaskCount);
        taskContainer = findViewById(R.id.taskContainer);

        spinnerPriority = findViewById(R.id.spinnerPriority);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        // Dashboard views
        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvCompletedTasks = findViewById(R.id.tvCompletedTasks);
        tvActiveTasks = findViewById(R.id.tvActiveTasks);
        tvCompletionRate = findViewById(R.id.tvCompletionRate);
        tvHighPriority = findViewById(R.id.tvHighPriority);

        switchTheme = findViewById(R.id.switchTheme);

        // Set switch according to saved theme
        switchTheme.setChecked(darkMode);

        setupPrioritySpinner();
        setupFilterSpinner();

        loadTasks();

        updateTaskCount();
        updateStatistics();

        // Add task
        btnAddTask.setOnClickListener(v -> addNewTask());

        // Dark mode
        switchTheme.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    sharedPreferences.edit()
                            .putBoolean(
                                    "dark_mode",
                                    isChecked
                            )
                            .apply();

                    if (isChecked) {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES
                        );

                    } else {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO
                        );
                    }
                }
        );

        // Filter
        spinnerFilter.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        applyFilter();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                }
        );
    }

    // =========================================================
    // PRIORITY SPINNER
    // =========================================================

    private void setupPrioritySpinner() {

        String[] priorities = {
                "High",
                "Medium",
                "Low"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        priorities
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerPriority.setAdapter(adapter);
    }

    // =========================================================
    // FILTER SPINNER
    // =========================================================

    private void setupFilterSpinner() {

        String[] filters = {
                "All Tasks",
                "Active Tasks",
                "Completed Tasks",
                "High Priority",
                "Medium Priority",
                "Low Priority"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        filters
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerFilter.setAdapter(adapter);
    }

    // =========================================================
    // ADD NEW TASK
    // =========================================================

    private void addNewTask() {

        String taskText =
                etTask.getText()
                        .toString()
                        .trim();

        String description =
                etDescription.getText()
                        .toString()
                        .trim();

        if (taskText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter a task",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String priority =
                spinnerPriority
                        .getSelectedItem()
                        .toString();

        selectedDeadline = "No deadline";

        showDeadlinePickerForNewTask(
                taskText,
                description,
                priority
        );
    }

    // =========================================================
    // NEW TASK - DATE
    // =========================================================

    private void showDeadlinePickerForNewTask(
            String taskText,
            String description,
            String priority) {

        Calendar calendar =
                Calendar.getInstance();

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {

                            selectedDeadline =
                                    String.format(
                                            "%02d/%02d/%04d",
                                            dayOfMonth,
                                            month + 1,
                                            year
                                    );

                            showTimePickerForNewTask(
                                    taskText,
                                    description,
                                    priority
                            );
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

        datePickerDialog.setTitle(
                "Select Deadline"
        );

        datePickerDialog.setButton(
                DatePickerDialog.BUTTON_NEGATIVE,
                "No Deadline",
                (dialog, which) -> {

                    selectedDeadline =
                            "No deadline";

                    finishAddingTask(
                            taskText,
                            description,
                            priority,
                            selectedDeadline
                    );
                }
        );

        datePickerDialog.show();
    }

    // =========================================================
    // NEW TASK - TIME
    // =========================================================

    private void showTimePickerForNewTask(
            String taskText,
            String description,
            String priority) {

        Calendar calendar =
                Calendar.getInstance();

        TimePickerDialog timePickerDialog =
                new TimePickerDialog(
                        this,
                        (view, hourOfDay, minute) -> {

                            String time =
                                    String.format(
                                            "%02d:%02d",
                                            hourOfDay,
                                            minute
                                    );

                            selectedDeadline =
                                    selectedDeadline +
                                            " " +
                                            time;

                            finishAddingTask(
                                    taskText,
                                    description,
                                    priority,
                                    selectedDeadline
                            );
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                );

        timePickerDialog.setTitle(
                "Select Time"
        );

        timePickerDialog.setButton(
                TimePickerDialog.BUTTON_NEGATIVE,
                "Skip Time",
                (dialog, which) -> {

                    finishAddingTask(
                            taskText,
                            description,
                            priority,
                            selectedDeadline
                    );
                }
        );

        timePickerDialog.show();
    }

    // =========================================================
    // FINISH ADDING TASK
    // =========================================================

    private void finishAddingTask(
            String taskText,
            String description,
            String priority,
            String deadline) {

        taskCount++;

        addTaskToScreen(
                taskText,
                description,
                false,
                priority,
                deadline
        );

        saveCurrentTasks();

        updateTaskCount();
        updateStatistics();

        etTask.setText("");
        etDescription.setText("");

        selectedDeadline =
                "No deadline";

        applyFilter();
    }

    // =========================================================
    // ADD TASK TO SCREEN
    // =========================================================

    private void addTaskToScreen(
            String taskText,
            String description,
            boolean completed,
            String priority,
            String deadline) {

        View taskView =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.task_item,
                                taskContainer,
                                false
                        );

        CheckBox cbTask =
                taskView.findViewById(
                        R.id.cbTask
                );

        TextView tvDescription =
                taskView.findViewById(
                        R.id.tvDescription
                );

        TextView tvPriority =
                taskView.findViewById(
                        R.id.tvPriority
                );

        TextView tvDeadline =
                taskView.findViewById(
                        R.id.tvDeadline
                );

        Button btnEdit =
                taskView.findViewById(
                        R.id.btnEdit
                );

        Button btnDelete =
                taskView.findViewById(
                        R.id.btnDelete
                );

        cbTask.setText(taskText);
        cbTask.setChecked(completed);

        // Description
        if (description != null &&
                !description.trim().isEmpty()) {

            tvDescription.setText(
                    description
            );

            tvDescription.setVisibility(
                    View.VISIBLE
            );

        } else {

            tvDescription.setText("");

            tvDescription.setVisibility(
                    View.GONE
            );
        }

        // Priority
        tvPriority.setText(
                "Priority: " + priority
        );

        setPriorityColor(
                tvPriority,
                priority
        );

        // Deadline
        tvDeadline.setText(
                "Deadline: " + deadline
        );

        // Completed state
        if (completed) {

            cbTask.setAlpha(0.6f);

        } else {

            cbTask.setAlpha(1.0f);
        }

        // Checkbox
        cbTask.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isChecked) {

                        cbTask.setAlpha(0.6f);

                    } else {

                        cbTask.setAlpha(1.0f);
                    }

                    saveCurrentTasks();
                    updateStatistics();
                    applyFilter();
                }
        );

        // Edit
        btnEdit.setOnClickListener(
                v -> showEditDialog(taskView)
        );

        // Delete
        btnDelete.setOnClickListener(
                v -> {

                    new AlertDialog.Builder(this)
                            .setTitle("Delete Task")
                            .setMessage(
                                    "Are you sure you want to delete this task?"
                            )
                            .setPositiveButton(
                                    "Delete",
                                    (dialog, which) -> {

                                        taskContainer.removeView(
                                                taskView
                                        );

                                        taskCount--;

                                        saveCurrentTasks();

                                        updateTaskCount();
                                        updateStatistics();
                                    }
                            )
                            .setNegativeButton(
                                    "Cancel",
                                    null
                            )
                            .show();
                }
        );

        taskContainer.addView(taskView);
    }

    // =========================================================
    // EDIT TASK
    // =========================================================

    private void showEditDialog(View taskView) {

        CheckBox cbTask =
                taskView.findViewById(
                        R.id.cbTask
                );

        TextView tvDescription =
                taskView.findViewById(
                        R.id.tvDescription
                );

        String currentTask =
                cbTask.getText()
                        .toString();

        String currentDescription =
                tvDescription.getVisibility()
                        == View.VISIBLE
                        ? tvDescription.getText()
                        .toString()
                        : "";

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                40,
                10,
                40,
                10
        );

        EditText editTask =
                new EditText(this);

        editTask.setHint(
                "Task name"
        );

        editTask.setText(
                currentTask
        );

        EditText editDescription =
                new EditText(this);

        editDescription.setHint(
                "Task description (optional)"
        );

        editDescription.setText(
                currentDescription
        );

        editDescription.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        editDescription.setGravity(
                Gravity.TOP
        );

        layout.addView(
                editTask
        );

        layout.addView(
                editDescription
        );

        new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(layout)
                .setPositiveButton(
                        "Next",
                        (dialog, which) -> {

                            String newTask =
                                    editTask.getText()
                                            .toString()
                                            .trim();

                            String newDescription =
                                    editDescription.getText()
                                            .toString()
                                            .trim();

                            if (newTask.isEmpty()) {

                                Toast.makeText(
                                        this,
                                        "Task cannot be empty",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            cbTask.setText(
                                    newTask
                            );

                            if (!newDescription.isEmpty()) {

                                tvDescription.setText(
                                        newDescription
                                );

                                tvDescription.setVisibility(
                                        View.VISIBLE
                                );

                            } else {

                                tvDescription.setText("");

                                tvDescription.setVisibility(
                                        View.GONE
                                );
                            }

                            showEditPriorityDialog(
                                    taskView
                            );
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    // =========================================================
    // EDIT PRIORITY
    // =========================================================

    private void showEditPriorityDialog(
            View taskView) {

        TextView tvPriority =
                taskView.findViewById(
                        R.id.tvPriority
                );

        String currentPriority =
                tvPriority.getText()
                        .toString()
                        .replace(
                                "Priority: ",
                                ""
                        );

        String[] priorities = {
                "High",
                "Medium",
                "Low"
        };

        int selected = 1;

        if (currentPriority.equals("High")) {

            selected = 0;

        } else if (currentPriority.equals("Low")) {

            selected = 2;
        }

        new AlertDialog.Builder(this)
                .setTitle(
                        "Select Priority"
                )
                .setSingleChoiceItems(
                        priorities,
                        selected,
                        (dialog, which) -> {

                            String newPriority =
                                    priorities[which];

                            tvPriority.setText(
                                    "Priority: " +
                                            newPriority
                            );

                            setPriorityColor(
                                    tvPriority,
                                    newPriority
                            );

                            saveCurrentTasks();

                            updateStatistics();

                            dialog.dismiss();

                            showEditDeadlineDialog(
                                    taskView
                            );
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    // =========================================================
    // EDIT DEADLINE
    // =========================================================

    private void showEditDeadlineDialog(
            View taskView) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Deadline"
                )
                .setItems(
                        new String[]{
                                "Set Deadline",
                                "No Deadline"
                        },
                        (dialog, which) -> {

                            if (which == 0) {

                                showEditDatePicker(
                                        taskView
                                );

                            } else {

                                TextView tvDeadline =
                                        taskView.findViewById(
                                                R.id.tvDeadline
                                        );

                                tvDeadline.setText(
                                        "Deadline: No deadline"
                                );

                                saveCurrentTasks();
                            }
                        }
                )
                .show();
    }

    // =========================================================
    // EDIT DATE
    // =========================================================

    private void showEditDatePicker(
            View taskView) {

        Calendar calendar =
                Calendar.getInstance();

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {

                            String date =
                                    String.format(
                                            "%02d/%02d/%04d",
                                            dayOfMonth,
                                            month + 1,
                                            year
                                    );

                            showEditTimePicker(
                                    taskView,
                                    date
                            );
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

        datePickerDialog.show();
    }

    // =========================================================
    // EDIT TIME
    // =========================================================

    private void showEditTimePicker(
            View taskView,
            String date) {

        Calendar calendar =
                Calendar.getInstance();

        TimePickerDialog timePickerDialog =
                new TimePickerDialog(
                        this,
                        (view, hourOfDay, minute) -> {

                            String time =
                                    String.format(
                                            "%02d:%02d",
                                            hourOfDay,
                                            minute
                                    );

                            TextView tvDeadline =
                                    taskView.findViewById(
                                            R.id.tvDeadline
                                    );

                            tvDeadline.setText(
                                    "Deadline: " +
                                            date +
                                            " " +
                                            time
                            );

                            saveCurrentTasks();
                        },
                        calendar.get(
                                Calendar.HOUR_OF_DAY
                        ),
                        calendar.get(
                                Calendar.MINUTE
                        ),
                        true
                );

        timePickerDialog.show();
    }

    // =========================================================
    // PRIORITY COLOR
    // =========================================================

    private void setPriorityColor(
            TextView textView,
            String priority) {

        if (priority.equals("High")) {

            textView.setTextColor(
                    Color.RED
            );

        } else if (priority.equals("Medium")) {

            textView.setTextColor(
                    Color.rgb(
                            255,
                            152,
                            0
                    )
            );

        } else {

            textView.setTextColor(
                    Color.rgb(
                            76,
                            175,
                            80
                    )
            );
        }
    }

    // =========================================================
    // TASK COUNT
    // =========================================================

    private void updateTaskCount() {

        tvTaskCount.setText(
                "Tasks: " + taskCount
        );
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private void updateStatistics() {

        int total =
                taskContainer.getChildCount();

        int completed = 0;
        int active = 0;
        int highPriority = 0;

        for (int i = 0; i < total; i++) {

            View taskView =
                    taskContainer.getChildAt(i);

            CheckBox cbTask =
                    taskView.findViewById(
                            R.id.cbTask
                    );

            TextView tvPriority =
                    taskView.findViewById(
                            R.id.tvPriority
                    );

            if (cbTask.isChecked()) {

                completed++;

            } else {

                active++;
            }

            String priority =
                    tvPriority.getText()
                            .toString();

            if (priority.contains("High")) {

                highPriority++;
            }
        }

        int completionRate = 0;

        if (total > 0) {

            completionRate =
                    (completed * 100) /
                            total;
        }

        tvTotalTasks.setText(
                "Total: " + total
        );

        tvCompletedTasks.setText(
                "Completed: " +
                        completed
        );

        tvActiveTasks.setText(
                "Active: " +
                        active
        );

        tvCompletionRate.setText(
                "Done: " +
                        completionRate +
                        "%"
        );

        tvHighPriority.setText(
                "High Priority: " +
                        highPriority
        );
    }

    // =========================================================
    // FILTER
    // =========================================================

    private void applyFilter() {

        if (spinnerFilter == null) {
            return;
        }

        String filter =
                spinnerFilter
                        .getSelectedItem()
                        .toString();

        for (int i = 0;
             i < taskContainer.getChildCount();
             i++) {

            View taskView =
                    taskContainer.getChildAt(i);

            CheckBox cbTask =
                    taskView.findViewById(
                            R.id.cbTask
                    );

            TextView tvPriority =
                    taskView.findViewById(
                            R.id.tvPriority
                    );

            boolean show = true;

            if (filter.equals(
                    "Active Tasks")) {

                show =
                        !cbTask.isChecked();

            } else if (filter.equals(
                    "Completed Tasks")) {

                show =
                        cbTask.isChecked();

            } else if (filter.equals(
                    "High Priority")) {

                show =
                        tvPriority.getText()
                                .toString()
                                .contains("High");

            } else if (filter.equals(
                    "Medium Priority")) {

                show =
                        tvPriority.getText()
                                .toString()
                                .contains("Medium");

            } else if (filter.equals(
                    "Low Priority")) {

                show =
                        tvPriority.getText()
                                .toString()
                                .contains("Low");
            }

            taskView.setVisibility(
                    show
                            ? View.VISIBLE
                            : View.GONE
            );
        }
    }

    // =========================================================
    // SAVE TASKS
    // =========================================================

    private void saveCurrentTasks() {

        SharedPreferences.Editor editor =
                sharedPreferences.edit();

        boolean darkMode =
                sharedPreferences.getBoolean(
                        "dark_mode",
                        false
                );

        editor.clear();

        // Preserve dark mode
        editor.putBoolean(
                "dark_mode",
                darkMode
        );

        int count =
                taskContainer.getChildCount();

        editor.putInt(
                "task_count",
                count
        );

        for (int i = 0; i < count; i++) {

            View taskView =
                    taskContainer.getChildAt(i);

            CheckBox cbTask =
                    taskView.findViewById(
                            R.id.cbTask
                    );

            TextView tvDescription =
                    taskView.findViewById(
                            R.id.tvDescription
                    );

            TextView tvPriority =
                    taskView.findViewById(
                            R.id.tvPriority
                    );

            TextView tvDeadline =
                    taskView.findViewById(
                            R.id.tvDeadline
                    );

            editor.putString(
                    "task_" + i,
                    cbTask.getText()
                            .toString()
            );

            editor.putBoolean(
                    "completed_" + i,
                    cbTask.isChecked()
            );

            editor.putString(
                    "description_" + i,
                    tvDescription.getText()
                            .toString()
            );

            editor.putString(
                    "priority_" + i,
                    tvPriority.getText()
                            .toString()
                            .replace(
                                    "Priority: ",
                                    ""
                            )
            );

            editor.putString(
                    "deadline_" + i,
                    tvDeadline.getText()
                            .toString()
                            .replace(
                                    "Deadline: ",
                                    ""
                            )
            );
        }

        editor.apply();
    }

    // =========================================================
    // LOAD TASKS
    // =========================================================

    private void loadTasks() {

        taskContainer.removeAllViews();

        taskCount =
                sharedPreferences.getInt(
                        "task_count",
                        0
                );

        for (int i = 0;
             i < taskCount;
             i++) {

            String task =
                    sharedPreferences.getString(
                            "task_" + i,
                            ""
                    );

            String description =
                    sharedPreferences.getString(
                            "description_" + i,
                            ""
                    );

            boolean completed =
                    sharedPreferences.getBoolean(
                            "completed_" + i,
                            false
                    );

            String priority =
                    sharedPreferences.getString(
                            "priority_" + i,
                            "Medium"
                    );

            String deadline =
                    sharedPreferences.getString(
                            "deadline_" + i,
                            "No deadline"
                    );

            if (!task.isEmpty()) {

                addTaskToScreen(
                        task,
                        description,
                        completed,
                        priority,
                        deadline
                );
            }
        }
    }
}