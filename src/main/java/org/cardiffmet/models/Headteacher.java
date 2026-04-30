package org.cardiffmet.models;

public class Headteacher extends User {

    public Headteacher(String userId, String name, String email, String password) {
        super(userId, name, email, password);
    }

    @Override
    public String getRole() { return "HEADTEACHER"; }
}
