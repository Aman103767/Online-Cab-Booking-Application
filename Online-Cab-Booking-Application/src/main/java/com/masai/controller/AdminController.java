package com.masai.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.masai.exceptions.AdminException;
import com.masai.exceptions.CustomerException;
import com.masai.exceptions.DriverException;
import com.masai.model.Admin;
import com.masai.model.Driver;
import com.masai.service.AdminService;
import com.masai.service.DriverService;

@RestController
public class AdminController {
	
	@Autowired
	private AdminService AService;
	
	
	@PostMapping("/admin")
	public ResponseEntity<Admin> saveAdmin(@Valid @RequestBody Admin admin) throws AdminException {
		
		Admin savedAdmin = AService.createAdmin(admin);
		
		
		return new ResponseEntity<Admin>(savedAdmin,HttpStatus.CREATED);
	}
	
	@PutMapping("/admin")
	public  ResponseEntity<Admin> updateAdmin(@Valid @RequestBody Admin admin,@RequestParam(required = false) String key ) throws AdminException {
		
		
		Admin updatedCustomer= AService.updateAdmin(admin, key);
				
		return new ResponseEntity<Admin>(updatedCustomer,HttpStatus.OK);
		
	}
	@GetMapping("/admin/{id}")
	public ResponseEntity<Admin> deleteAdmin(@PathVariable("id") Integer adminId) throws AdminException{
		
		Admin DeleteAdmin = AService.deleteAdmin(adminId);
		
		return new ResponseEntity<Admin>(DeleteAdmin,HttpStatus.OK);
	}
	
	

}
