package com.cloudsuites.framework.webapp.rest;

import com.cloudsuites.framework.services.common.entities.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class UserController {

	@GetMapping("/hello")
	public User hello() {
		User user = new User();
		user.setFirstName("cheeks");
		user.setFirstName("peeps");
		user.setEmail("chmomar@gmail.com");
		user.setCreated(new Date());
		user.setUpdated(new Date());
		return user;
	}
}
 