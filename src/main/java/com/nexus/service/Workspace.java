package com.nexus.service;

import com.nexus.exception.NexusValidationException;
import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;

public class Workspace {
    private final List<Project> projects = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    public void addProject(Project project) {
        projects.add(project);
    }
    public List<Project> getProjects() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(projects);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }
    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    public void addUser(User user) {
        users.add(user);
    }
    public List<User> getUsers() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(users);
    }
    
    // Métodos de checagem de existência de projetos, tarefas e usuários nas respectivas listas
    public boolean projectNameExists(String name) {
        return projects.stream()
                .anyMatch(p -> p.getProjectName().equalsIgnoreCase(name.trim()));
    }

    public Project findProject(String name) {
        return projects.stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElseThrow(() -> new NexusValidationException("Projeto não encontrado."));
    }
    public boolean taskNameExists(String name) {
        return tasks.stream()
                .anyMatch(u -> u.getTaskName().equalsIgnoreCase(name.trim()));
    }

    public boolean userNameExists(String name) {
        return users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(name.trim()));
    }

    // Relatórios de status
    public void topPerformers() {
        System.out.println("--------------------------------------------------");
        System.out.println("      USUÁRIOS COM MAIS TAREFAS CONCLUÍDAS        ");
        System.out.println("--------------------------------------------------");
        
        tasks.stream()
            .filter(t -> t.getStatus() == TaskStatus.DONE && t.getOwner() != null)
            .collect(Collectors.groupingBy(Task::getOwner, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(3)
            .forEach(entry -> {
                System.out.printf("Usuário: %-15s | Concluídas: %d%n", 
                              entry.getKey().getUsername(), 
                              entry.getValue());
            });
        System.out.println("--------------------------------------------------");
    }

    public void overloadedUsers() {
    System.out.println("--------------------------------------------------");
    System.out.println("    USUÁRIOS COM SOBRECARGA (>10 EM ANDAMENTO)    ");
    System.out.println("--------------------------------------------------");

    tasks.stream()
        .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS && t.getOwner() != null)
        .collect(Collectors.groupingBy(Task::getOwner, Collectors.counting()))
        .entrySet().stream()
        .filter(entry -> entry.getValue() > 10)
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .forEach(entry -> {
            System.out.printf("Usuário: %-15s | Em andamento: %d%n", 
                              entry.getKey().getUsername(), 
                              entry.getValue());
        });

    System.out.println("--------------------------------------------------");
    }

    public void projectHealth() {
    System.out.println("--------------------------------------------------");
    System.out.println("       PERCENTUAL DE CONCLUSÃO DOS PROJETOS       ");
    System.out.println("--------------------------------------------------");

    projects.forEach(project -> {
        long total = project.getTotalTasks();
        double percentage = 0.0;

        if (total > 0) {
            percentage = (project.getDoneTasksCount() * 100.0) / total;
        }

        System.out.printf("Projeto: %-20s | Progresso: [%3.0f%%] | Total: %d%n", 
                          project.getProjectName(), 
                          percentage, 
                          total);
    });

    System.out.println("--------------------------------------------------");
    }

    public void printMostFrequentStatus() {
    System.out.println("--------------------------------------------------");
    System.out.println(" STATUS COM MAIOR VOLUME DE TAREFAS (EXCETO DONE) ");
    System.out.println("--------------------------------------------------");

    tasks.stream()
        .filter(t -> t.getStatus() != TaskStatus.DONE)
        .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .ifPresentOrElse(
            entry -> System.out.printf("Status Predominante: %s | Total: %d tarefas%n", 
                                        entry.getKey(), entry.getValue()),
            () -> System.out.println("Nenhuma tarefa encontrada.")
        );

    System.out.println("--------------------------------------------------");
    }
}