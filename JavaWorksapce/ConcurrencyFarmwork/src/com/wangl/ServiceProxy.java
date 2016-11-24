package com.wangl;

public class ServiceProxy implements Service {

	private Service _service;
	private ActiveObject _active_object;
	
	public ServiceProxy(){
		_service = new ServiceImp();
		_active_object = new ActiveObject();
	}
	
	/**
	 * 将请求方法通过接口封装起来,在活动队列中存放继承于接口的对象,对象调用实现的接口方法,调用该方法就执行了请求
	 */
	@Override
	public void sayHello() {
		MethodRequest mr = new SayHello(_service);
		
		_active_object.enqueue(mr);

	}

}
