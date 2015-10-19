package com.github.ivkustoff.app;

import com.github.ivkustoff.app.model.parsing.CSVFileParser;
import com.github.ivkustoff.app.model.parsing.ParsedTopicData;
import com.github.ivkustoff.app.model.real.RealTopic;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class RealTopicTest {
    private static CSVGenerator generator;
    @BeforeClass
    public static void before() {
        generator = new CSVGenerator();
    }

    @Test
    public void testRealTopicCreation() {
        List<String> values = Arrays.asList("1,1", "1,", "Imparseble,851", "a112,814bz", "220,330");
        generator.generateNormalFile(values);
        CSVFileParser parser = new CSVFileParser(generator.getCurrentPath(), "apulaz");
        ParsedTopicData topicData = parser.parseFile().parsedTopicData();
        RealTopic realTopic = new RealTopic(topicData).generateTopic();
        assertEquals("apulaz",realTopic.name());
        assertEquals(165,realTopic.avgMessagesPerPartition());
        assertEquals(2,realTopic.mapPartitionSumMessages().size());
        assertEquals(1,realTopic.minumumMessagesInPartition());
        assertNotNull(realTopic.lastLaunchTime());
        assertEquals(330,realTopic.maximumMessagesInPartition());
        assertEquals(331,realTopic.totalMessages());
    }

    @Test
    public void testRealTopicZeroStats() {
        List<String> values = Arrays.asList("1,0", "1,1", "2,0");
        generator.generateNormalFile(values);
        CSVFileParser parser = new CSVFileParser(generator.getCurrentPath(), "apulaz");
        ParsedTopicData topicData = parser.parseFile().parsedTopicData();
        RealTopic realTopic = new RealTopic(topicData).generateTopic();
        assertEquals("apulaz",realTopic.name());
        assertEquals(0,realTopic.avgMessagesPerPartition());
        assertEquals(2,realTopic.mapPartitionSumMessages().size());
        assertEquals(0,realTopic.minumumMessagesInPartition());
        assertNotNull(realTopic.lastLaunchTime());
        assertEquals(1,realTopic.maximumMessagesInPartition());
        assertEquals(1,realTopic.totalMessages());
    }


    @After
    public void cleanup() {
        new FileDeleter().delete(generator.getCurrentPath().toAbsolutePath().toFile());
    }
}
