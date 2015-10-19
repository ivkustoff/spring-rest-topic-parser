package com.github.ivkustoff.app;

import com.github.ivkustoff.app.model.Topic;
import com.github.ivkustoff.app.model.parsing.DirCrawler;
import com.github.ivkustoff.app.model.parsing.ParsedTopicData;
import com.github.ivkustoff.app.model.real.RealTopic;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class DirCrawlerTest {
    private static CSVGenerator generator;

    @BeforeClass
    public static void before() {
        generator = new CSVGenerator();
    }

    @Test
    public void testParsing() {
        List<String> topicNames = Arrays.asList("apulaz", "arnold", "fear", "destruction");
        List<String> topicLines = Arrays.asList("1,0", "22,22", "1,", "1,1", "13,18", "12,98");
        List<String> timeStamps = Arrays.asList("2015-10-19-00-00-00", "2015-10-09-23-23-23", "garbageDir");
        generator.generateDirectoryTree(topicNames, topicLines, timeStamps);
        Path pathToWalk = generator.getCurrentPath().toAbsolutePath();
        List<ParsedTopicData> parsedTopicData = new DirCrawler(pathToWalk).crawl().topics();
        List<RealTopic> realTopics = new ArrayList<>();
        for (ParsedTopicData topicData : parsedTopicData) {
            realTopics.add(new RealTopic(topicData).generateTopic());
        }
        assertEquals(4, realTopics.size());
        Topic first = realTopics.get(0);
        Topic second = realTopics.get(1);
        Topic third = realTopics.get(2);
        Topic fourth = realTopics.get(3);

        assertEquals("apulaz", first.name());
        assertEquals(34, first.avgMessagesPerPartition());
        assertEquals(4, first.mapPartitionSumMessages().size());
        assertEquals(1, first.minumumMessagesInPartition());
        assertNotNull(first.lastLaunchTime());
        assertEquals(98, first.maximumMessagesInPartition());
        assertEquals(139, first.totalMessages());

        assertEquals("arnold", second.name());
        assertEquals(34, second.avgMessagesPerPartition());
        assertEquals(4, second.mapPartitionSumMessages().size());
        assertEquals(1, second.minumumMessagesInPartition());
        assertNotNull(second.lastLaunchTime());
        assertEquals(98, second.maximumMessagesInPartition());
        assertEquals(139, second.totalMessages());

        assertEquals("destruction", third.name());
        assertEquals(34, third.avgMessagesPerPartition());
        assertEquals(4, third.mapPartitionSumMessages().size());
        assertEquals(1, third.minumumMessagesInPartition());
        assertNotNull(third.lastLaunchTime());
        assertEquals(98, third.maximumMessagesInPartition());
        assertEquals(139, third.totalMessages());

        assertEquals("fear", fourth.name());
        assertEquals(34, fourth.avgMessagesPerPartition());
        assertEquals(4, fourth.mapPartitionSumMessages().size());
        assertEquals(1, fourth.minumumMessagesInPartition());
        assertNotNull(fourth.lastLaunchTime());
        assertEquals(98, fourth.maximumMessagesInPartition());
        assertEquals(139, fourth.totalMessages());


    }

    @After
    public void cleanup() {
       new FileDeleter().delete(generator.getCurrentPath().toAbsolutePath().toFile());
    }


}
