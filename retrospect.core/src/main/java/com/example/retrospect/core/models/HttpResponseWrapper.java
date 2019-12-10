package com.example.retrospect.core.models;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class HttpResponseWrapper {

    public void setSessionCookie(String cookieName, String value) {
    }

    public void removeSessionCookie(String cookieName) {

    }
}
