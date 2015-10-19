package com.github.ivkustoff.app.model.parsing;

import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CSVFileParser {
    private final String LINE_REGEXP = "\\d{1,},\\d{1,}";

    private final Path pathToParse;
    private final List<ParsingErrorMessage> parsingErrorMessageList = new ArrayList<>();
    private ParsedTopicData parsedTopicData;
    private String currentLine = "";

    public CSVFileParser(final Path pathToParse, final String topicName) {
        this.pathToParse = pathToParse;
        this.parsedTopicData = new ParsedTopicData(convertPathToTimestamp(pathToParse), topicName);
    }

    public CSVFileParser parseFile() {
        if (!pathToParse.toFile().exists()) {
            parsingErrorMessageList.add(new ParsingErrorMessage("File not exists").setPath(pathToParse));
        } else {
            List<String> lines = readFileToMemory();
            List<String> filteredByRegexp = lines.stream().filter(s -> s.matches(LINE_REGEXP)).collect(Collectors.toList());

            for (String filteredLine : filteredByRegexp) {
                this.currentLine = filteredLine;
                addLine(parsedTopicData);
            }
        }
        return this;
    }

    private void addLine(ParsedTopicData parsedTopicData) {
        List<String> splitted = Arrays.asList(currentLine.split(","));
        if (splitted.size() != 2) {
            parsingErrorMessageList.add(new ParsingErrorMessage("problem while parsing line").setLine(currentLine).setPath(pathToParse));
        }
        Integer partitionNumber = parseLong(splitted.get(0)).intValue();
        Long numMessages = parseLong(splitted.get(1));

        if (partitionNumber != -1 && numMessages != -1) {
            parsedTopicData.addLine(partitionNumber, numMessages);
        }
    }

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            parsingErrorMessageList.add(new ParsingErrorMessage("unable to transform to number value " + value).setLine(currentLine).setPath(pathToParse));
        }
        return -1L;
    }

    public DateTime convertPathToTimestamp(Path pathToParse) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        try {
            String name = pathToParse.getParent().getFileName().toString();
            final Date convertedCurrentDate = sdf.parse(pathToParse.getParent().getFileName().toString());
            return new DateTime(convertedCurrentDate);
        } catch (ParseException ex) {
            parsingErrorMessageList.add(new ParsingErrorMessage("Exception while trying to convert dir to date-time " + ex).setPath(pathToParse));
            return null;
        } catch (NullPointerException ex) {
            parsingErrorMessageList.add(new ParsingErrorMessage("Error acquiring parent. Directory structure is wrong").setPath(pathToParse));
            return null;
        }
    }

    private List<String> readFileToMemory() {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(pathToParse);
        } catch (IOException ex) {
            parsingErrorMessageList.add(new ParsingErrorMessage("Exception while reading file:\n" + ex.getMessage()).setPath(pathToParse));
        }
        return lines;
    }

    public List<ParsingErrorMessage> errors() {
        return parsingErrorMessageList;
    }

    public ParsedTopicData parsedTopicData() {
        return parsedTopicData;
    }
}
