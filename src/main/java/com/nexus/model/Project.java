package com.nexus.model;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private String projectName;
    private List<Task> taskList;
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
        throw new IllegalArgumentException("Tarefa vazia.");
    }
    int currentEffort = taskList.stream()
                    .mapToInt(Task::getEffort)
                    .sum();
    int totalEffort = currentEffort + t.getEffort();
    if (totalEffort > totalBudget) {
        throw new IllegalArgumentException("Orçamento total (em horas) do Projeto foi excedido.");
    }
    taskList.add(t);
    }

    // Permite obter uma cópia imutável da lista de tarefas do projeto 
    public List<Task> getTaskList() {
        return List.copyOf(taskList);
    }
}
