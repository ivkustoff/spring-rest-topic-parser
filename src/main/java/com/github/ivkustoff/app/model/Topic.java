package com.github.ivkustoff.app.model;

import org.joda.time.DateTime;

import java.util.Map;

public interface Topic {
    String name();

    long avgMessagesPerPartition();

    long minumumMessagesInPartition();

    long totalMessages();

    long maximumMessagesInPartition();

    DateTime lastLaunchTime();

    Map<Integer, Long> mapPartitionSumMessages();
}
