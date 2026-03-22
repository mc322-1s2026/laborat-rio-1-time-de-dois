package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class LogProcessor {
    private int totalValidationErrors = 0;

    public void processLog(String fileName, Workspace workspace, List<User> users) {
        try {
            // Busca o arquivo dentro da pasta de recursos do projeto (target/classes)
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);
            
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));
                
                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    String[] p = line.split(";");
                    String action = p[0];

                    try {
                        switch (action) {
                            case "CREATE_USER" -> {
                                User user = new User(p[1], p[2]);
                                users.add(user);
                                workspace.addUser(user);
                                System.out.println("[LOG] Usuário criado: " + user.getUsername());
                            }
                            case "CREATE_PROJECT" -> {
                                Project project = new Project(p[1], Integer.parseInt(p[2]));
                                workspace.addProject(project);
                                System.out.println("[LOG] Projeto criado: " + project.getProjectName());
                            }
                            case "CREATE_TASK" -> {
                                Project targetProject = workspace.findProject(p[4]);
                                Task targetTask = new Task(p[1], LocalDate.parse(p[2]),Integer.parseInt(p[3]),targetProject.getProjectName());
                                targetProject.addTask(targetTask);
                                workspace.addTask(targetTask);
                                System.out.println("[LOG] Tarefa criada: " + targetTask.getTaskName());
                            }
                            case "ASSIGN_USER" -> {
                                Task targetTask = workspace.getTasks().stream()
                                    .filter(t -> t.getId() == Integer.parseInt(p[1]))
                                    .findFirst()
                                    .orElseThrow(() -> new NexusValidationException("Task com ID " + p[1] + " não encontrada."));
                                User targetUser = workspace.getUsers().stream()
                                    .filter(u -> u.getUsername().equalsIgnoreCase(p[2].trim()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("Usuário não cadastrado: " + p[2].trim()));
                                targetTask.setOwner(targetUser);
                            }
                            case "CHANGE_STATUS" -> {
                                Task targetTask = workspace.getTasks().stream()
                                    .filter(t -> t.getId() == Integer.parseInt(p[1]))
                                    .findFirst()
                                    .orElseThrow(() -> new NexusValidationException("Task com ID " + p[1] + " não encontrada."));
                               
                                if (p[2].trim().equals("IN_PROGRESS") ) {
                                    targetTask.moveToInProgress(targetTask.getOwner());
                                }
                                else if (p[2].trim().equals("DONE")) {
                                    targetTask.markAsDone(targetTask.getOwner());
                                }
                                else if (p[2].trim().equals("BLOCKED")) {
                                    targetTask.setBlocked(true);
                                }
                                else if (p[2].trim().equals("TO_DO")) {
                                    targetTask.setBlocked(false);
                                }
                                    
                            }
                            case "REPORT_STATUS" -> {
                                workspace.topPerformers();
                                workspace.overloadedUsers();
                                workspace.projectHealth();
                                workspace.mostFrequentStatus();
                            }
                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    } catch (NexusValidationException e) {
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                        totalValidationErrors++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}