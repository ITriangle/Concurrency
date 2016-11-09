package com.wang;

public class SpringWang2 {
	private SpringWang2 () {
		
	}
	
	private static SpringWang2 springWang2;
	
	public static SpringWang2 getSpringWang2(){
		if(springWang2 == null){
			springWang2 = new SpringWang2();
		}
		return springWang2;
	}
}
