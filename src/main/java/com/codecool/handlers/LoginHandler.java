package com.codecool.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginHandler {

    public void redirectToLoginPage(HttpExchange httpExchange) throws IOException {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.set("Location", "login");
        httpExchange.sendResponseHeaders(302, 0);
        httpExchange.close();
    }

    public void send200(HttpExchange httpExchange, String response) {
        try {
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFormData(HttpExchange httpExchange) {
        String formData = null;
        try {
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            formData = br.readLine();
            System.out.println(formData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formData;
    }

    public Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = "";
            String value = "";
            if (keyValue.length > 0) {
                key = new URLDecoder().decode(keyValue[0], System.getProperty("file.encoding"));
            }
            if (keyValue.length > 1) {
                value = new URLDecoder().decode(keyValue[1], System.getProperty("file.encoding"));
            }
            map.put(key, value);
        }
        return map;
    }

    public boolean checkIfFormIsNotEmpty(Map<String, String> inputs) {
        return !(inputs.get("username").equals("") || inputs.get("password").equals(""));
    }
}

