package com.codecool;

import com.codecool.handlers.CookieHandler;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class Logout implements HttpHandler {
    private final CookieHandler cookieHandler;
    private final Map<String, User> loggedUsers;

    public Logout(TempDatabase tempDatabase) {
        cookieHandler = new CookieHandler();
        loggedUsers = tempDatabase.getLoggedUsers();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        loggedUsers.remove(getExtractedCookie(httpExchange));
        cookieHandler.removeCookie(httpExchange);

        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.set("Location", "login");
        httpExchange.sendResponseHeaders(302, 0);
        httpExchange.close();


    }

    private String getExtractedCookie(HttpExchange httpExchange) {
        String cookie = httpExchange.getRequestHeaders().getFirst("Cookie");
        return cookie.replace("\"", "").replace("sessionId=", "");
    }
}
