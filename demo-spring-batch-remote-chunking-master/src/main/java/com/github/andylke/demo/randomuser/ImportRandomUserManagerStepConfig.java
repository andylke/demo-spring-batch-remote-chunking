package com.github.andylke.demo.randomuser;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

@Configuration
public class ImportRandomUserManagerStepConfig {

  @Autowired private RemoteChunkingManagerStepBuilderFactory stepBuilderFactory;

  @Autowired private ImportRandomUserProperties properties;

  @Bean
  public Step importRandomUserManagerStep() {
    return stepBuilderFactory
        .get("importRandomUserManager")
        .chunk(properties.getChunkSize())
        .reader(randomUserFileReader())
        .outputChannel(importRandomUserRequestsChannel())
        .inputChannel(importRandomUserRepliesChannel())
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<? extends RandomUser> randomUserFileReader() {
    return new FlatFileItemReaderBuilder<RandomUser>()
        .name("randomUserFileReader")
        .resource(new FileSystemResource(DownloadRandomUserStepConfig.RANDOM_USER_FILE_PATH))
        .linesToSkip(1)
        .delimited()
        .delimiter("|")
        .names(DownloadRandomUserStepConfig.RANDOM_USER_FIELD_NAMES)
        .targetType(RandomUser.class)
        .build();
  }

  @Bean
  public DirectChannel importRandomUserRequestsChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow importRandomUserRequestsFlow(ActiveMQConnectionFactory connectionFactory) {
    return IntegrationFlows.from(importRandomUserRequestsChannel())
        .handle(Jms.outboundAdapter(connectionFactory).destination("import-random-user-requests"))
        .get();
  }

  @Bean
  public QueueChannel importRandomUserRepliesChannel() {
    return new QueueChannel();
  }

  @Bean
  public IntegrationFlow importRandomUserRepliesFlow(ActiveMQConnectionFactory connectionFactory) {
    return IntegrationFlows.from(
            Jms.messageDrivenChannelAdapter(connectionFactory)
                .destination("import-random-user-replies"))
        .channel(importRandomUserRepliesChannel())
        .get();
  }
}
