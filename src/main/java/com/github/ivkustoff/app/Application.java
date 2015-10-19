/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ivkustoff.app;

import com.github.ivkustoff.app.model.Topic;
import com.github.ivkustoff.app.model.parsing.DirCrawler;
import com.github.ivkustoff.app.model.parsing.ParsedTopicData;
import com.github.ivkustoff.app.model.real.RealTopic;
import com.github.ivkustoff.app.repository.TopicRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan({"com.github.ivkustoff.app"})
public class Application {

    public static void main(String[] args) {
        if  (args != null && args.length > 0) {
            Path testRoot = Paths.get(args[0]);
            if (testRoot.toFile().exists()) {
                System.out.println("Parsing data...");
                DirCrawler crawler = new DirCrawler(testRoot).crawl();
                System.out.println(crawler.errors());
                List<ParsedTopicData> parsedTopicData = crawler.topics();
                List<Topic> realTopics = new ArrayList<>();
                for (ParsedTopicData topicData : parsedTopicData) {
                    realTopics.add(new RealTopic(topicData).generateTopic());
                }
                System.out.println("Starting server...");
                ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
                applicationContext.getBean(TopicRepository.class).addTopics(realTopics);
            }
        } else {
            System.out.println("Please provide topicRoot directory as program argument");
        }
    }
}
