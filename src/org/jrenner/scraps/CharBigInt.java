package org.jrenner.scraps;

import com.sun.xml.internal.fastinfoset.util.CharArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharBigInt {
	public char[] chars;

	public CharBigInt(long val) {
		String s = String.valueOf(val);
		chars = s.toCharArray();
	}

	public CharBigInt(String val) {
		if (stringContainsNonDigits(val)) {
			throw new RuntimeException("invalid string");
		}
		String pattern = "[1-9][0-9]*";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(val);
		int matches = 0;
		while (m.find()) {
			matches++;
			System.out.println("match: " + m.group());
		}
		System.out.println("matches: " + matches);
		if (matches < 1) {
			throw new RuntimeException("pattern did not find group");
		}
		chars = val.toCharArray();
	}

	private void printChars() {
		printCharArray(chars);
	}

	private static void printCharArray(char[] ca) {
		int i = 0;
		System.out.println("char[] length: " + ca.length);
		for (char c : ca) {
			System.out.printf("[%d]: '%c'\n", i, c);
		}
	}

	private boolean stringContainsNonDigits(String s) {
		Pattern p = Pattern.compile("[^0-9]");
		Matcher m = p.matcher(s);
		if (m.find()) {
			System.out.printf("found non-digit in string:\n\tString: %s\n\tnon-digit: %s\n", s, m.group());
			Main.sleep(100);
			return true;
		}
		return false;
	}

	public void add(CharBigInt cbi) {

	}

	@Override
	public String toString() {
		return String.valueOf(chars);
	}

	public static void test() {
		Random rand = new Random();
		for (int i = 0; i < 100; i++) {
			int max = Integer.MAX_VALUE / 2;
			int x = rand.nextInt(max);
			int y = rand.nextInt(max);
			CharBigInt a = new CharBigInt(x);
			CharBigInt b = new CharBigInt(y);

			CharBigInt sum = addCharArrays(a.chars, b.chars);
			System.out.printf("%s + %s = %s\n", a, b, sum);
			CharBigInt expected = new CharBigInt(x + y);
			if (!expected.equals(sum)) {
				throw new RuntimeException("test failed:\nexpected: " + expected + "\nresult: " + sum);
			}
		}
		//testMatchingArrays();
	}

	private static CharBigInt addCharArrays(char[] a, char[] b) {
		StringBuilder result = new StringBuilder("");
		char[] large, small;
		if (a.length >= b.length) {
			large = a;
			small = b;
		} else {
			large = b;
			small = a;
		}
		small = createMatchingSizeCharArray(large, small);
		int hi = large.length;
		int i;
		int carry = 0;
		for (i = hi - 1; i >= 0; i--) {
			int x = Character.getNumericValue(small[i]);
			int y = Character.getNumericValue(large[i]);
			int sum = x + y;
			System.out.printf("%d + %d + (%d) = ", x, y, carry);
			if (carry > 0) {
				sum += carry;
				carry = 0;
			}
			while (sum >= 10) {
				carry += 1;
				sum -= 10;
			}
			System.out.printf("sum: %d, carry: %d\n", sum, carry);
			result.append(sum);
		}
		if (carry > 0) result.append(carry);
		return new CharBigInt(result.reverse().toString());
	}

	private static char[] createMatchingSizeCharArray(char[] large, char[] small) {
		char[] result = new char[large.length];
		// zero it out
		for (int i = 0; i < result.length; i++) {
			result[i] = '0';
		}
		for (int i = 1; i <= small.length; i++) {
			int smallIdx = small.length - i;
			int resultIdx = result.length - i;
			result[resultIdx] = small[smallIdx];
		}
		return result;
	}

	private static void testMatchingArrays() {
		String sa = "1234567890";
		String sb = "555";
		char[] matching = createMatchingSizeCharArray(sa.toCharArray(), sb.toCharArray());
		printCharArray(sa.toCharArray());
		printCharArray(matching);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharBigInt) {
			if (Arrays.equals(chars, ((CharBigInt) obj).chars))
				return true;
		}
		return false;
	}
}
