package com.revature.application.restControllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.revature.application.beans.Greeting;
import com.revature.application.beans.RequestEmployee;
import com.revature.application.beans.RequestStatus;
import com.revature.application.dao.EmployeeDao;
import com.revature.application.dao.LocationDao;
import com.revature.application.dao.beans.Employee;
import com.revature.application.dao.beans.Location;
import com.revature.application.dao.beans.forms.EmployeeForm;
import com.revature.application.services.LoginOperations;

@RestController
@RequestMapping("/authentication")
public class LoginController {

	@Autowired
	EmployeeDao employeeDAO;

	@Autowired
	LoginOperations loginService;

	@Autowired
	LocationDao locationDAO;

	/*
	 * Main handler for logging in a user
	 */
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public ResponseEntity<RequestStatus> login(String username, String password) {

		// Search in the database
		loginService.login(username, password);

		if (loginService.isLoggedIn()) {
			return new ResponseEntity<>(new RequestStatus(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new RequestStatus(false, "Login was not successful"), HttpStatus.OK);
		}
	}

	/*
	 * Main handler for logging out a user
	 */
	@RequestMapping(path = "/logout", method = RequestMethod.GET)
	public RequestStatus logout() {
		loginService.logout();
		return new RequestStatus();
	}

	@RequestMapping(path = "/user", method = RequestMethod.GET)
	public RequestEmployee getCurrentUser() {
		if (loginService.isLoggedIn()) {
			return new RequestEmployee(true, employeeDAO.read(loginService.getEmployeeId()));
		}
		return new RequestEmployee(false, null);
	}

	@RequestMapping(path = "/update-profile", method = RequestMethod.POST)
	public RequestStatus updateProfile(String username, String email, String fname, String lname) {
		if (!loginService.isLoggedIn())
			return new RequestStatus(false, "Not logged in");
		Employee employee = employeeDAO.read(loginService.getEmployeeId());

		long locId = employee.getLocation() == null ? 0 : employee.getLocation().getLocationId();
		long compId = employee.getCompany() == null ? 0 : employee.getCompany().getCompanyId();

		EmployeeForm employeeForm = new EmployeeForm(locId, compId, username, employee.getPassword(), email, fname,
				lname);

		employeeDAO.update(employee.getEmployeeId(), employeeForm);
		return new RequestStatus();
	}

	@RequestMapping(path = "/set-location", method = RequestMethod.POST)
	public RequestStatus setLocation(String city, HttpSession session) {

		if (!loginService.isLoggedIn())
			return new RequestStatus(false, "Not logged in");

		Location loc = locationDAO.read(city);
		if (loc == null)
			return new RequestStatus(false, "location not found");

		employeeDAO.updateLocation(loginService.getEmployeeId(), loc);

		return new RequestStatus();
	}
}
