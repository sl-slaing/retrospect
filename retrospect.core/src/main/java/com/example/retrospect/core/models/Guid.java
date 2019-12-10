package com.example.retrospect.core.models;

import java.util.UUID;

public class Guid {
    public static String next(){
        return UUID.randomUUID().toString();
    }
}
