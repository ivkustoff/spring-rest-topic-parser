package com.github.ivkustoff.app.model.parsing;

import java.nio.file.Path;

public class ParsingErrorMessage {
    private final String message;

    private Path path;
    private String line;

    ParsingErrorMessage(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        if (line != null) {
            sb.append(" line ").append(line);
        }
        if (path != null) {
            sb.append(" at file ").append(path);
        }
        return sb.toString();
    }

    public Path getPath() {
        return path;
    }

    public String getLine() {
        return line;
    }


    public ParsingErrorMessage setPath(Path path) {
        this.path = path;
        return this;
    }

    public ParsingErrorMessage setLine(String line) {
        this.line = line;
        return this;
    }
}
