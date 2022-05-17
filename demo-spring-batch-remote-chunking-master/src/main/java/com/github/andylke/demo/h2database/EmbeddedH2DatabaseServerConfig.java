package com.github.andylke.demo.h2database;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedH2DatabaseServerConfig {

  @Bean(initMethod = "start", destroyMethod = "stop")
  public Server embeddedH2DatabaseServer() throws SQLException {
    return Server.createTcpServer("-tcp", "-tcpPort", "9090", "-tcpAllowOthers", "-webAllowOthers");
  }
}
