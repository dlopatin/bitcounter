package com.dlopatin.filereader.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.dlopatin.bitcounter.model.State;

public class StateTest {

	@Test
	public void testIncrementReaders() {
		State state = new State();
		state.setReaders(5);
		state.incrementReaders();
		assertThat(state.getReaders(), is(6));
	}

	@Test
	public void testIncrementDoneReaders() {
		State state = new State();
		state.setDoneReaders(5);
		state.incrementDoneReaders();
		assertThat(state.getDoneReaders(), is(6));
	}

	@Test
	public void testIncrementResultReadReaders() {
		State state = new State();
		state.setResultReadReaders(5);
		state.incrementResultReadReaders();
		assertThat(state.getResultReadReaders(), is(6));
	}

}
