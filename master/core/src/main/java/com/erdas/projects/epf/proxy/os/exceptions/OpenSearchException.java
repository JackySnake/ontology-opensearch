package com.erdas.projects.epf.proxy.os.exceptions;

/**
 * @author fabian.skivee@erdas.com
 */
public class OpenSearchException extends Exception {
	protected Throwable throwable;

    public OpenSearchException(String message) {
		super(message);
	}

	public OpenSearchException(String message, Throwable throwable) {
		super(message);
		this.throwable = throwable;
	}

	public Throwable getCause() {
		return throwable;
	}

}

