package com.github.ivkustoff.app.model.real;

import com.github.ivkustoff.app.model.Topic;
import com.github.ivkustoff.app.model.parsing.ParsedTopicData;
import org.joda.time.DateTime;

import java.util.Comparator;
import java.util.Map;

public class RealTopic implements Topic {
    private TopicStats topicStats = new TopicStats();
    private final String name;
    private ParsedTopicData parsedData;
    private Map<Integer, Long> mapPartitionSumMessages;

    public RealTopic(ParsedTopicData parsedTopicData) {
        this.parsedData = parsedTopicData;
        this.name = parsedTopicData.name();
    }

    public RealTopic generateTopic() {
        if (parsedData != null) {
            mapPartitionSumMessages = parsedData.partitionSumMessagesMap();
            final Comparator<? super Map.Entry<Integer, Long>> valueComparator = (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue());
            final Map.Entry<Integer, Long> entryMin = mapPartitionSumMessages.entrySet().stream().min(valueComparator).get();
            final Map.Entry<Integer, Long> entryMax = mapPartitionSumMessages.entrySet().stream().max(valueComparator).get();
            final long sumValues = mapPartitionSumMessages.values().stream().mapToLong(l -> l.longValue()).sum();
            final long amountOfPartitions = mapPartitionSumMessages.keySet().stream().count();
            long avgMessages = 0;
            if (amountOfPartitions > 0) {
                avgMessages = sumValues / amountOfPartitions;
            }
            topicStats = new TopicStats()
                    .setMinMessages(entryMin.getValue())
                    .setMaxMessages(entryMax.getValue())
                    .setAvgMessages(avgMessages)
                    .setLastLaunchTime(parsedData.launchTime())
                    .setSumMessages(sumValues);
        }
        return this;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public long avgMessagesPerPartition() {
        return topicStats.avgMessages;
    }

    @Override
    public long minumumMessagesInPartition() {
        return topicStats.minMessages;
    }

    @Override
    public long totalMessages() {
        return topicStats.sumMessages;
    }

    @Override
    public long maximumMessagesInPartition() {
        return topicStats.maxMessages;
    }

    @Override
    public DateTime lastLaunchTime() {
        return topicStats.lastLaunchTime;
    }

    @Override
    public Map<Integer, Long> mapPartitionSumMessages() {
        return this.mapPartitionSumMessages;
    }

    static class TopicStats {
        private DateTime lastLaunchTime = null;
        private long avgMessages;
        private long minMessages;
        private long sumMessages;
        private long maxMessages;

        public TopicStats setAvgMessages(long messages) {
            this.avgMessages = messages;
            return this;
        }

        public TopicStats setMinMessages(long messages) {
            this.minMessages = messages;
            return this;
        }

        public TopicStats setSumMessages(long messages) {
            this.sumMessages = messages;
            return this;
        }

        public TopicStats setMaxMessages(long messages) {
            this.maxMessages = messages;
            return this;
        }

        public TopicStats setLastLaunchTime(DateTime time) {
            this.lastLaunchTime = time;
            return this;
        }

    }

    @Override
    public boolean equals(Object otherTopic) {
        if (!(otherTopic instanceof RealTopic)) {
            return false;
        } else {
            return this.name() == ((RealTopic) otherTopic).name();
        }
    }
}



