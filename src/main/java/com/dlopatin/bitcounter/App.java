package com.dlopatin.bitcounter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.dlopatin.bitcounter.service.FileBitCounter;

public class App {

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length == 0) {
			throw new IllegalArgumentException("File is not specified");
		}
		String userFile = args[0];
		Path path = Paths.get(userFile);
		if (!Files.exists(path)) {
			throw new IllegalArgumentException("File not exists");
		}
		FileBitCounter fileBitCounter = new FileBitCounter(path);
		System.out.println(fileBitCounter.count());
		fileBitCounter.deleteStateFile();
	}
}
