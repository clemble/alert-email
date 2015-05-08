package com.clemble.casino.server.email.service;

import com.clemble.casino.server.ServerService;

/**
 * Created by mavarazy on 12/8/14.
 */
public interface ServerEmailSender extends ServerService {

    void send(String email, String text);

}
