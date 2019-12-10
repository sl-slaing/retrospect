package models;

public class User implements Identifiable {
    public static final String TYPE_NAME = "USER";

    protected String userName;
    protected String displayName;
    protected String emailAddress;

    public User(String userName, String displayName, String emailAddress) {
        this.userName = userName;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
    }

    @Override
    public String getId() {
        return userName;
    }

    @Override
    public String getType() {
        return TYPE_NAME;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}

