package com.wang.AopProferm;

import org.springframework.stereotype.Component;

@Component
public class PersonService {

	public void addPerson(String personName) {
		System.out.println("add person " + personName);
	}
	
	public boolean deletePerson(String personName) {
		System.out.println("delete person " + personName) ;
		return true;
	}
	
	public void editPerson(String personName) {
		System.out.println("edit person " + personName);
		throw new RuntimeException("edit person throw exception");
	}
	
}