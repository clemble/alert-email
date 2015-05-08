package com.clemble.casino.server.email.spring;

import com.clemble.casino.server.email.controller.PlayerEmailController;
import com.clemble.casino.server.email.listener.SystemEmailAddedEventListener;
import com.clemble.casino.server.email.listener.SystemEmailSendRequestEventListener;
import com.clemble.casino.server.email.repository.PlayerEmailRepository;
import com.clemble.casino.server.email.service.MandrillEmailSender;
import com.clemble.casino.server.email.service.ServerEmailSender;
import com.clemble.casino.server.email.service.ServerPlayerEmailService;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import com.clemble.casino.server.player.notification.SystemNotificationServiceListener;
import com.clemble.casino.server.spring.common.CommonSpringConfiguration;
import com.clemble.casino.server.spring.common.MongoSpringConfiguration;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillUserInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.clemble.casino.server.template.MustacheTemplateService;

import java.io.IOException;

/**
 * Created by mavarazy on 12/6/14.
 */
@Configuration
@Import({CommonSpringConfiguration.class, MongoSpringConfiguration.class, PlayerEmailSpringConfiguration.Cloud.class, PlayerEmailSpringConfiguration.Default.class} )
public class PlayerEmailSpringConfiguration implements SpringConfiguration {

    @Bean
    public PlayerEmailRepository playerEmailRepository(MongoRepositoryFactory repositoryFactory) {
        return repositoryFactory.getRepository(PlayerEmailRepository.class);
    }

    @Bean
    public SystemEmailAddedEventListener systemEmailAddedEventListener(
        ServerPlayerEmailService serverPlayerEmailService,
        SystemNotificationServiceListener notificationServiceListener) {
        SystemEmailAddedEventListener emailAddedEventListener = new SystemEmailAddedEventListener(serverPlayerEmailService);
        notificationServiceListener.subscribe(emailAddedEventListener);
        return emailAddedEventListener;
    }

    @Bean
    public SystemEmailSendRequestEventListener systemEmailSendRequestEventListener(
        ServerEmailSender emailSender,
        PlayerEmailRepository emailRepository,
        SystemNotificationServiceListener notificationServiceListener) {
        SystemEmailSendRequestEventListener emailAddedEventListener = new SystemEmailSendRequestEventListener(new MustacheTemplateService(),emailSender, emailRepository);
        notificationServiceListener.subscribe(emailAddedEventListener);
        return emailAddedEventListener;
    }

    @Bean
    public PlayerEmailController playerEmailServiceController(
        @Value("${clemble.registration.token.host}") String host,
        ServerPlayerEmailService playerEmailService){
        return new PlayerEmailController("http://" + host.substring(1), playerEmailService);
    }

    @Bean
    public ServerPlayerEmailService playerEmailService(
        @Value("${clemble.registration.token.host}") String host,
        @Qualifier("emailTextEncryptor") TextEncryptor textEncryptor,
        ServerEmailSender emailService,
        PlayerEmailRepository emailRepository,
        SystemNotificationService systemNotificationService)  {
        return new ServerPlayerEmailService(host, textEncryptor, emailService, emailRepository, systemNotificationService);
    }

    @Configuration
    @Profile({SpringConfiguration.TEST, SpringConfiguration.DEFAULT, SpringConfiguration.INTEGRATION_TEST})
    public static class Default implements SpringConfiguration {

        @Bean
        public ServerEmailSender serverEmailSender() {
            return new ServerEmailSender() {
                @Override
                public void send(String email, String text) {
                }
            };
        }

        @Bean
        public TextEncryptor emailTextEncryptor() {
            return Encryptors.noOpText();
        }

    }

    @Configuration
    @Profile(SpringConfiguration.CLOUD)
    public static class Cloud implements SpringConfiguration {

        @Bean
        public TextEncryptor emailTextEncryptor(
            @Value("${clemble.email.encryptor.password}") String password,
            @Value("${clemble.email.encryptor.salt}") String salt
        ) {
            return Encryptors.queryableText(password, salt);
        }

        @Bean
        public MandrillApi mandrillApi(@Value("${clemble.email.mandrill.key}") String apiKey) throws IOException, MandrillApiError {
            MandrillApi mandrillApi = new MandrillApi(apiKey);
            MandrillUserInfo user = mandrillApi.users().info();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(user));

            return mandrillApi;
        }

        @Bean
        public MandrillEmailSender mandrillPlayerEmailService(MandrillApi mandrillApi) {
            return new MandrillEmailSender(mandrillApi);
        }

    }

}
