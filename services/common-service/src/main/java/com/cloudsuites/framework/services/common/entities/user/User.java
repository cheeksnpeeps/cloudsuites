package com.cloudsuites.framework.services.common.entities.user;

import com.cloudsuites.framework.services.common.entities.Status;
import lombok.Data;

import java.util.Date;

@Data
public class User {

	private String uid;
	
	/**
	 * Property ID
	 */
	private int pid;

	/**
	 * Unit ID
	 */
	private int unid;
	
	private String firstName; 
	
	private String lastName;
	
	private Gender gender;
	
	private Status status;
	
	private Role role;
	
	private Date created;
	
	private Date updated;
	
	private String email;
	
	private String phone;
	
}
