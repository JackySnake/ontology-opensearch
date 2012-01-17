package com.erdas.projects.epf.proxy.os.exceptions;

/**
 * @author fabian.skivee@erdas.com
 */
public class ModelWriterException extends Exception {

    protected Throwable throwable;

	public ModelWriterException(String message) {
		super(message);
	}

	public ModelWriterException(String message, Throwable throwable) {
		super(message);
		this.throwable = throwable;
	}

	public Throwable getCause() {
		return throwable;
	}

}
