package com.archive.archive.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BasicController {

    @GetMapping("/login")
    public String login(){
        return "login";
    }



    @RequestMapping("/default")
    public String showFirstPage(HttpServletRequest request){
        if (request.isUserInRole("USER")) {
            return "redirect:/user/";
        } else if (request.isUserInRole("ADMIN")) {
            return "redirect:/admin/";
        }
        return "redirect:/login";
    }
    
    
}
