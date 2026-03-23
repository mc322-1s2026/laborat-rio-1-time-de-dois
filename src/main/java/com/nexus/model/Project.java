package com.nexus.model;

import java.util.ArrayList;
import java.util.List;

import com.nexus.exception.NexusValidationException;

public class Project {
    private List<Task> taskList;
    private String projectName;
    private int totalBudget;

    // Construtor que demanda nome do projeto e orçamento total
    public Project(String projectName, int totalBudget) {
    if (projectName == null || projectName.isBlank()) {
        throw new IllegalArgumentException("Nome do projeto não pode ser vazio.");
    }
    if (totalBudget <= 0) {
        throw new IllegalArgumentException("Orçamento total (em horas) deve ser inteiro positivo.");
    }
    this.projectName = projectName.trim();
    this.totalBudget = totalBudget;
    this.taskList = new ArrayList<>();
    }

    // Adiciona tarefas no projeto até o limite do orçamento de horas
    public void addTask(Task t) {
    if (t == null) {
        throw new IllegalArgumentException("Tarefa não pode ser nula.");
    }
    int currentEffort = taskList.stream()
                    .mapToInt(Task::getEffort)
                    .sum();
    int totalEffort = currentEffort + t.getEffort();
    if (totalEffort > totalBudget) {
        throw new NexusValidationException("Orçamento total (em horas) do projeto foi excedido.");
    }
    taskList.add(t);
    }

    // Getters
    public List<Task> getTaskList() { return List.copyOf(taskList); }
    public long getTotalTasks() { return this.taskList.size(); }
    public String getProjectName() { return projectName; }
    public int getTotalBudget() { return totalBudget; }
    public long getDoneTasksCount() {
        return this.taskList.stream()
               .filter(t -> t.getStatus() == TaskStatus.DONE)
               .count();
    }
}
