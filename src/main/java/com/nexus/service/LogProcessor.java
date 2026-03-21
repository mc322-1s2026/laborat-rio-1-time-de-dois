package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class LogProcessor {

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
                                System.out.println("[LOG] Usuário criado: " + user.getUsername());
                            }
                            case "CREATE_PROJECT" -> {
                                Project project = new Project(p[1], Integer.parseInt(p[2]));
                            }
                            case "CREATE_TASK" -> {
                                Task t = new Task(p[1], LocalDate.parse(p[2]), Integer.parseInt(p[3]));
                                workspace.addTask(t);
                                Project targetProject = workspace.findProject(p[4]);
                                targetProject.addTask(t);
                                System.out.println("[LOG] Tarefa criada: " + t.getTaskName());
                            }
                            case "ASSIGN_USER" -> {
                                Task targetTask = workspace.getTasks().stream()
                                    .filter(t -> t.getId() == Integer.parseInt(p[1]))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("Task com ID " + p[1] + " não encontrada."));
                                User targetUser = workspace.getUsers().stream()
                                    .filter(u -> u.getUsername().equalsIgnoreCase(p[2].trim()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("Usuário não cadastrado: " + p[2].trim()));
                                targetTask.moveToInProgress(targetUser);
                            }
                            case "CHANGE_STATUS" -> {
                                Task targetTask = workspace.getTasks().stream()
                                    .filter(t -> t.getId() == Integer.parseInt(p[1]))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("Task com ID " + p[1] + " não encontrada."));
                                if (p[2].trim() == "IN_PROGRESS" && (targetTask.getStatus() == TaskStatus.TO_DO)) {
                                    targetTask.moveToInProgress(targetTask.getOwner());
                                }
                                if (p[2].trim() == "DONE" && (targetTask.getStatus() == TaskStatus.TO_DO || targetTask.getStatus() == TaskStatus.IN_PROGRESS)) {
                                    targetTask.markAsDone(targetTask.getOwner());
                                }
                                if (p[2].trim() == "BLOCKED" && targetTask.getStatus() != TaskStatus.DONE) {
                                    targetTask.setBlocked(true);
                                }
                                if (p[2].trim() == "TO_DO" && targetTask.getStatus() == TaskStatus.BLOCKED) {
                                    targetTask.setBlocked(false);
                                }
                                    
                            }
                            case "REPORT_STATUS" -> {
                                
                            }
                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    } catch (NexusValidationException e) {
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}