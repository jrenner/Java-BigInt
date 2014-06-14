package org.jrenner.scraps;

import java.util.Random;

/**
 * A BigInt implementation backed by an array of bytes.
 * This implementation is tricker to implement that the character array backed implementation
 * But it would provide for better performance
 * TODO: This implementation does not actually support values larger than long as of yet
 * which is pretty important for BigIntegers!
 */
public class BigInt {
	private int[] bytes;
	private static final boolean VERBOSE = true;

	private BigInt() {}

	public BigInt(int byteSize) {
		this.bytes = new int[byteSize];
	}

	public int getByteSize() {
		return bytes.length;
	}

	public int getBit(int n) {
		int byteIdx = n / 8;
		int idx = n % 8;
		int b = bytes[byteIdx];
		if ((b & (1 << idx)) != 0) {
			return 1;
		}
		return 0;
	}

	public int getBitSafe(int n) {
		int byteIdx = n / 8;
		if (byteIdx >= bytes.length) {
			return 0;
		} else {
			return getBit(n);
		}
	}

	public void setBit(int i, int n) {
		if (n < 0 || n > 1) throw new IllegalArgumentException("need zero or one for setting bit");
		int byteIdx = i / 8;
		int bitIdx = i % 8;
		int bit = n << bitIdx;
		if (n == 1) {
			bytes[byteIdx] |= bit;
		} else {
			bytes[byteIdx] &= ~bit;
		}
		//System.out.printf("set bit [%d]: %d\n", i, getBit(i));
	}

	public static BigInt fromValue(long value) {
		final int LONG_SIZE = 8;
		BigInt big = new BigInt(LONG_SIZE);
		for (int i = 0; i < LONG_SIZE; i++) {
			int b = (int) (value >> (i * 8)) & 0xFF;
			big.bytes[i] = b;
		}
		return big;
	}

	public static void test() {
		BigInt one = new BigInt(4); // 4 = int, 8 = long
		one.bytes[0] = 0b01111111;
		//one.bytes[1] = 0b00000001;
		one.bytes[2] = 0b10000000;

		System.out.println("one ----------------");
		System.out.println(one);
		//big.printBytes();
		BigInt two = new BigInt(2);
		two.bytes[0] = 0b00000011;
		System.out.println("two ----------------");
		System.out.println(two);
		//two.printBytes();
		System.out.println("==== addition ====");
		BigInt result = add(one, two);
		System.out.println(" -=-=-=- FINAL RESULT -=-=-=");
		//result.printBits();
		System.out.println(result);
		result.printBytes();

		//additionTests();
		multiplyTests();
	}

	public static BigInt add(BigInt x, BigInt y) {
		// TODO reduce initial allocated byte size or implement dynamic resizing
		BigInt big = new BigInt(x.getByteSize() + y.getByteSize());
		boolean[] carries = new boolean[big.getByteSize() * 8]; // think of hand-written multiplication, true represent a carried binary value
		for (int i = 0; i < big.getByteSize() * 8; i++) {
			int xBit = x.getBitSafe(i);
			int yBit = y.getBitSafe(i);
			//System.out.printf("[%2d - %8d]: x: %8d, y: %8d\n", i, (long)Math.pow(2, i), xBit, yBit);
			int sum = addBits(xBit, yBit, i, carries);
			if (carries[i]) {
				int carried = 0b01; // in binary addition the only possible carry value is 1
				sum = addBits(sum, carried, i, carries);
			}
			//System.out.printf("sum (%d): %d\n", i, sum);
			big.setBit(i, sum);
		}
		// cut the fat
		int i = big.getByteSize() - 1;
		while (big.bytes[i] == 0 && i > 0) {
			i--;
		}
		//System.out.printf("non-zero / total: %d / %d\n", i + 1, big.getByteSize());
		big.resize(i);
		return big;
	}

	public static BigInt mul(BigInt x, BigInt y) {
		BigInt big = new BigInt(1);
		for (int i = 0; i < y.toLong(); i++) {
			big = add(big, x);
		}
		return big;
	}

	private static int addBits(int x, int y, int i, boolean[] carries) {
		//System.out.printf("add bits: %d + %d\n", x, y);
		if ((x & y) != 0) {
			int carryIdx = i+1;
			while (carries[carryIdx]) {
				carryIdx++;
				if (carryIdx >= carries.length)
					throw new RuntimeException("BigInt addition overflow, carryIdx: " + carryIdx);
			}
			//System.out.println("\t ---- carry: " + carryIdx);
			carries[carryIdx] = true;
		}
		return x ^ y;
	}

	public long toLong() {
		if (getByteSize() > 8)
			throw new RuntimeException("biginteger cannot be larger than long, not yet implemented larger values");
		long n = 0;
		int i = 0;
		for (int b : bytes) {
			long val = b;
			val <<= (i * 8);
			n += val;
			i++;
		}
		return n;
	}

	public void printBytes() {
		int i = 0;
		for (int b : bytes) {
			long val = b;
			val <<= (i * 8);
			System.out.printf("[%d]: %8s  -  %16d\n", i++, Integer.toBinaryString(b), val);
		}
	}

	public void printBits() {
		for (int i = 0; i < getByteSize() * 8; i++) {
			System.out.printf("[%2d - %8d]: %8d\n", i, (long)Math.pow(2, i), getBit(i));
		}
	}

	public static void printByte(int b, int shift) {
		long val = b;
		val <<= (shift * 8);
		System.out.printf("byte: %s - %d\n", Integer.toBinaryString(b), val);
	}

	private static void printBoolArray(boolean[] bools) {
		for (int i = 0; i < bools.length; i++) {
			System.out.printf("[%2d]: %s\n", i, bools[i]);
		}
	}

	@Override
	public String toString() {
		// TODO: support values larger than long
		return "BigInt: " + Long.toString(toLong());
	}

	public static void additionTests() {
		Random rand = new Random();
		for (long i = 0; i < 1000; i++) {
			if (i % 1000 == 0) System.out.println("test #" + i);
			long x = (long) rand.nextInt(100000);
			long y = (long) rand.nextInt(100000);
			assertAddition(x, y);
		}
		for (long x = 0; x < Integer.MAX_VALUE; x += 10000) {
			assertAddition(x, x);
		}
	}

	public static void multiplyTests() {
		for (int x = 0; x < 10000; x++) {
			for (int y = 0; y < 100; y++) {
				assertMultiplication(x, y);
			}
		}
	}

	private static void assertAddition(long x, long y) {
		BigInt one = BigInt.fromValue(x);
		BigInt two = BigInt.fromValue(y);
		long expected = x + y;
		long result = add(one, two).toLong();
		if (VERBOSE) System.out.printf("[%d + %d] expect: %d, result: %d\n", x, y, expected, result);
		if (expected != result) {
			Main.sleep(100);
			System.err.printf("expect: %d, result: %d\n", expected, result);
			throw new RuntimeException("addition test failed");
		}
	}

	private static void assertMultiplication(long x, long y) {
		BigInt one = BigInt.fromValue(x);
		BigInt two = BigInt.fromValue(y);
		long expected = x * y;
		long result = mul(one, two).toLong();
		if (VERBOSE) System.out.printf("[%d * %d] expect: %d, result: %d\n", x, y, expected, result);
		if (expected != result) {
			Main.sleep(100);
			System.err.printf("expect: %d, result: %d\n", expected, result);
			throw new RuntimeException("multiplication test failed");
		}
	}

	private void resize(int lastIndex) {
		int initialSize = getByteSize();
		int[] resized = new int[lastIndex + 1];
		System.arraycopy(bytes, 0, resized, 0, resized.length);
		bytes = resized;
		//if (VERBOSE) System.out.printf("resized from %d to %d\n", initialSize, getByteSize());
	}
}
