package org.jrenner.scraps;

public class BitMath {

	public static int add(int x, int y) {
		//System.out.printf("BitMath add: %d + %d\n", x, y);
		int sum, carry;

		sum = x ^ y;
		carry = x & y;
		while (carry != 0) {
			carry = carry << 1;
			x = sum;
			y = carry;
			sum = x ^ y;
			carry = x & y;
		}

		//System.out.printf("sum: %d\n", sum);
		return sum;
	}

	public static int mult(int x, int y) {
		System.out.printf("BitMath mult: %d * %d\n", x, y);
		int product = 0;
		for (int i = 0; i < y; i++) {
			product = add(product, x);
		}
		System.out.printf("product: %d\n", product);
		return product;
	}
}
