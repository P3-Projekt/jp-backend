package com.dat3.jpgroentbackend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
public class Task {

    public enum Category {
        Water, Harvest, Plant
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    public Category category;
    public LocalDate dueDate;

    @OneToOne
    public User completedBy;

    public LocalDateTime completedAt;

    public Task(){}
    public Task(Category category, LocalDate dueDate) {
        this.category = category;
        this.dueDate = dueDate;
    }

    public void complete(User completedBy) {
        this.completedBy = completedBy;
        this.completedAt = LocalDateTime.now();
    }

    public static List<Task> filterTasksInWeek(List<Task> tasks, int weekNumber) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int dueDateWeekNumber = task.dueDate.get(weekFields.weekOfWeekBasedYear());
            if(dueDateWeekNumber == weekNumber) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    public static Task copy(Task task) {
        if(task.completedAt != null) {
            throw new RuntimeException("It's not possible to copy a completed task");
        }
        return new Task(task.category, task.dueDate);
    }

    public boolean isTaskDueInWeek(int weekNumber) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int dueDateWeekNumber = this.dueDate.get(weekFields.weekOfWeekBasedYear());
        return dueDateWeekNumber == weekNumber;
    }

    public static List<Task> copy(List<Task> tasks){
        return tasks.stream().map(Task::copy).toList();
    }

}
