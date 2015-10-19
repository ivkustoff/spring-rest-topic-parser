package com.github.ivkustoff.app.repository;

import com.github.ivkustoff.app.model.Topic;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TopicRepository {
    public Map<String, Topic> topics = new HashMap<>();

    public Topic getByName(final String topicName) {
        return topics.get(topicName);
    }

    public void addTopic(Topic topic) {
        topics.put(topic.name(), topic);
    }

    public void addTopics(List<Topic> topics) {
        topics.forEach(topic -> addTopic(topic));
    }

    public boolean exists(final String topicName) {
        return topics.containsKey(topicName);
    }
}
