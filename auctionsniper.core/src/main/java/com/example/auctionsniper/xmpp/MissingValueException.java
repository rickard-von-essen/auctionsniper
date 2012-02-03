package com.example.auctionsniper.xmpp;

public class MissingValueException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MissingValueException(final String name) {
		super("Missing value for field: " + name);
	}
}
