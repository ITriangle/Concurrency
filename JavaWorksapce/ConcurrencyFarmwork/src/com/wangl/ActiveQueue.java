package com.wangl;

import java.util.Stack;

public class ActiveQueue {
	private static final int QUEUE_SIZE = 20;
	private Stack<MethodRequest> _queue;
	
	public ActiveQueue(){
		_queue = new Stack<MethodRequest>();
	}
	
	public synchronized void enqueue(MethodRequest mr){
		while(_queue.size() > QUEUE_SIZE){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		_queue.push(mr);
		notifyAll();
		System.out.println("Leave Queue");
	}


	public synchronized MethodRequest dequeue(){
		MethodRequest mr;
		
		while(_queue.empty()){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		mr = (MethodRequest)_queue.pop();
		notifyAll();
		
		return mr;
	}

}
