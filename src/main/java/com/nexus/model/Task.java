package com.nexus.model;

import java.time.LocalDate;

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
    private TaskStatus status;
    private User owner;
    
    public Task(String taskName, LocalDate deadline, int effort, String projectName) {
        if (!Workspace.getInstance().projectNameExists(projectName)) {
            throw new IllegalArgumentException("Nome do projeto não consta da lista de projetos.");
        }
        if (taskName == null || taskName.isBlank()) {
            throw new IllegalArgumentException("Título não pode ser vazio.");
        }
        if (effort <= 0) {
            throw new IllegalArgumentException("Esforço estimado (em horas) deve ser inteiro positivo.");
        }

        this.id = nextId++;
        this.deadline = deadline;
        this.taskName = taskName.trim();
        this.status = TaskStatus.TO_DO;        
        this.effort = effort;

        totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress(User user) {
        if (user == null || !Workspace.getInstance().userNameExists(user.getUsername())) {
            throw new IllegalArgumentException("Informe um usuário válido.");
        }
        if (this.status == TaskStatus.BLOCKED) {
            throw new IllegalArgumentException("Status da tarefa: BLOCKED.");
        }

        this.owner = user;
        this.status = TaskStatus.IN_PROGRESS;
        activeWorkload++;
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone(User user) {
        if (user == null || !Workspace.getInstance().userNameExists(user.getUsername())) {
            throw new IllegalArgumentException("Informe um usuário válido.");
        }
        if (this.status == TaskStatus.BLOCKED) {
            throw new IllegalArgumentException("Status da tarefa: BLOCKED.");
        }
        if (this.status == TaskStatus.IN_PROGRESS) {
            activeWorkload--;
        }
        this.status = TaskStatus.DONE;
        // TODO: Implementar lógica de proteção e atualizar activeWorkload (decrementar)
    }

    public void setBlocked(boolean blocked) {
        if (this.status == TaskStatus.IN_PROGRESS) {
            activeWorkload--;
        }
        if (blocked) {
            this.status = TaskStatus.BLOCKED;
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