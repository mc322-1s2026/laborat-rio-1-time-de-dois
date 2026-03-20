package com.nexus.service;

import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Workspace {
    private final List<Project> projects = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    private static Workspace instance;
    public static Workspace getInstance() {
    if (instance == null) instance = new Workspace();
    return instance;
    }
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

    public boolean taskNameExists(String name) {
        return tasks.stream()
                .anyMatch(u -> u.getTaskName().equalsIgnoreCase(name.trim()));
    }

    public boolean userNameExists(String name) {
        return users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(name.trim()));
    }
}