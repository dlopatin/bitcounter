package com.dlopatin.bitcounter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class State {

	private int readers;
	private int doneReaders;
	private int resultReadReaders;

	private long position;
	private long result;

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public long getResult() {
		return result;
	}

	public void setResult(long result) {
		this.result = result;
	}

	public int getReaders() {
		return readers;
	}

	public void setReaders(int readersCnt) {
		this.readers = readersCnt;
	}

	public int getDoneReaders() {
		return doneReaders;
	}

	public void setDoneReaders(int doneReaders) {
		this.doneReaders = doneReaders;
	}

	public int getResultReadReaders() {
		return resultReadReaders;
	}

	public void setResultReadReaders(int resultReadReaders) {
		this.resultReadReaders = resultReadReaders;
	}

	@JsonIgnore
	public void incrementReaders() {
		readers++;
	}

	@JsonIgnore
	public void incrementDoneReaders() {
		doneReaders++;
	}

	@JsonIgnore
	public void incrementResultReadReaders() {
		resultReadReaders++;
	}

}
