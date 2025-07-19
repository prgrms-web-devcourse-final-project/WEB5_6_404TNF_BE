package com.grepp.teamnotfound.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "home/index";
    }

    @GetMapping("/social/login")
    public String socialLoginTest() {
        return "home/login";
    }

    @GetMapping("/user/login")
    public String userLoginTest() {
        return "home/index";
    }

    @GetMapping("/admin/login")
    public String adminLoginTest() {
        return "home/index";
    }

    @GetMapping("/error/login")
    public String errorLoginTest() {
        return "home/index";
    }

}
