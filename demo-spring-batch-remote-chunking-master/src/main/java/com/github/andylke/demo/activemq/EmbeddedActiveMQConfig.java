package com.github.andylke.demo.activemq;

import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ActiveMQConfig.class})
public class EmbeddedActiveMQConfig {

  @Autowired private ActiveMQProperties activeMQProperties;

  @Bean(initMethod = "start", destroyMethod = "stop")
  public BrokerService broker() throws Exception {
    final BrokerService brokerService = new BrokerService();
    brokerService.addConnector(activeMQProperties.getBrokerUrl());
    brokerService.setPersistent(false);
    return brokerService;
  }
}
