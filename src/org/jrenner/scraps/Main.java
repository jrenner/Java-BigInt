package org.jrenner.scraps;

public class Main {

	public static void main(String[] args) {
		BigInt.test();
		CharBigInt.test();
	}

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
