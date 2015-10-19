package com.github.ivkustoff.app.restapi;

import com.github.ivkustoff.app.restapi.dto.LastRunTimestampForTopic;
import com.github.ivkustoff.app.restapi.dto.PartitionsAndMessagesInCurrentTopic;
import com.github.ivkustoff.app.restapi.dto.TopicList;
import com.github.ivkustoff.app.restapi.dto.TopicStats;
import com.github.ivkustoff.app.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class TopicController implements ErrorController {
    @Autowired
    public TopicService topicService;

    @RequestMapping(value = "/topics", method = RequestMethod.GET)
    public TopicList listTopics() {
        return topicService.listTopics();
    }

    @RequestMapping(value = "/topics/{name}", method = RequestMethod.GET)
    public PartitionsAndMessagesInCurrentTopic currentTopicData(@PathVariable("name") final String topicName) {
        return topicService.partitionsAndMessagesInCurrentTopic(topicName);
    }

    @RequestMapping(value = "/topics/{name}/lastrun", method = RequestMethod.GET)
    public LastRunTimestampForTopic lastruntime(@PathVariable("name") final String topicName) {
        return topicService.lastRunTimestampForTopic(topicName);
    }

    @RequestMapping(value = "/topics/{name}/stats", method = RequestMethod.GET)
    public TopicStats topicStats(@PathVariable("name") final String topicName) {
        return topicService.topicStatsForCurrentTopic(topicName);
    }

    @ExceptionHandler
    void handleNotFoundException(NotFoundException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = "/error")
    public String error() {
        return "Topic with this name is not found";
    }
}
