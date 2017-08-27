package com.revature.application.restControllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.github.api.UserOperations;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.revature.application.beans.Greeting;
import com.revature.application.beans.RequestEmployee;
import com.revature.application.beans.RequestStatus;
import com.revature.application.dao.EmployeeDao;
import com.revature.application.dao.beans.Employee;
import com.revature.application.dao.beans.forms.EmployeeForm;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RestController
@PropertySource(value="classpath:loginauth.properties", ignoreResourceNotFound=true)
@RequestMapping("/authentication")
public class LoginController {
    
    @Autowired
    EmployeeDao employeeDAO;
    
//    //githubId
//	@Value("${github.client_id}")
//	private String githubId;
//    //github secret
//	@Value("${github.client_secret}")
//	private String githubSecret;
    @Autowired
    Environment env;
    
    
    /*
     * Main handler for logging in a user
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public RequestStatus login(String username, String password, HttpSession session) {
        
        // Search in the database
        Employee user = employeeDAO.read(username);
        if (user == null)
            return new RequestStatus(false, "User does not exist");
        
        if (user.getPassword().equals(password)) {
            // Start a session
            // session.setAttribute("id", ""+user.getEmployeeId());
            saveEmployee(session, user);
            
            // Return Success
            return new RequestStatus();
        } else {
            // Return Failure
            return new RequestStatus(false, "Incorrect password");
        }
    }
    
    /**
     * Github method for logging in
     * has an external http post with in it and github objects
     * 
     * if you would like to change the url path let me know, i have to change or remove the callback url online
     * 
     * ...for this to work we need to map this completely
     * an example of the url we get is:
     * http://localhost:8080/login/github?code=32ffc9fd1c0643ae7fa7
     * 
     * but i get whitelabel error pages when 
     * 
     * 
     * @throws IOException 
     */
    @RequestMapping("/login/github")
    public RequestStatus loginThruGithub(HttpServletRequest req, HttpSession session) throws IOException {

    	System.out.println("In github login method");
		String code = req.getParameter("code");

		String neededURL= "https://github.com/login/oauth/access_token";
		String clientId = env.getProperty("github.client_id");
		String clientSecret = env.getProperty("github.client_secret");
		String redirectURL = "http://localhost:8080/login/github";

		HttpUrl.Builder urlBuilder = HttpUrl.parse(neededURL).newBuilder();
		urlBuilder.addQueryParameter("client_id",clientId);
		urlBuilder.addQueryParameter("client_secret",clientSecret);
		urlBuilder.addQueryParameter("code",code);
		urlBuilder.addQueryParameter("redirect_uri",redirectURL);

		System.out.println(clientId+", "+clientSecret);
		
		String fullUrl = urlBuilder.build().toString();

		Request postRequest = new Request.Builder().url(fullUrl).build();

		OkHttpClient client = new OkHttpClient();
		Response response = client.newCall(postRequest).execute();
    	
		String[] seperatedResponse = seperateResponse(response.body().string());
		
		GitHub github = new GitHubTemplate(seperatedResponse[0]);

		UserOperations userOP = github.userOperations();
		GitHubUserProfile profile = userOP.getUserProfile();	
	
		System.out.println("Username: "+profile.getUsername());
		System.out.println("Name: "+profile.getName());
		System.out.println("Email: "+profile.getEmail());
		System.out.println("Location: "+profile.getLocation());
		
		return new RequestStatus();
//		return "made it thru github";
    }
    
    public String[] seperateResponse(String full) {
    	String[] strings = new String[3];
    	
		String[] split = full.split("&");

    	String[] splitToken = split[0].split("=");
		String[] splitScope = split[1].split("=");
		String[] splitType = split[2].split("=");

		strings[0] = splitToken[1];
		strings[1] = splitScope[1];
		strings[2] = splitType[1];
   	
    	return strings;
    }
    
    /*
     * Main handler for logging out a user
     */
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public RequestStatus logout(HttpSession session) {
        // Destroy session
        session.invalidate();
        
        // Return success
        return new RequestStatus();
    }
    
    private void saveEmployee(HttpSession session, Employee e) {
        session.setAttribute("id", "" + e.getEmployeeId());
    }
    
    private Employee loadEmployee(HttpSession session) {
        String id_str = (String) session.getAttribute("id");
        if (id_str == null)
            return null;
        long employee_id;
        try {
            employee_id = Long.parseLong(id_str);
        } catch (Exception e) {
            return null;
        }
        Employee employee = employeeDAO.read(employee_id);
        return employee;
    }
    
    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public RequestEmployee getCurrentUser(HttpSession session) {
        Employee employee = loadEmployee(session);
        if (employee == null)
            return new RequestEmployee(false, null);
        return new RequestEmployee(true, employee);
    }
    
    
   /*
    * Change method (NEEDS to validate input)
    * 
    @RequestMapping(path = "/employees/{userId}", method = RequestMethod.PUT)
    public RequestStatus updateProfile(@Valid EmployeeForm employeeForm, BindingResult bindingResult, 
                                       @PathVariable userId, HttpSession session) {
        Employee employee = loadEmployee(session);
        if (employee == null || employee.getEmployeeId() != userId) {
            return new RequestStatus(false, "Not logged in");
        }
            
        if (!bindingResult.hasErrors()) {
            employeeDAO.update(userId, employeeForm);
            return new Request();
        }
        
        return new RequestStatus(false, "Failed to update employee");
    }
    */
    
    @RequestMapping(path = "/update-profile", method = RequestMethod.POST)
    public RequestStatus updateProfile(String username, String email, String fname, String lname,
            HttpSession session) {
        Employee employee = loadEmployee(session);
        if (employee == null)
            return new RequestStatus(false, "Not logged in");
        
        long locId = employee.getLocation() == null ? 0 : employee.getLocation().getLocationId();
        long compId = employee.getCompany() == null ? 0 : employee.getCompany().getCompanyId();
        
        EmployeeForm employeeForm = new EmployeeForm(locId, compId, username,
                employee.getPassword(), email, fname, lname);
        
        employeeDAO.update(employee.getEmployeeId(), employeeForm);
        return new RequestStatus();
    }
}
