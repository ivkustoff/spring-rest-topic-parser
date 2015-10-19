package com.github.ivkustoff.app.model.parsing;


import org.joda.time.DateTime;

import java.util.*;

public class ParsedTopicData {
    private final DateTime launchTime;
    private final Map<Integer, List<Long>> partitionNumberMessages = new HashMap<>();
    private final String name;

    public ParsedTopicData(final DateTime launchTime, final String name) {
        this.launchTime = launchTime;
        this.name = name;
    }

    public DateTime launchTime() {
        return launchTime;
    }

    public String name() {
        return name;
    }

    public Map<Integer, Long> partitionSumMessagesMap() {
        Map<Integer, Long> resultMap = new HashMap<>();
        for (final Integer key : partitionNumberMessages.keySet()) {
            final long sum = partitionNumberMessages.get(key).stream().mapToInt(i -> i.intValue()).sum();
            resultMap.put(key, sum);
        }
        return resultMap;
    }

    public void addLine(final Integer partitionNumber, final Long messages) {
        if (partitionNumberMessages.containsKey(partitionNumber)) {
            partitionNumberMessages.get(partitionNumber).add(messages);
        } else {
            partitionNumberMessages.put(partitionNumber, new ArrayList<>(Arrays.asList(messages)));
        }
    }

    @Override
    public String toString() {
        String launchTime = this.launchTime == null ? "null" : this.launchTime.toString();
        return "ParsedData: launchTime ->" + launchTime + "\n" +
                "PartitionsMap: " + this.partitionNumberMessages.toString() + "\n" +
                "name: " + this.name + "\n";
    }
}
