package com.clemble.casino.server.email.spring;

import com.clemble.casino.server.spring.WebBootSpringConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by mavarazy on 3/31/15.
 */
@Configuration
@Import({ WebBootSpringConfiguration.class, PlayerEmailSpringConfiguration.class })
public class PlayerEmailApplication {

    public static void main(String args[]) {
        SpringApplication.run(PlayerEmailSpringConfiguration.class, args);
    }

}
