package com.example.retrospect.core.managers;

import com.example.retrospect.core.exceptions.NotLoggedInException;
import com.example.retrospect.core.models.HttpRequestWrapper;
import com.example.retrospect.core.models.HttpResponseWrapper;
import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.example.retrospect.core.repositories.UserRepository;
import org.springframework.web.context.WebApplicationContext;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class CookieUserSessionManager implements UserSessionManager {
    public static final String COOKIE_NAME = "RETROSPECT-USER-ID";

    private final UserRepository userRepository;
    private final HttpRequestWrapper request;
    private final HttpResponseWrapper response;

    @Autowired
    public CookieUserSessionManager(UserRepository userRepository, HttpRequestWrapper request, HttpResponseWrapper response) {
        this.userRepository = userRepository;
        this.request = request;
        this.response = response;
    }

    @Override
    public void login(User user){
        response.setSessionCookie(COOKIE_NAME, user.getId());
    }

    @Override
    public void logout(){
        response.removeSessionCookie(COOKIE_NAME);
    }

    @Override
    public LoggedInUser getLoggedInUser(){
        var cookieValue = request.getCookie(COOKIE_NAME);
        if (cookieValue == null || cookieValue.equals("")){
            throw new NotLoggedInException("No user is logged in");
        }

        var user = userRepository.getUser(cookieValue);
        if (user == null){
            throw new NotLoggedInException("Logged in user doesn't exist");
        }

        return new LoggedInUser(user.getId());
    }
}
