package com.github.ivkustoff.app.service;

import com.github.ivkustoff.app.model.Topic;
import com.github.ivkustoff.app.repository.TopicRepository;
import com.github.ivkustoff.app.restapi.NotFoundException;
import com.github.ivkustoff.app.restapi.dto.LastRunTimestampForTopic;
import com.github.ivkustoff.app.restapi.dto.PartitionsAndMessagesInCurrentTopic;
import com.github.ivkustoff.app.restapi.dto.TopicList;
import com.github.ivkustoff.app.restapi.dto.TopicStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;

    public TopicList listTopics() {
        final TopicList topicList = new TopicList();
        topicList.topicNames = new ArrayList<>(topicRepository.topics.keySet());
        return topicList;
    }

    public LastRunTimestampForTopic lastRunTimestampForTopic(final String topicName) {
        if (topicRepository.exists(topicName)) {
            final LastRunTimestampForTopic lastRunTimestampForTopic = new LastRunTimestampForTopic();
            lastRunTimestampForTopic.lastRunTimestamp = topicRepository.getByName(topicName).lastLaunchTime();
            lastRunTimestampForTopic.name = topicRepository.getByName(topicName).name();
            return lastRunTimestampForTopic;
        } else {
            throw new NotFoundException();
        }
    }


    public PartitionsAndMessagesInCurrentTopic partitionsAndMessagesInCurrentTopic(final String topicName) {
        if (topicRepository.exists(topicName)) {
            final Topic topic = topicRepository.getByName(topicName);
            final PartitionsAndMessagesInCurrentTopic partitionsAndMessagesInCurrentTopic = new PartitionsAndMessagesInCurrentTopic();
            partitionsAndMessagesInCurrentTopic.partitionsMessages = topic.mapPartitionSumMessages();
            partitionsAndMessagesInCurrentTopic.lastRunTimestamp = topic.lastLaunchTime();
            partitionsAndMessagesInCurrentTopic.name = topic.name();
            return partitionsAndMessagesInCurrentTopic;
        } else {
            throw new NotFoundException();
        }
    }

    public TopicStats topicStatsForCurrentTopic(final String topicName) {
        if (topicRepository.exists(topicName)) {
            final Topic topic = topicRepository.getByName(topicName);
            final TopicStats topicStats = new TopicStats();
            topicStats.avgMessagesInPartition = topic.avgMessagesPerPartition();
            topicStats.maxMessagesInPartition = topic.maximumMessagesInPartition();
            topicStats.minMessagesInPartition = topic.minumumMessagesInPartition();
            topicStats.totalMessages = topic.totalMessages();
            return topicStats;
        } else {
            throw new NotFoundException();
        }
    }
}
