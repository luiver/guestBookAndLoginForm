package com.codecool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TempDatabase {
    private final List<User> allUsers;
    private final Map<String, User> loggedUsers;

    public TempDatabase() {
        allUsers = new ArrayList<>();
        allUsers.add(new User("xxx", "xxx"));
        allUsers.add(new User("1", "1"));
        allUsers.add(new User("rafcio", "qwe"));
        allUsers.add(new User("2", "2"));
        loggedUsers = new HashMap<>();
    }

    public List<User> getAllUsers() {
        return allUsers;
    }

    public Map<String, User> getLoggedUsers() {
        return loggedUsers;
    }

}
