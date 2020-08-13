package com.codecool.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginHandler {


    public void send200(HttpExchange httpExchange, String response) {
        try {
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (Exception e) {
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
            // We have to decode the value because it's urlencoded. see: https://en.wikipedia.org/wiki/POST_(HTTP)#Use_for_submitting_web_forms
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
}

