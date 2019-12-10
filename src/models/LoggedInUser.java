package models;

public class LoggedInUser extends User {
    public LoggedInUser(String userName) {
        super(userName, "displayname", "emailAddress");
    }
}
