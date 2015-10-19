package com.github.ivkustoff.app.restapi.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.ivkustoff.app.springStuff.JsonDateSerializer;
import org.joda.time.DateTime;

public class LastRunTimestampForTopic {
    public String name;
    @JsonSerialize(using = JsonDateSerializer.class)
    public DateTime lastRunTimestamp;
}
