package com.wangl;

public class SayHello implements MethodRequest {
	private Service _service;
	
	public SayHello(Service s){
		_service = s;
	}
	
	@Override
	public void call() {
		_service.sayHello();
	}

}
