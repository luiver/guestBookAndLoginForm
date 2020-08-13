package com.codecool.handlers;

import com.codecool.helpers.CookieHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

public class CookieHandler {
    private static final String SESSION_COOKIE_NAME = "sessionId";
    CookieHelper cookieHelper = new CookieHelper();

    public Optional<HttpCookie> getSessionIdCookie(HttpExchange httpExchange){
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
        List<HttpCookie> cookies = cookieHelper.parseCookies(cookieStr);
        return cookieHelper.findCookieByName(SESSION_COOKIE_NAME, cookies);
    }

    private String generateSessionID() { //todo check if sessionId already exists
        final int randNumOrigin = 48;
        final int randNumBound = 122;
        final int len = 15;
        SecureRandom random = new SecureRandom();
        return random.ints(randNumOrigin, randNumBound + 1)
                .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i))
                .limit(len)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }

    public String createCookie(HttpExchange httpExchange) {
        Optional<HttpCookie> cookie = getSessionIdCookie(httpExchange);
        String sessionId = generateSessionID();
        cookie = Optional.of(new HttpCookie(SESSION_COOKIE_NAME, sessionId));
        httpExchange.getResponseHeaders().add("Set-Cookie", cookie.get().toString());
        return sessionId;
    }

    public void removeCookie(HttpExchange httpExchange) {
        String requestURI = httpExchange.getRequestURI().toString();
        System.out.println(requestURI);

        String cookie = httpExchange.getRequestHeaders().getFirst("Cookie") + ";Max-age=0";
        httpExchange.getResponseHeaders().set("Set-Cookie", cookie);
    }

    private String getExtractedCookie(HttpExchange httpExchange) {
        String cookie = httpExchange.getRequestHeaders().getFirst("Cookie");
        return cookie.replace("\"", "").replace("sessionId=", "");
    }
}
