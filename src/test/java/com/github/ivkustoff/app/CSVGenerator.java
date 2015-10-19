package com.github.ivkustoff.app;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CSVGenerator {
    private final String testRoot = Paths.get("").toString();

    private Path currentPath;

    public Path generateEmptyFileNoParent() {
        File f = Paths.get(testRoot, "offsets.csv").toFile();
        try {
            f.createNewFile();
            currentPath = f.toPath();
            return currentPath;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Path generateEmptyFileNormalParent() {
        File parentDir = Paths.get(testRoot, "2015-10-19-12-15-13").toFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        File child = Paths.get(parentDir.getAbsolutePath(), "offsets.csv").toFile();
        try {
            if (!child.exists()) {
                child.createNewFile();
            }
            currentPath = child.toPath();
            return currentPath;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Path generateEmptyFileNormalParentWithDateString(String dateString) {
        File parentDir = Paths.get(testRoot, dateString).toFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        File child = Paths.get(parentDir.getAbsolutePath(), "offsets.csv").toFile();
        try {
            if (!child.exists()) {
                child.createNewFile();
            }
            currentPath = child.toPath();
            return currentPath;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public Path generateNormalFile(List<String> lines) {
        File parentDir = Paths.get(testRoot, "2015-10-19-12-15-13").toFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        Path child = Paths.get(parentDir.getAbsolutePath(), "offsets.csv");
        try {
            child.toFile().createNewFile();
            Files.write(child, lines);
            currentPath = child;
            return currentPath;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void generateDirectoryTree(List<String> topicNames, List<String> lines, List<String> timestamps) {
        try {
            currentPath = Paths.get(testRoot, "topicRoot");
            for (String topic : topicNames) {
                final Path topicDir = Paths.get(currentPath.toString(), topic);
                topicDir.toFile().mkdirs();
                final Path topicDirHistory = Paths.get(topicDir.toString(), "history");
                topicDirHistory.toFile().mkdirs();
                for (String timestamp : timestamps) {
                    final Path runDate = Paths.get(topicDirHistory.toString(), timestamp);
                    runDate.toFile().mkdirs();
                    final Path fileToWrite = Paths.get(runDate.toString(),"offsets.csv");
                    Files.write(fileToWrite,lines);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
