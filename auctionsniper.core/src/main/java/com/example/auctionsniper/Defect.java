package com.example.auctionsniper;

public class Defect extends RuntimeException {

	private static final long serialVersionUID = 5052832324284678209L;

	public Defect(final String message) {
		super(message);
	}
}
