package com.github.andylke.demo.randomuser;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import com.github.andylke.demo.user.User;
import com.github.andylke.demo.user.UserRepository;

@Configuration
public class ImportRandomUserWorkerStepConfig {

  @Autowired private RemoteChunkingWorkerBuilder<RandomUser, User> workerBuilder;

  @Autowired private UserRepository userRepository;

  @Bean
  public IntegrationFlow importRandomUserWorkerStep() {
    return workerBuilder
        .itemProcessor(randomUserToUserProcessor())
        .itemWriter(userRepositoryWriter())
        .inputChannel(importRandomUserRequestsChannel())
        .outputChannel(importRandomUserRepliesChannel())
        .build();
  }

  @Bean
  public RandomUserToUserProcessor randomUserToUserProcessor() {
    return new RandomUserToUserProcessor();
  }

  @Bean
  public RepositoryItemWriter<User> userRepositoryWriter() {
    return new RepositoryItemWriterBuilder<User>().repository(userRepository).build();
  }

  @Bean
  public QueueChannel importRandomUserRequestsChannel() {
    return new QueueChannel();
  }

  @Bean
  public IntegrationFlow importRandomUserRequestsFlow(ActiveMQConnectionFactory connectionFactory) {
    return IntegrationFlows.from(
            Jms.messageDrivenChannelAdapter(connectionFactory)
                .destination("import-random-user-requests"))
        .channel(importRandomUserRequestsChannel())
        .get();
  }

  @Bean
  public QueueChannel importRandomUserRepliesChannel() {
    return new QueueChannel();
  }

  @Bean
  public IntegrationFlow importRandomUserRepliesFlow(ActiveMQConnectionFactory connectionFactory) {
    return IntegrationFlows.from(importRandomUserRepliesChannel())
        .handle(Jms.outboundAdapter(connectionFactory).destination("import-random-user-replies"))
        .get();
  }
}
