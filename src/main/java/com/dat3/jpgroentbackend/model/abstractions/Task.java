package com.dat3.jpgroentbackend.model.abstractions;

import com.dat3.jpgroentbackend.model.entities.Batch;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @NotNull
    protected final LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "completed_by_name")
    protected Employee completedBy = null;

    @OneToOne
    @JoinColumn(name = "batch_id", nullable = false)
    @NotNull
    protected final Batch batch;

    protected LocalDateTime completedAt = null;

    protected Task(LocalDate dueDate, Batch batch) {
        this.dueDate = dueDate;
        this.batch = batch;
    }

    protected Task() {
        this.dueDate = null;
        this.batch = null;
    }

    public long getId() {
        return id;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Employee getCompletedBy() {
        return completedBy;
    }

    public void complete(Employee completedBy, LocalDateTime completedAt) throws IllegalStateException {
        if (this.completedBy != null || this.completedAt != null) {
            throw new IllegalStateException("'" + this + "' has already been completed");
        }
        this.completedBy = completedBy;
        this.completedAt = completedAt;
    }

    public Batch getBatch() {
        return batch;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    @Override
    public String toString() {
        return getClass().getName() + "#" + getId();
    }
}
