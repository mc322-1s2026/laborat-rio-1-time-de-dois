package com.nexus.model;

import java.time.LocalDate;

public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;
    private static int nextId = 1;

    private int effort;
    private int id;
    private LocalDate deadline; // Imutável após o nascimento
    private String title;
    private TaskStatus status;
    private User owner;
    
    public Task(String title, LocalDate deadline, int effort, String projectName) {
        if (title == null || title.isBlank()) {
             throw new IllegalArgumentException("Título não pode ser vazio.");
        }
        if (effort <= 0) {
            throw new IllegalArgumentException("Esforço estimado (em horas) deve ser inteiro positivo.");
        }

        this.id = nextId++;
        this.deadline = deadline;
        this.title = title.trim();
        this.status = TaskStatus.TO_DO;        
        this.effort = effort;

        Project project = Project.findByName(projectName);
        if (project != null) {
            project.addTask(this);
        } else {
        throw new IllegalArgumentException("Projeto inexistente: " + projectName);
        }

        totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress(User user) {
        if (this.status == TaskStatus.BLOCKED) {
            throw new IllegalArgumentException("Status da tarefa: BLOCKED.");
        }
        if (user == null) {
            throw new IllegalArgumentException("Informe um usuário.");
        }
        this.owner = user;
        this.status = TaskStatus.IN_PROGRESS;
        activeWorkload++;
        // TODO: Implementar lógica de proteção e atualizar activeWorkload
        // Se falhar, incrementar totalValidationErrors e lançar NexusValidationException
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone(User user) {
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
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }
}