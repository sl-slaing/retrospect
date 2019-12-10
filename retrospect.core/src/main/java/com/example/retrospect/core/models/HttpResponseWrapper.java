package com.example.retrospect.core.models;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class HttpResponseWrapper {

    private final HttpServletResponse response;

    public HttpResponseWrapper(HttpServletResponse response) {
        this.response = response;
    }

    public void setSessionCookie(String cookieName, String value) {
        var cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    public void removeSessionCookie(String cookieName) {
        var cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
