package com.dlopatin.filereader.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dlopatin.bitcounter.service.FileBitCounter;

public class FileBitCounterTest {

	private static final Logger log = LoggerFactory.getLogger(FileBitCounterTest.class);

	private static final String FILE = "test.dat";
	private static final long FILE_SIZE = 0x8000000; // 128 Mb
	private static final long LONG_PATTERN = 0x18AB49A2129L;

	private FileBitCounter bitCounter;

	@Before
	public void before() throws IOException {
		bitCounter = new FileBitCounter(Paths.get(FILE));
	}

	@After
	public void after() throws IOException {
		Files.deleteIfExists(Paths.get(FILE));
		Files.deleteIfExists(Paths.get(FILE + ".json"));
	}

	@Test
	public void testCount() throws IOException, InterruptedException {
		writeFileWithPattern(LONG_PATTERN);
		assertThat(bitCounter.count(), is(788529152L));
	}

	@Test
	public void testCount_noZeros() throws IOException, InterruptedException {
		writeFileWithPattern(-1);
		assertThat(bitCounter.count(), is(0L));
	}

	@Test
	public void testCount_allZeros() throws IOException, InterruptedException {
		writeFileWithPattern(0);
		assertThat(bitCounter.count(), is(FILE_SIZE * 8));
	}

	@Test
	public void testDeleteStateFile() throws IOException, InterruptedException {
		writeFileWithPattern(LONG_PATTERN);
		bitCounter.deleteStateFile();
		assertThat(bitCounter.deleteStateFile(), is(true));
	}

	private void writeFileWithPattern(long pattern) throws IOException {
		try (FileChannel channel =
				FileChannel.open(Paths.get(FILE), StandardOpenOption.WRITE, StandardOpenOption.READ,
						StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			MappedByteBuffer out = channel.map(FileChannel.MapMode.READ_WRITE, 0, FILE_SIZE);
			long zerosInLong = Long.SIZE - Long.bitCount(pattern);
			log.debug("In '{}' there are {} zeros", pattern, zerosInLong);
			long amount = FILE_SIZE / 8;
			for (int i = 0; i < amount; i++) {
				out.putLong(pattern);
			}
			out.force();
			log.debug("Total zeros: {}", amount * zerosInLong);
		}
	}

}
