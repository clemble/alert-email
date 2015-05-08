package com.clemble.casino.server.email.controller;

import com.clemble.casino.WebMapping;
import com.clemble.casino.player.PlayerEmailWebMapping;
import com.clemble.casino.player.service.PlayerEmailService;
import com.clemble.casino.server.ExternalController;
import com.clemble.casino.server.email.service.ServerPlayerEmailService;
import org.apache.http.HttpResponse;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by mavarazy on 12/8/14.
 */
@RestController
public class PlayerEmailController implements PlayerEmailService, ExternalController {

    final private String redirect;
    final private ServerPlayerEmailService playerEmailService;

    public PlayerEmailController(String redirect, ServerPlayerEmailService playerEmailService) {
        this.redirect = redirect;
        this.playerEmailService = playerEmailService;
    }

    @Override
    public String myEmail() {
        throw new UnsupportedOperationException();
    }


    @RequestMapping(method = RequestMethod.GET, value = PlayerEmailWebMapping.MY, produces = WebMapping.PRODUCES)
    public String myEmail(@CookieValue("player") String player) {
        return playerEmailService.myEmail(player);
    }

    @Override
    public boolean verify(@RequestParam("code") String verificationCode) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = RequestMethod.GET, value = PlayerEmailWebMapping.VERIFY)
    public void verify(@RequestParam("code") String verificationCode, HttpServletResponse response) throws IOException {
        if (playerEmailService.verify(verificationCode)) {
            response.sendRedirect(redirect);
        } else {
            response.getWriter().append("Can't verify this code, request again");
        }
    }

}
