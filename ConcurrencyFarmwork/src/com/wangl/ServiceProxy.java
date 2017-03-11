package com.wangl;

public class ServiceProxy implements Service {

	private Service _service;
	private ActiveObject _active_object;
	
	public ServiceProxy(){
		_service = new ServiceImp();
		_active_object = new ActiveObject();
	}
	
	/**
	 * �����󷽷�ͨ���ӿڷ�װ����,�ڻ�����д�ż̳��ڽӿڵĶ���,�������ʵ�ֵĽӿڷ���,���ø÷�����ִ��������
	 */
	@Override
	public void sayHello() {
		MethodRequest mr = new SayHello(_service);
		
		_active_object.enqueue(mr);

	}

}
