package org.cardiffmet.models;

public abstract class User {
    protected String userId;
    protected String name;
    protected String email;
    protected String password;

    public User(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public abstract String getRole();

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
