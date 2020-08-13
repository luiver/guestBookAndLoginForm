package com.codecool;

import com.codecool.handlers.CookieHandler;
import com.codecool.handlers.LoginHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login implements HttpHandler {
    private final List<User> allUsers;
    private final Map<String, User> loggedUsers;
    private final CookieHandler cookieHandler;
    private final LoginHandler loginHandler;

    public Login(TempDatabase tempDatabase) {
        allUsers = tempDatabase.getAllUsers();
        loggedUsers = tempDatabase.getLoggedUsers();
        cookieHandler = new CookieHandler();
        loginHandler = new LoginHandler();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response;
        String method = httpExchange.getRequestMethod();

        if (method.equals("POST")) {
            String formData = loginHandler.getFormData(httpExchange);
            Map<String, String> inputs = loginHandler.parseFormData(formData);
            if (checkIfFormIsNotEmpty(inputs) && checkIfUserExists(inputs)) {
                String sessionID = cookieHandler.createCookie(httpExchange);
                loggedUsers.put(sessionID, new User(inputs.get("username"), inputs.get("password")));
                JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/welcomePage.twig");
                JtwigModel model = JtwigModel.newModel();
                model.with("username", inputs.get("username"));
                response = template.render(model);
            } else {
                JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/loginPage.twig");
                JtwigModel model = JtwigModel.newModel();
                model.with("username", inputs.get("username"));
                model.with("wrongCredentials", "Wrong Username or Password!");
                response = template.render(model);
            }
        } else {
            if (sessionIdExists(httpExchange)) {
                response = loadUserData(httpExchange);
            } else {
                JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/loginPage.twig");
                JtwigModel model = JtwigModel.newModel();
                response = template.render(model);
            }
        }
        loginHandler.send200(httpExchange,response);
    }

    private String loadUserData(HttpExchange httpExchange) {
        String username = getUsernameBasedOnCookie(httpExchange);
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/welcomePage.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("username", username);
        return template.render(model);
    }

    private String getUsernameBasedOnCookie(HttpExchange httpExchange) {
        String sessionID = getExtractedCookie(httpExchange);
        User user = loggedUsers.get(sessionID);
        return user.getUsername();
    }

    private boolean sessionIdExists(HttpExchange httpExchange) {
        if (loggedUsers.size() > 0) {
            String sessionId = getExtractedCookie(httpExchange);
            return loggedUsers.containsKey(sessionId);
        } else {
            return false;
        }
    }

    private String getExtractedCookie(HttpExchange httpExchange) {
        String cookie = httpExchange.getRequestHeaders().getFirst("Cookie");
        return cookie.replace("\"", "").replace("sessionId=", "");
    }

    private boolean checkIfUserExists(Map<String, String> inputs) {
        for (User user : allUsers) {
            if (user.getUsername().equals(inputs.get("username")) && user.getPassword().equals(inputs.get("password"))) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfFormIsNotEmpty(Map<String, String> inputs) {
        return !(inputs.get("username").equals("") || inputs.get("password").equals(""));
    }
}
