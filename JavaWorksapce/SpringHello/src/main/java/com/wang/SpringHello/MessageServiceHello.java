package com.wang.SpringHello;

import org.springframework.stereotype.Component;

//@Component
public class MessageServiceHello implements MessageService {

	public String getMessage() {
		return "Hello world";
	}

}
