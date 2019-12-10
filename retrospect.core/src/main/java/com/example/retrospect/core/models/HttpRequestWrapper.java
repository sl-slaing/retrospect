package com.example.retrospect.core.models;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class HttpRequestWrapper {
    private final HttpServletRequest request;

    public HttpRequestWrapper(HttpServletRequest request) {
        this.request = request;
    }

    public String getCookie(String cookieName) {
        if (request.getCookies() == null){
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}

