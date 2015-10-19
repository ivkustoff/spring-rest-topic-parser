package com.github.ivkustoff.app;


import com.github.ivkustoff.app.model.parsing.CSVFileParser;
import com.github.ivkustoff.app.model.parsing.ParsedTopicData;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;


import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;

public class CSVFileParserTest {
    private static CSVGenerator generator;
    @BeforeClass
    public static void before() {
        generator = new CSVGenerator();
    }

    @After
    public void cleanup() {
        new FileDeleter().delete(generator.getCurrentPath().toAbsolutePath().toFile());
    }

    @Test
    public void testEmptyFile()  {
        generator.generateEmptyFileNoParent();
        CSVFileParser parser = new CSVFileParser(generator.getCurrentPath(), "apulaz");
        assertFalse(parser.parseFile().errors().isEmpty());
    }

    @Test
    public void testEmptyFileParsing() {
        generator.generateEmptyFileNormalParent();
        CSVFileParser parser = new CSVFileParser(generator.getCurrentPath(), "apulaz");
        parser.parseFile();
        assertEquals(0,parser.errors().size());
        assertEquals(0,parser.parsedTopicData().partitionSumMessagesMap().size());
        assertNotNull(parser.parsedTopicData().launchTime());
        assertNotNull(parser.parsedTopicData().name());
    }

    @Test
    public void testDateParsing() {
        String dateString = "2015-10-19-13-18-13";
        generator.generateEmptyFileNormalParentWithDateString(dateString);
        CSVFileParser parser = new CSVFileParser(generator.getCurrentPath(), "apulaz");
        DateTime launchTime = parser.parseFile().parsedTopicData().launchTime();
        assertEquals("2015",launchTime.toString("yyyy"));
        assertEquals("10",launchTime.toString("MM"));
        assertEquals("19",launchTime.toString("dd"));
        assertEquals("13",launchTime.toString("HH"));
        assertEquals("18",launchTime.toString("mm"));
        assertEquals("13",launchTime.toString("ss"));
    }

    @Test
    public void testParsingNormalData() {
        List<String> values = Arrays.asList("1,1", "2,2", "3,851");
        generator.generateNormalFile(values);
        CSVFileParser parser = new CSVFileParser(generator.getCurrentPath(), "apulaz");
        ParsedTopicData topicData = parser.parseFile().parsedTopicData();
        assertEquals(topicData.name(),"apulaz");
        assertEquals(3,topicData.partitionSumMessagesMap().size());
        assertTrue(topicData.partitionSumMessagesMap().containsKey(1));
        assertTrue(topicData.partitionSumMessagesMap().containsKey(2));
        assertTrue(topicData.partitionSumMessagesMap().containsKey(3));
        assertEquals(1L, topicData.partitionSumMessagesMap().get(1).longValue());
        assertEquals(2L, topicData.partitionSumMessagesMap().get(2).longValue());
        assertEquals(851L, topicData.partitionSumMessagesMap().get(3).longValue());
    }


    @Test
    public void testParsingRepeatedData() {
        List<String> values = Arrays.asList("1,1", "1,2", "3,851");
        generator.generateNormalFile(values);
        CSVFileParser parser = new CSVFileParser(generator.getCurrentPath(), "apulaz");
        ParsedTopicData topicData = parser.parseFile().parsedTopicData();
        assertEquals(topicData.name(),"apulaz");
        assertEquals(2,topicData.partitionSumMessagesMap().size());
        assertTrue(topicData.partitionSumMessagesMap().containsKey(1));
        assertTrue(topicData.partitionSumMessagesMap().containsKey(3));
        assertEquals(3L, topicData.partitionSumMessagesMap().get(1).longValue());
        assertEquals(851L, topicData.partitionSumMessagesMap().get(3).longValue());
    }

    @Test
    public void testUnparsableData() {
        List<String> values = Arrays.asList("1,1", "1,", "Imparseble,851", "a112,814bz", "220,330");
        generator.generateNormalFile(values);
        CSVFileParser parser = new CSVFileParser(generator.getCurrentPath(), "apulaz");
        ParsedTopicData topicData = parser.parseFile().parsedTopicData();
        assertEquals(topicData.name(),"apulaz");
        assertEquals(2,topicData.partitionSumMessagesMap().size());
        assertTrue(topicData.partitionSumMessagesMap().containsKey(1));
        assertEquals(1L, topicData.partitionSumMessagesMap().get(1).longValue());
        assertEquals(330L, topicData.partitionSumMessagesMap().get(220).longValue());

    }
}
