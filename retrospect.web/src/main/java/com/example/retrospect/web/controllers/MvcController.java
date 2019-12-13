package com.example.retrospect.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MvcController {
    @GetMapping("/")
    public String home(){
        return "react";
    }
}
