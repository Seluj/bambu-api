package com.selutech.bambuapi.controllers;

import com.selutech.bambuapi.data.ConfigType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
public class Config {
    @GetMapping("/config/simple/{configType}")
    public ResponseEntity<List<String>> getAllSimpleConfigsAvailable(@PathVariable ConfigType configType) {
        try {
            org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource("static/simple_config");
            File directory = resource.getFile();

            if (directory.exists() && directory.isDirectory()) {
                switch (configType) {
                    case FILAMENT -> {
                        directory = new File(directory, ConfigType.FILAMENT.getFolderName());
                    }
                    case MACHINE -> {
                        directory = new File(directory, ConfigType.MACHINE.getFolderName());
                    }
                    case PROCESS -> {
                        directory = new File(directory, ConfigType.PROCESS.getFolderName());
                    }
                }
                String[] fileNames = directory.list();
                if (fileNames != null) {
                    return ResponseEntity.ok(Arrays.asList(fileNames));
                }
            }
            return ResponseEntity.ok(List.of());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * List all files name in the resource/static/all_configs directory.
     *
     * @return
     */
    @GetMapping("/config/all/{configType}")
    public ResponseEntity<List<String>> getAllConfigsAvailable(@PathVariable ConfigType configType) {
        try {
            org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource("static/all_config");
            File directory = resource.getFile();

            if (directory.exists() && directory.isDirectory()) {
                switch (configType) {
                    case FILAMENT -> {
                        directory = new File(directory, ConfigType.FILAMENT.getFolderName());
                    }
                    case MACHINE -> {
                        directory = new File(directory, ConfigType.MACHINE.getFolderName());
                    }
                    case PROCESS -> {
                        directory = new File(directory, ConfigType.PROCESS.getFolderName());
                    }
                }
                String[] fileNames = directory.list();
                if (fileNames != null) {
                    // Remove all files starting with non-printable characters
                    fileNames = Arrays.stream(fileNames)
                            .filter(name -> !name.startsWith(".") && !name.startsWith("_"))
                            .toArray(String[]::new);
                    // Remove all files that contains a field "instantiation" with value "false"
                    File finalDirectory = directory;
                    fileNames = Arrays.stream(fileNames)
                            .filter(name -> {
                                try {
                                    File file = new File(finalDirectory, name);
                                    if (file.exists()) {
                                        // Assuming JSON format - you may need to adapt this based on your file format
                                        org.springframework.core.io.Resource fileResource = new org.springframework.core.io.FileSystemResource(file);
                                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                                        com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(fileResource.getInputStream());

                                        // Check if instantiation field exists and if it's set to "true"
                                        if (root.has("instantiation") && "false".equals(root.get("instantiation").asText())) {
                                            return false;
                                        }
                                        return true;
                                    }
                                    return true; // If file doesn't exist (shouldn't happen), we include it
                                } catch (Exception e) {
                                    return true; // If there's an error, we assume the file is valid
                                }
                            })
                            .toArray(String[]::new);
                    // Remove all files that contains H2D or P1P or A1 in the name
                    fileNames = Arrays.stream(fileNames)
                            .filter(name -> !name.contains("H2D") && !name.contains("P1P") && !name.contains("A1"))
                            .toArray(String[]::new);
                    // Remove all files that contains "0.2" in the name
                    fileNames = Arrays.stream(fileNames)
                            .filter(name -> !name.contains("0.2 nozzle"))
                            .toArray(String[]::new);

                    // Remove the ".json" extension from the file names
                    fileNames = Arrays.stream(fileNames)
                            .map(name -> name.replace(".json", ""))
                            .toArray(String[]::new);
                    return ResponseEntity.ok(Arrays.asList(fileNames));
                }
            }
            return ResponseEntity.ok(List.of());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
