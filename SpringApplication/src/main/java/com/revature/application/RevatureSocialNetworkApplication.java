package com.revature.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:appContext.xml")
public class RevatureSocialNetworkApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(RevatureSocialNetworkApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RevatureSocialNetworkApplication.class);
	}
}
