package com.nexus.model;

import java.util.List;

public class Project {
    private String projectName;
    private List<Task> taskList;
    private int totalBudget;

    public Project(String projecstName, int totalBudget) {
    projectName = projectName.trim();
    if (projectName == null || projectName.isBlank()) {
        throw new IllegalArgumentException("Nome do projeto não pode ser vazio.");
    }
    if (totalBudget <= 0) {
        throw new IllegalArgumentException("Orçamento total (em horas) deve ser inteiro positivo.");
    }
    this.projectName = projectName;
    this.totalBudget = totalBudget;
    }
}
