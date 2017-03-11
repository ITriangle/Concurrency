package com.wangl;

public class Client {
	private Service _service;
	
	public Client(Service s){
		_service = s;
	}
	
	public void requestService(){
		_service.sayHello();
	}
	
	public static void main(String[] args){
//		Service service = new ServiceImp();
		
		
		Service service = new ServiceProxy();
		Client client = new Client(service);
		
		client.requestService();
	}
}
