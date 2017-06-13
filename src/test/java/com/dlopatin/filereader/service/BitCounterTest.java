package com.dlopatin.filereader.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.dlopatin.bitcounter.service.BitCounter;

public class BitCounterTest {

	@Test
	public void testCountOnesInByte() {
		assertThat(BitCounter.countOnes((byte) 8), is(1));
		assertThat(BitCounter.countOnes((byte) 1), is(1));
		assertThat(BitCounter.countOnes((byte) 121), is(5));
		assertThat(BitCounter.countOnes((byte) 67), is(3));
		assertThat(BitCounter.countOnes((byte) -11), is(6));
		assertThat(BitCounter.countOnes((byte) -121), is(4));
		assertThat(BitCounter.countOnes((byte) -128), is(1));
	}

	@Test
	public void testCountZerosInByte() {
		assertThat(BitCounter.countZeros((byte) 8), is(7));
		assertThat(BitCounter.countZeros((byte) 1), is(7));
		assertThat(BitCounter.countZeros((byte) 121), is(3));
		assertThat(BitCounter.countZeros((byte) 67), is(5));
		assertThat(BitCounter.countZeros((byte) -11), is(2));
		assertThat(BitCounter.countZeros((byte) -121), is(4));
		assertThat(BitCounter.countZeros((byte) -128), is(7));
	}

	@Test
	public void testCountZerosInLong() {
		assertThat(BitCounter.countZeros(8L), is(63));
		assertThat(BitCounter.countZeros(1L), is(63));
		assertThat(BitCounter.countZeros(121L), is(59));
		assertThat(BitCounter.countZeros(67L), is(61));
		assertThat(BitCounter.countZeros(-11L), is(2));
		assertThat(BitCounter.countZeros(-121L), is(4));
		assertThat(BitCounter.countZeros(-128L), is(7));
	}

}
