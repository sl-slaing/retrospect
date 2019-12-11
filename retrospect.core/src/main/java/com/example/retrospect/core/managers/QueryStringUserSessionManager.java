package com.example.retrospect.core.managers;

import com.example.retrospect.core.exceptions.NotLoggedInException;
import com.example.retrospect.core.models.HttpRequestWrapper;
import com.example.retrospect.core.models.HttpResponseWrapper;
import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.User;
import com.example.retrospect.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.regex.Pattern;

@Service
@Primary
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class QueryStringUserSessionManager implements UserSessionManager {
    public static final String QUERY_STRING_NAME = "user";

    private final UserRepository userRepository;
    private final HttpRequestWrapper request;
    private final HttpResponseWrapper response;

    @Autowired
    public QueryStringUserSessionManager(UserRepository userRepository, HttpRequestWrapper request, HttpResponseWrapper response) {
        this.userRepository = userRepository;
        this.request = request;
        this.response = response;
    }

    @Override
    public void login(User user, String redirectUrl) {
        var queryStringDelimiter = redirectUrl.contains("?")
                ? "&"
                : "?";

        try {
            response.redirect(redirectUrl + queryStringDelimiter + QUERY_STRING_NAME + "=" + user.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logout() {
        var pattern = Pattern.compile("([?&]" + QUERY_STRING_NAME + "=.+)");
        var matcher = pattern.matcher(request.getUri());

        if (!matcher.matches()){
            return;
        }

        var newUri = matcher.replaceAll("");
        try {
            response.redirect(newUri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LoggedInUser getLoggedInUser() {
        var pattern = Pattern.compile("(?:&?" + QUERY_STRING_NAME + "=(.+))", Pattern.CASE_INSENSITIVE);
        var matcher = pattern.matcher(request.getQueryString());

        if (!matcher.matches()){
            throw new NotLoggedInException("Not logged in");
        }

        var username = matcher.group(1);
        if (username == null || username.equals("")){
            throw new NotLoggedInException("Not logged in.");
        }

        var user = userRepository.getUser(username);
        if (user == null){
            throw new NotLoggedInException("Logged in user doesn't exist");
        }

        return new LoggedInUser(username);
    }
}
