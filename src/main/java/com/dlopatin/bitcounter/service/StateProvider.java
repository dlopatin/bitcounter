package com.dlopatin.bitcounter.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dlopatin.bitcounter.model.State;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StateProvider {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Path filePath;
	private final ObjectMapper mapper = new ObjectMapper();

	public StateProvider(String filePath) {
		this.filePath = Paths.get(filePath);
	}

	public int registerReader() throws JsonGenerationException, JsonMappingException, IOException {
		try (FileChannel channel = openRWChannel()) {
			channel.lock();
			State state = loadState(channel);
			state.incrementReaders();
			channel.truncate(0);
			channel.write(ByteBuffer.wrap(mapper.writeValueAsBytes(state)));
			log.debug("Reader registered {}", mapper.writeValueAsString(state));
			return state.getReaders();
		}
	}

	public State markReaderAsDone() throws JsonGenerationException, JsonMappingException, IOException {
		try (FileChannel channel = openRWChannel()) {
			channel.lock();
			State state = loadState(channel);
			state.incrementDoneReaders();
			channel.truncate(0);
			channel.write(ByteBuffer.wrap(mapper.writeValueAsBytes(state)));
			log.debug("Reader done {}", mapper.writeValueAsString(state));
			return state;
		}
	}

	public State markResultRead() throws JsonGenerationException, JsonMappingException, IOException {
		try (FileChannel channel = openRWChannel()) {
			channel.lock();
			State state = loadState(channel);
			state.incrementResultReadReaders();
			channel.truncate(0);
			channel.write(ByteBuffer.wrap(mapper.writeValueAsBytes(state)));
			log.debug("Reader read result {}", mapper.writeValueAsString(state));
			return state;
		}
	}

	public State getState() throws IOException {
		try (FileChannel channel = openRWChannel()) {
			channel.lock();
			return loadState(channel);
		}
	}

	public State getStateFromDisk() throws IOException {
		try (FileChannel channel = openRChannel()) {
			return readStateFromDisk(channel);
		}
	}

	public long nextPosition(long length) throws JsonParseException, JsonMappingException, IOException {
		try (FileChannel channel = openRWChannel()) {
			channel.lock();
			State state = loadState(channel);
			long position = state.getPosition();
			state.setPosition(position + length);
			channel.truncate(0);
			channel.write(ByteBuffer.wrap(mapper.writeValueAsBytes(state)));
			return position;
		}
	}

	public long updateResult(long amountToAdd) throws JsonGenerationException, JsonMappingException, IOException {
		try (FileChannel channel = openRWChannel()) {
			channel.lock();
			State state = loadState(channel);
			state.setResult(state.getResult() + amountToAdd);
			channel.truncate(0);
			channel.write(ByteBuffer.wrap(mapper.writeValueAsBytes(state)));
			return state.getResult();
		}
	}

	public boolean removeFile() throws IOException {
		return Files.deleteIfExists(filePath);
	}

	private FileChannel openRWChannel() throws IOException {
		return FileChannel.open(filePath,
				StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
	}

	private FileChannel openRChannel() throws IOException {
		return FileChannel.open(filePath, StandardOpenOption.READ);
	}

	private State loadState(FileChannel channel) throws IOException, JsonParseException, JsonMappingException {
		State state = new State();
		if (channel.size() > 0) {
			state = readStateFromDisk(channel);
		}
		return state;
	}

	private State readStateFromDisk(FileChannel channel) throws IOException, JsonParseException, JsonMappingException {
		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[(int) channel.size()]);
		channel.read(byteBuffer);
		return mapper.readValue(byteBuffer.array(), State.class);
	}
}
