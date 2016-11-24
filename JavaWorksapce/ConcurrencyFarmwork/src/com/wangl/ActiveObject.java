package com.wangl;

public class ActiveObject extends Thread{
	private ActiveQueue _queue;
	
	public ActiveObject(){
		_queue = new ActiveQueue();
		start();
	}
	
	public void enqueue(MethodRequest mr){
		_queue.enqueue(mr);
	}

	@Override
	public void run() {
		while(true){
			MethodRequest mr = _queue.dequeue();
			mr.call();
		}
	}
	
	
}
