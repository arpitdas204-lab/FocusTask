package com.example.focustask;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etTask;
    Button btnAddTask;
    TextView tvTaskCount;
    LinearLayout taskContainer;
    Spinner spinnerPriority;

    int taskCount = 0;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Connect Java variables with XML
        etTask = findViewById(R.id.etTask);
        btnAddTask = findViewById(R.id.btnAddTask);
        tvTaskCount = findViewById(R.id.tvTaskCount);
        taskContainer = findViewById(R.id.taskContainer);
        spinnerPriority = findViewById(R.id.spinnerPriority);

        // Priority options
        String[] priorities = {
                "High",
                "Medium",
                "Low"
        };

        ArrayAdapter<String> priorityAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        priorities
                );

        priorityAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerPriority.setAdapter(priorityAdapter);

        // Open permanent storage
        sharedPreferences = getSharedPreferences(
                "FocusTaskData",
                MODE_PRIVATE
        );

        // Load saved tasks
        loadTasks();

        // Add Task button
        btnAddTask.setOnClickListener(v -> {

            String taskText =
                    etTask.getText().toString().trim();

            String priority =
                    spinnerPriority.getSelectedItem().toString();

            if (!taskText.isEmpty()) {

                addTaskToScreen(
                        taskText,
                        false,
                        priority
                );

                SharedPreferences.Editor editor =
                        sharedPreferences.edit();

                editor.putString(
                        "task_" + taskCount,
                        taskText
                );

                editor.putBoolean(
                        "completed_" + taskCount,
                        false
                );

                editor.putString(
                        "priority_" + taskCount,
                        priority
                );

                editor.putInt(
                        "task_count",
                        taskCount + 1
                );

                editor.apply();

                taskCount++;

                tvTaskCount.setText(
                        "Tasks: " + taskCount
                );

                etTask.setText("");
            }
        });
    }

    // Load saved tasks
    private void loadTasks() {

        int savedTaskCount =
                sharedPreferences.getInt(
                        "task_count",
                        0
                );

        for (int i = 0; i < savedTaskCount; i++) {

            String taskText =
                    sharedPreferences.getString(
                            "task_" + i,
                            ""
                    );

            boolean isCompleted =
                    sharedPreferences.getBoolean(
                            "completed_" + i,
                            false
                    );

            String priority =
                    sharedPreferences.getString(
                            "priority_" + i,
                            "Medium"
                    );

            if (!taskText.isEmpty()) {

                addTaskToScreen(
                        taskText,
                        isCompleted,
                        priority
                );

                taskCount++;
            }
        }

        tvTaskCount.setText(
                "Tasks: " + taskCount
        );
    }

    // Create a task using task_item.xml
    private void addTaskToScreen(
            String taskText,
            boolean isCompleted,
            String priority
    ) {

        // Create a copy of task_item.xml
        LinearLayout taskLayout =
                (LinearLayout) LayoutInflater
                        .from(this)
                        .inflate(
                                R.layout.task_item,
                                taskContainer,
                                false
                        );

        // Find views inside task_item.xml
        CheckBox checkBox =
                taskLayout.findViewById(
                        R.id.cbTask
                );

        TextView priorityText =
                taskLayout.findViewById(
                        R.id.tvPriority
                );

        Button deleteButton =
                taskLayout.findViewById(
                        R.id.btnDelete
                );

        // Set task information
        checkBox.setText(taskText);

        checkBox.setChecked(
                isCompleted
        );

        priorityText.setText(
                "Priority: " + priority
        );

        // Change priority color
        if (priority.equals("High")) {

            priorityText.setTextColor(
                    0xFFE53935
            );

        } else if (priority.equals("Medium")) {

            priorityText.setTextColor(
                    0xFFFF9800
            );

        } else {

            priorityText.setTextColor(
                    0xFF43A047
            );
        }

        // Add task card to screen
        taskContainer.addView(
                taskLayout
        );

        // Save checkbox state
        checkBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    saveCurrentTasks();
                }
        );

        // Delete task
        deleteButton.setOnClickListener(
                view -> {

                    taskContainer.removeView(
                            taskLayout
                    );

                    taskCount--;

                    saveCurrentTasks();

                    tvTaskCount.setText(
                            "Tasks: " + taskCount
                    );
                }
        );
    }

    // Save tasks, completion status and priority
    private void saveCurrentTasks() {

        SharedPreferences.Editor editor =
                sharedPreferences.edit();

        editor.clear();

        for (int i = 0;
             i < taskContainer.getChildCount();
             i++) {

            LinearLayout taskLayout =
                    (LinearLayout)
                            taskContainer.getChildAt(i);

            CheckBox checkBox =
                    taskLayout.findViewById(
                            R.id.cbTask
                    );

            TextView priorityText =
                    taskLayout.findViewById(
                            R.id.tvPriority
                    );

            String taskText =
                    checkBox.getText().toString();

            boolean isCompleted =
                    checkBox.isChecked();

            String priority =
                    priorityText.getText()
                            .toString()
                            .replace(
                                    "Priority: ",
                                    ""
                            );

            editor.putString(
                    "task_" + i,
                    taskText
            );

            editor.putBoolean(
                    "completed_" + i,
                    isCompleted
            );

            editor.putString(
                    "priority_" + i,
                    priority
            );
        }

        editor.putInt(
                "task_count",
                taskContainer.getChildCount()
        );

        editor.apply();
    }
}