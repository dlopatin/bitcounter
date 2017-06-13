package com.dlopatin.bitcounter.service;

public final class BitCounter {

	private BitCounter() {
		// utility
	}

	public static int countZeros(long l) {
		return Long.SIZE - Long.bitCount(l);
	}

	public static int countZeros(byte b) {
		return Byte.SIZE - countOnes(b);
	}

	public static int countOnes(byte b) {
		int val = b;
		val = (val - ((val >>> 1) & 0x55));
		val = ((val & 0x33) + ((val >>> 2) & 0x33));
		val = ((val + (val >>> 4)) & 0x0f);
		return val & 0xf;
	}

}
