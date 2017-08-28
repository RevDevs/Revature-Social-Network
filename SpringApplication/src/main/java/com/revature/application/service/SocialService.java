package com.revature.application.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.github.api.UserOperations;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Service;

import com.revature.application.beans.RequestStatus;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@PropertySource(value="classpath:loginauth.properties", ignoreResourceNotFound=true)
@Service
public class SocialService {
	
  @Autowired
  Environment env;

  /*
   * Github
   * seperate methods, for possibility of Github auth update or Github API change/upgrade
   */
  	public GitHub fullGithub(String code) throws IOException {
  	  	String token = githubConnectionService(code);
    	
  		GitHub github = getGithub(token);
  		
  		return github;
  	}
  
	public String githubConnectionService(String code) throws IOException {
  	
		/*
		 * Get Access Token
		 */
    	System.out.println("code "+code);
    	
   		String neededURL= "https://github.com/login/oauth/access_token";
		String clientId = env.getProperty("github.client_id");
		String clientSecret = env.getProperty("github.client_secret");

		HttpUrl.Builder urlBuilder = HttpUrl.parse(neededURL).newBuilder();
		urlBuilder.addQueryParameter("client_id",clientId);
		urlBuilder.addQueryParameter("client_secret",clientSecret);
		urlBuilder.addQueryParameter("code",code);

		System.out.println(clientId+", "+clientSecret);
		
		String fullUrl = urlBuilder.build().toString();

		Request postRequest = new Request.Builder().url(fullUrl).build();

		OkHttpClient client = new OkHttpClient();
		Response response = client.newCall(postRequest).execute();
    	String responseBody = response.body().string();
    	System.out.println("response body: "+responseBody);
		String[] seperatedResponse = seperateResponseGithub(responseBody);
		
		String accessToken = seperatedResponse[0];
		
		System.out.println("access token: "+accessToken);
		
		return accessToken;
	}
    
    public String[] seperateResponseGithub(String full) {
    	String[] strings = new String[3];
    	
		String[] split = full.split("&");

    	String[] splitToken = split[0].split("=");
		String[] splitType = split[2].split("=");

		strings[0] = splitToken[1];
		strings[1] = splitType[1];
    	return strings;
    }
    
    public GitHub getGithub(String token) {
    	
		GitHub github = new GitHubTemplate(token);

		UserOperations userOP = github.userOperations();
		GitHubUserProfile profile = userOP.getUserProfile();	
	
		System.out.println("Username: "+profile.getUsername());
		System.out.println("Name: "+profile.getName());
		System.out.println("Email: "+profile.getEmail());
		System.out.println("Location: "+profile.getLocation());
		
		return github;    	
    }

}
