package com.example.advanced;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class AdvancedApplication {
  public static final String APPLICATION_LOCATIONS = "spring.config.location="
          + "optional:classpath:application.properties,"
          + "optional:/usr/local/myapp/application.properties";


  public static void main(String[] args) {


    new SpringApplicationBuilder(AdvancedApplication.class)
            .properties(APPLICATION_LOCATIONS)
            .run(args);
  }

}