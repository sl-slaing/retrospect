package managers;

import models.HttpRequestWrapper;
import models.HttpResponseWrapper;
import models.LoggedInUser;
import models.User;
import repositories.UserRepository;

public class UserSessionManager {
    public static final String COOKIE_NAME = "RETROSPECT-USER-ID";

    private final UserRepository userRepository;
    private final HttpRequestWrapper request;
    private final HttpResponseWrapper response;

    public UserSessionManager(UserRepository userRepository, HttpRequestWrapper request, HttpResponseWrapper response) {
        this.userRepository = userRepository;
        this.request = request;
        this.response = response;
    }

    public void login(User user){
        response.setSessionCookie(COOKIE_NAME, user.getId());
    }

    public void logout(){
        response.removeSessionCookie(COOKIE_NAME);
    }

    public LoggedInUser getLoggedInUser(){
        var cookieValue = request.getCookie(COOKIE_NAME);
        if (cookieValue == null || cookieValue.equals("")){
            throw new RuntimeException("No user is logged in");
        }

        var user = userRepository.getUser(cookieValue);
        if (user == null){
            throw new RuntimeException("Logged in user doesn't exist");
        }

        return new LoggedInUser(user.getId());
    }
}
