package com.example.text2voice.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/hello")
public class HelloWorld {
    @GetMapping("/world")
    @ResponseBody
    public String hello(){
        return "Hello World!!!!!";
    }
}
