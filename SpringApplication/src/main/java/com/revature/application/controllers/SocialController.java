package com.revature.application.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.revature.application.service.SocialService;

@Controller
public class SocialController {
	
    @Autowired
    SocialService socialService;

	@RequestMapping("/social/github")
	public String homePage(HttpServletRequest req,HttpSession session) throws IOException {
    	String code = req.getParameter("code");
    
		GitHub github = socialService.fullGithub(code);	
		
		session.setAttribute("githubLoggedIn", true);
		session.setAttribute("githubUserOP", github.userOperations());
		
		System.out.println("-----Done------");
		
		/*
		 * It will redirect to the home which displays the user information
		 * but then redirects back to the login, i am guessing ..but
		 * bc i dont have the correct session attributes
		 */
//		return "index.html";
		return "redirect:/home";
	}
	
}
