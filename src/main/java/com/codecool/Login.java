package com.codecool;

import com.codecool.handlers.CookieHandler;
import com.codecool.handlers.LoginHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.util.Map;

public class Login implements HttpHandler {
    private final CookieHandler cookieHandler;
    private final LoginHandler loginHandler;
    private final TempDatabase db;

    public Login(TempDatabase tempDatabase) {
        this.db = tempDatabase;
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
            if (loginHandler.checkIfFormIsNotEmpty(inputs) && checkIfUserExists(inputs)) {
                String sessionID = cookieHandler.createCookie(httpExchange);
                db.getLoggedUsers().put(sessionID, new User(inputs.get("username"), inputs.get("password")));
                response = defaultModelResponse("templates/welcomePage.twig",inputs.get("username"));
            } else {
                response = modelWrongResponse(inputs);
            }
        } else {
            if (checkIfSessionIdExists(httpExchange)) {
                response = loadUserData(httpExchange);
            } else {
                response = defaultModelResponse("templates/loginPage.twig","");
            }
        }
        loginHandler.send200(httpExchange, response);
    }

    private String modelWrongResponse(Map<String, String> inputs) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/loginPage.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("username", inputs.get("username"));
        model.with("wrongCredentials", "Wrong Username or Password!");
        return template.render(model);
    }

    private String defaultModelResponse(String templatePath, String username){
        JtwigTemplate template = JtwigTemplate.classpathTemplate(templatePath);
        JtwigModel model = JtwigModel.newModel();
        model.with("username", username);
        return template.render(model);
    }

    private String loadUserData(HttpExchange httpExchange) {
        String username = getUsernameBasedOnCookie(httpExchange);
        return defaultModelResponse("templates/welcomePage.twig", username);
    }

    private String getUsernameBasedOnCookie(HttpExchange httpExchange) {
        String sessionID = cookieHandler.getExtractedCookie(httpExchange);
        User user = db.getLoggedUsers().get(sessionID);
        return user.getUsername();
    }

    private boolean checkIfSessionIdExists(HttpExchange httpExchange) {
        if (cookieHandler.getSessionIdCookie(httpExchange).isPresent()) {
            String sessionId = cookieHandler.getExtractedCookie(httpExchange);
            return db.getLoggedUsers().containsKey(sessionId);
        } else {
            return false;
        }
    }

    private boolean checkIfUserExists(Map<String, String> inputs) {
        for (User user : db.getAllUsers()) {
            if (user.getUsername().equals(inputs.get("username")) && user.getPassword().equals(inputs.get("password"))) {
                return true;
            }
        }
        return false;
    }
}
