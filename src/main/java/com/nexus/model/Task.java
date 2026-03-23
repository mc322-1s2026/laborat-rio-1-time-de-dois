package com.nexus.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.nexus.exception.NexusValidationException;
import com.nexus.service.Workspace;

public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int activeWorkload = 0;
    private static int nextId = 1;

    private int effort;
    private int id;
    private LocalDate deadline; // Imutável após o nascimento
    private String taskName;
    private String projectName;
    private TaskStatus status;
    private User owner;
    
    public Task(String taskName, LocalDate deadline, int effort, String projectName) {
        if (taskName == null || taskName.isBlank()) {
            throw new IllegalArgumentException("Título não pode ser vazio.");
        }
        if (effort <= 0) {
            throw new IllegalArgumentException("Esforço estimado (em horas) deve ser inteiro positivo.");
        }

        this.id = nextId++;
        this.deadline = deadline;
        this.taskName = taskName.trim();
        this.projectName = projectName.trim();
        this.status = TaskStatus.TO_DO;        
        this.effort = effort;

        totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress(User user) {
        if (user == null) {
            throw new NexusValidationException("Informe um usuário válido.");
        }
        if (this.status == TaskStatus.BLOCKED) {
            throw new NexusValidationException("Status da tarefa: BLOCKED.");
        }

        this.owner = user;
        this.status = TaskStatus.IN_PROGRESS;
        activeWorkload++;
    }

    public void setOwner(User user){
        this.owner = user;
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone(User user) {
        if (user == null) {
            throw new NexusValidationException("Informe um usuário válido.");
        }
        if (this.status == TaskStatus.BLOCKED) {
            throw new NexusValidationException("Status da tarefa: BLOCKED.");
        }
        if (this.status == TaskStatus.IN_PROGRESS) {
            activeWorkload--;
        }
        this.status = TaskStatus.DONE;
    }

    public void setBlocked(boolean blocked) {
        if (this.status == TaskStatus.IN_PROGRESS) {
            activeWorkload--;
        }
        if (blocked) {
            if(this.status != TaskStatus.DONE){
                this.status = TaskStatus.BLOCKED;
            }else{
                throw new NexusValidationException("Operação não permitida: trocar de DONE para BLOCKED");
            }
        } else {
            this.status = TaskStatus.TO_DO;
        }
    }

    // Getters
    public int getEffort() { return effort; }
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTaskName() { return taskName; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }
}