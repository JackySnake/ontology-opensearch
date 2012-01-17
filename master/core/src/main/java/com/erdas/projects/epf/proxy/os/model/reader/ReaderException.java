package com.erdas.projects.epf.proxy.os.model.reader;

/**
 * Created by IntelliJ IDEA.
 * User: fskivee
 * Date: 23-août-2011
 * Time: 15:11:22
 * To change this template use File | Settings | File Templates.
 */
public class ReaderException extends Exception {

    protected Throwable throwable;

	public ReaderException(String message) {
		super(message);
	}

	public ReaderException(String message, Throwable throwable) {
		super(message);
		this.throwable = throwable;
	}

	public Throwable getCause() {
		return throwable;
	}
}
