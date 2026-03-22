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
}