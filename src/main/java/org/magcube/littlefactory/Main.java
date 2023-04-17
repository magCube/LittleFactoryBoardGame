package org.magcube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.magcube.littlefactory.card.BuildingCard;
import org.magcube.littlefactory.card.CardType;
import org.magcube.littlefactory.card.ResourceCard;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

//TODO: migrate as spring boot
@SpringBootApplication
public class Main {

  public static void main(String[] args) {
    System.out.println("Hello world!");
  }

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {

      System.out.println("Let's inspect the beans provided by Spring Boot:");

      String[] beanNames = ctx.getBeanDefinitionNames();
      Arrays.sort(beanNames);
      for (String beanName : beanNames) {
        System.out.println(beanName);
      }
    };
  }
}