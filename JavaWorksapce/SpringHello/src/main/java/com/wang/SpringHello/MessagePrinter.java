package com.wang.SpringHello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//表名这是一个组件类,在自动扫描时,实例化bean,加入Spring上下文
//@Component
public class MessagePrinter {
	//依赖于MessageService
	final private MessageService  service;
	
	@Autowired
	public MessagePrinter(MessageService service){
		this.service = service;
	}
	
	public void printMessage() {
		System.out.println(this.service.getMessage());
	}


}