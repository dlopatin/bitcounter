package com.dlopatin.bitcounter.service;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.dlopatin.bitcounter.model.State;

public class FileBitCounter {

	private static final int TIMEOUT_MS = 100;
	private static final long CHUNK_SIZE = (long) Math.pow(1024, 2) * 10; // 10 MB

	private final Path path;
	private final StateProvider stateProvider;

	public FileBitCounter(Path path) {
		this.path = path;
		stateProvider = new StateProvider(path.toString() + ".json");
	}

	public long count() throws IOException, InterruptedException {
		stateProvider.registerReader();
		try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
			long nextPosition = stateProvider.nextPosition(CHUNK_SIZE);
			while (nextPosition <= channel.size()) {
				long sizeToMap = CHUNK_SIZE;
				if (nextPosition + CHUNK_SIZE > channel.size()) {
					sizeToMap = channel.size() - nextPosition;
				}
				MappedByteBuffer mmap = channel.map(MapMode.READ_ONLY, nextPosition, sizeToMap);
				long cnt = 0;
				while (mmap.hasRemaining()) {
					if (mmap.remaining() % 8 == 0) {
						cnt += BitCounter.countZeros(mmap.getLong());
					} else {
						cnt += BitCounter.countZeros(mmap.get());
					}
				}
				stateProvider.updateResult(cnt);
				nextPosition = stateProvider.nextPosition(CHUNK_SIZE);
			}
		}
		State state = stateProvider.markReaderAsDone();
		while (state.getDoneReaders() != state.getReaders()) {
			Thread.sleep(100);
			state = stateProvider.getState();
		}
		state = stateProvider.markResultRead();
		return state.getResult();
	}

	public boolean deleteStateFile() throws InterruptedException {
		try {
			State state = stateProvider.getStateFromDisk();
			while (state.getResultReadReaders() != state.getReaders()) {
				Thread.sleep(TIMEOUT_MS);
				state = stateProvider.getStateFromDisk();
			}
			return stateProvider.removeFile();
		} catch (IOException ex) {
			// ignore, file is deleted
		}
		return true;
	}

}
