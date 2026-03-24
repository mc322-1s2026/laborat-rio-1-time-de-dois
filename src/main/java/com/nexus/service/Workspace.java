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

    public void addProject(Project project) { projects.add(project); }
    public List<Project> getProjects() { return Collections.unmodifiableList(projects); }

    public void addTask(Task task) { tasks.add(task); }
    public List<Task> getTasks() { return Collections.unmodifiableList(tasks); }

    public void addUser(User user) { users.add(user); }
    public List<User> getUsers() { return Collections.unmodifiableList(users); }
    
    // Método que procura e retorna projeto
    public Project findProject(String name) {
        if (name == null) { throw new IllegalArgumentException("Nome do projeto não pode ser nulo."); }
        return projects.stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElseThrow(() -> new NexusValidationException("Projeto não encontrado."));
    }

    // Método que diz se nome de projeto existe
    public boolean projectNameExists(String name) {
        return projects.stream()
                .anyMatch(p -> p.getProjectName().equalsIgnoreCase(name.trim()));
    }
    
    // Método que diz se nome da tarefa existe
    public boolean taskNameExists(String name) {
        return tasks.stream()
                .anyMatch(u -> u.getTaskName().equalsIgnoreCase(name.trim()));
    }

    // Método que diz se nome de usuário existe
    public boolean userNameExists(String name) {
        return users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(name.trim()));
    }

    // Relatórios de status
    public void topPerformers() {
    System.out.println("--------------------------------------------------");
    System.out.println("      USUÁRIOS COM MAIS TAREFAS CONCLUÍDAS        ");
    System.out.println("--------------------------------------------------");

    Map<User, Long> performaceMap = tasks.stream()
            .filter(t -> t.getStatus() == TaskStatus.DONE && t.getOwner() != null)
            .collect(Collectors.groupingBy(Task::getOwner, Collectors.counting()));

    if (performaceMap.isEmpty()) {
        System.out.println("Nenhuma tarefa concluída encontrada até o momento.");
    } else {
        // 3. Se houver dados, ordenamos, limitamos e imprimimos
        performaceMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .forEach(entry -> {
                    System.out.printf("Usuário: %-15s | Concluídas: %d%n", 
                                  entry.getKey().getUsername(), 
                                  entry.getValue());
                });
    }
    System.out.println("--------------------------------------------------");
    }

    public void overloadedUsers() {
    System.out.println("--------------------------------------------------");
    System.out.println("    USUÁRIOS COM SOBRECARGA (>10 EM ANDAMENTO)    ");
    System.out.println("--------------------------------------------------");

    List<User> overloaded = users.stream()
            .filter(u -> u.getWorkload() > 10)
            .sorted(Comparator.comparingLong(User::getWorkload).reversed())
            .toList();

    if (overloaded.isEmpty()) {
        System.out.println("Não há usuários sobrecarregados no momento.");
    } else {
        overloaded.forEach(u -> 
            System.out.printf("Usuário: %-15s | Em andamento: %d%n", u.getUsername(), u.getWorkload())
        );
    }
    System.out.println("--------------------------------------------------");
    }

    public void projectHealth() {
    System.out.println("--------------------------------------------------");
    System.out.println("       PERCENTUAL DE CONCLUSÃO DOS PROJETOS       ");
    System.out.println("--------------------------------------------------");

    if (projects.isEmpty()) {
        System.out.println("Não há projetos cadastrados no sistema.");
    } else {
        projects.forEach(project -> {
            long total = project.getTotalTasks();
            double percentage = 0.0;

            if (total > 0) {
                percentage = (project.getDoneTasksCount() * 100.0) / total;
                System.out.printf("Projeto: %-20s | Progresso: [%3.0f%%] | Total: %d%n", 
                                  project.getProjectName(), 
                                  percentage, 
                                  total);
            } else {
                System.out.printf("Projeto: %-20s | [SEM TAREFAS LANÇADAS]%n", 
                                  project.getProjectName());
            }
        });
    }
    System.out.println("--------------------------------------------------");
    }

    public void mostFrequentStatus() {
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