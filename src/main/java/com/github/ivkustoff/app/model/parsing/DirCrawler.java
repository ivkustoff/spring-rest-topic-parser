package com.github.ivkustoff.app.model.parsing;

import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirCrawler {
    private final String TIMESTAMP_PATTERN = "\\d{4}-\\d{2}-\\d{2}-\\d{2}-\\d{2}-\\d{2}";
    private final Path startDir;
    private List<ParsedTopicData> topics = new ArrayList<>();
    List<ParsingErrorMessage> errorMessages = new ArrayList<>();

    public DirCrawler(Path startDir) {
        this.startDir = startDir;
    }

    public DirCrawler crawl() {
        Stream<Path> topics = Stream.empty();
        try {
            topics = Files.list(startDir);
        } catch (IOException ex) {
            errorMessages.add(new ParsingErrorMessage("Problem warking directory structure").setPath(startDir));
        }
        Iterator<Path> iterator = topics.iterator();
        while (iterator.hasNext()) {
            final Path topicRoot = iterator.next();
            final Path historyRoot = Paths.get(topicRoot.toString(), "history");
            if (historyRoot.toFile().exists()) {
                Stream<Path> childs = Stream.empty();
                try {
                    childs = Files.list(historyRoot);
                } catch (IOException ex) {
                    errorMessages.add(new ParsingErrorMessage("Problem warking directory structure").setPath(historyRoot));
                }
                Path mostRecentRun = sortChilds(childs);
                if (mostRecentRun != null) {
                    Path mostRecentRunOffsets = Paths.get(mostRecentRun.toAbsolutePath().toString(), "offsets.csv");
                    CSVFileParser parser = new CSVFileParser(mostRecentRunOffsets, topicRoot.getFileName().toString()).parseFile();
                    this.errorMessages.addAll(parser.errors());
                    this.topics.add(parser.parsedTopicData());
                }

            }
        }

        return this;
    }

    private Path sortChilds(Stream<Path> childs) {
        final List<Path> filteredTimestamps = childs.filter(s -> s.getFileName().toString().matches(TIMESTAMP_PATTERN)).collect(Collectors.toList());
        final Comparator<Path> pathComparator = pathComparator();
        return filteredTimestamps.stream().max(pathComparator).get();
    }


    public List<ParsedTopicData> topics() {
        return topics;
    }

    public List<ParsingErrorMessage> errors() {
        return errorMessages;
    }


    public static Comparator<Path> pathComparator() {
        return new Comparator<Path>() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

            @Override
            public int compare(Path left, Path right) {
                try {
                    DateTime leftTime = new DateTime(sdf.parse(left.getFileName().toString()));
                    DateTime rightTime = new DateTime(sdf.parse(right.getFileName().toString()));
                    return leftTime.compareTo(rightTime);
                } catch (ParseException ex) {
                    return 0;
                } catch (Exception ex) {
                    return 0;
                }
            }
        };
    }
}
