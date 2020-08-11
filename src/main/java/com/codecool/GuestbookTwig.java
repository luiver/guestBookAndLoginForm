package com.codecool;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class GuestbookTwig implements HttpHandler {
    List<Entry> entries = new ArrayList<>();
    int id = 0;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        String method = httpExchange.getRequestMethod();

        if (httpExchange.getRequestURI().toString().split("/").length > 2) {
            if (getNthURLArgument(httpExchange,2).equals("delete") && getNthURLArgument(httpExchange,2) != null) {
                deleteEntry(httpExchange, response);

            }
            if (getNthURLArgument(httpExchange,2).equals("edit") && getNthURLArgument(httpExchange,2) != null) {
                try {
                    final int id = Integer.parseInt(getNthURLArgument(httpExchange,3));
                    if (entries.size() > 0) {
                        Entry entry = entries.stream().filter(a -> a.getId() == id).collect(Collectors.toList()).get(0);
                        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/editPage.twig");
                        JtwigModel model = JtwigModel.newModel();
                        model.with("id", entry.getId());
                        model.with("message", entry.getMessage());
                        model.with("name", entry.getName());
                        response = template.render(model);

                        if (method.equals("POST")) {
                            updateEntry(httpExchange,response);
                        }
                        send200(httpExchange, response);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //send200(httpExchange, response);
        } else {
            if (method.equals("POST")) {
                addEntry(httpExchange);
            }

        }
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/mainPage.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("entries", entries);
        response = template.render(model);
        send200(httpExchange, response);
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
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

    private String getStringifiedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(formatter.format(date));
        return formatter.format(date);
    }

    private void redirectToMainPage(HttpExchange httpExchange, String response) {
        Headers responseHeaders = httpExchange.getResponseHeaders();
        responseHeaders.set("Location", "/guestbook");
        try {
            httpExchange.sendResponseHeaders(302, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send404(HttpExchange httpExchange) {
        try {
            String response = "404 (Not Found)\n";
            httpExchange.sendResponseHeaders(404, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send200(HttpExchange httpExchange, String response) {
        try {
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFormData(HttpExchange httpExchange) {
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

    private String getNthURLArgument(HttpExchange httpExchange, int nth) {
        return httpExchange.getRequestURI().toString().split("/")[nth];
    }

    private void updateEntry(HttpExchange httpExchange, String response) throws UnsupportedEncodingException {
        String formData = getFormData(httpExchange);
        String date = getStringifiedDate();
        Map<String, String> inputs = parseFormData(formData);
        if (!(inputs.get("message").equals("") || inputs.get("name").equals(""))) {
            Entry updatedEntry = entries.stream().filter(a -> a.getId() == id).collect(Collectors.toList()).get(0);
            updatedEntry.setMessage(inputs.get("message"));
            updatedEntry.setName(inputs.get("name"));
            updatedEntry.setDate(date + " (Updated)");
            //redirectToMainPage(httpExchange, response);
        }
    }

    private void deleteEntry(HttpExchange httpExchange, String response) {
        try {
            int id = Integer.parseInt(getNthURLArgument(httpExchange,3));
            if (entries.size() > 0) {
                entries.removeIf(obj -> obj.getId() == id);
                //redirectToMainPage(httpExchange, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEntry(HttpExchange httpExchange) throws UnsupportedEncodingException {
        String formData = getFormData(httpExchange);
        String date = getStringifiedDate();
        Map<String, String> inputs = parseFormData(formData);
        if (!(inputs.get("message").equals("") || inputs.get("name").equals(""))) {
            entries.add(new Entry(++id, inputs.get("message"), inputs.get("name"), date));
        }
    }
}
