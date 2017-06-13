package com.dlopatin.filereader.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dlopatin.bitcounter.model.State;
import com.dlopatin.bitcounter.service.StateProvider;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class StateProviderTest {

	private static final String STATE_SAVED = "{\"readers\":3,\"doneReaders\":1,\"resultReadReaders\":0,"
		+ "\"position\":1101004800,\"result\":6283591680}";
	private static final String FILE_NAME = "test";
	private StateProvider stateProvider;

	@Before
	public void before() {
		stateProvider = new StateProvider(FILE_NAME);
	}

	@After
	public void after() throws IOException {
		Files.deleteIfExists(Paths.get(FILE_NAME));
	}

	@Test
	public void testRegisterReader() throws JsonGenerationException, JsonMappingException, IOException {
		assertThat(stateProvider.registerReader(), is(1));
	}

	@Test
	public void testMarkReaderAsDone() throws JsonGenerationException, JsonMappingException, IOException {
		assertThat(stateProvider.markReaderAsDone().getDoneReaders(), is(1));
	}

	@Test
	public void testMarkResultRead() throws JsonGenerationException, JsonMappingException, IOException {
		assertThat(stateProvider.markResultRead().getResultReadReaders(), is(1));
	}

	@Test
	public void testGetState() throws JsonGenerationException, JsonMappingException, IOException {
		Files.write(Paths.get(FILE_NAME), STATE_SAVED.getBytes());

		State state = stateProvider.getState();
		assertSavedState(state);
	}

	@Test
	public void testGetStateFromDisk() throws JsonGenerationException, JsonMappingException, IOException {
		Files.write(Paths.get(FILE_NAME), STATE_SAVED.getBytes());
		State state = stateProvider.getStateFromDisk();
		assertSavedState(state);
	}

	@Test
	public void testNextPosition() throws JsonGenerationException, JsonMappingException, IOException {
		Files.write(Paths.get(FILE_NAME), STATE_SAVED.getBytes());

		assertThat(stateProvider.nextPosition(1111), is(1101004800L));
		String savedState = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
		assertThat(savedState, is("{\"readers\":3,\"doneReaders\":1,\"resultReadReaders\":0,"
			+ "\"position\":1101005911,\"result\":6283591680}"));
	}

	@Test
	public void testUpdateResult() throws JsonGenerationException, JsonMappingException, IOException {
		Files.write(Paths.get(FILE_NAME), STATE_SAVED.getBytes());

		assertThat(stateProvider.updateResult(1111), is(6283592791L));
		String savedState = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
		assertThat(savedState, is("{\"readers\":3,\"doneReaders\":1,\"resultReadReaders\":0,"
			+ "\"position\":1101004800,\"result\":6283592791}"));
	}

	@Test
	public void testRemoveFile() throws IOException {
		Files.write(Paths.get(FILE_NAME), STATE_SAVED.getBytes());

		assertThat(stateProvider.removeFile(), is(true));
		assertThat(Files.exists(Paths.get(FILE_NAME)), is(false));
	}

	private void assertSavedState(State state) {
		assertThat(state.getReaders(), is(3));
		assertThat(state.getDoneReaders(), is(1));
		assertThat(state.getResultReadReaders(), is(0));
		assertThat(state.getPosition(), is(1101004800L));
		assertThat(state.getResult(), is(6283591680L));
	}
}
