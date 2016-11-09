package com.wang;

import org.junit.Test;

public class SpringTest {

	@Test
	public void testSpringWang() {
		SpringWang1 s1 = SpringWang1.getSpringWang();
		SpringWang1 s2 = SpringWang1.getSpringWang();

		if (s1 == s2) {
			System.out.println("s1与s2相同");

		} else {
			System.out.println("s1与s2不同");
		}
	}

	@Test
	public void testSpringWang2() {
		SpringWang2 t1 = SpringWang2.getSpringWang2();
		SpringWang2 t2 = SpringWang2.getSpringWang2();

		if (t1 == t2) {
			System.out.println("t1与t2相同");

		} else {
			System.out.println("t1与t2不同");
		}
	}

	/*
	 * public static void main(String[] args) { SpringWang s1 =
	 * SpringWang.getSpringWang(); SpringWang s2 = SpringWang.getSpringWang();
	 * 
	 * if(s1 == s2){ System.out.println("s1与s2相同");
	 * 
	 * } else { System.out.println("s1与s2不同"); }
	 * 
	 * 
	 * SpringWang2 t1 = SpringWang2.getSpringWang2(); SpringWang2 t2 =
	 * SpringWang2.getSpringWang2();
	 * 
	 * if(t1 == t2){ System.out.println("t1与t2相同");
	 * 
	 * } else { System.out.println("t1与t2不同"); }
	 * 
	 * }
	 */

}
