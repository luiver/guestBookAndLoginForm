package com.codecool;

import com.codecool.handlers.CookieHandler;
import com.codecool.handlers.LoginHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class Logout implements HttpHandler {
    private final CookieHandler cookieHandler;
    private final TempDatabase db;
    private final LoginHandler loginHandler;
    
    public Logout(TempDatabase tempDatabase) {
        cookieHandler = new CookieHandler();
        this.db = tempDatabase;
        loginHandler = new LoginHandler();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        db.getLoggedUsers().remove(cookieHandler.getExtractedCookie(httpExchange));
        cookieHandler.removeCookie(httpExchange);
        loginHandler.redirectToLoginPage(httpExchange);
    }


}
