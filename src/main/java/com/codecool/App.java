package com.codecool;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class App 
{
    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        TempDatabase tempDatabase = new TempDatabase();
        server.createContext("/static", new Static());
        server.createContext("/guestbook", new GuestbookTwig());
        server.createContext("/logout", new Logout(tempDatabase));
        server.createContext("/login", new Login(tempDatabase));
        server.setExecutor(null);

        server.start();
    }
}
