package com.example.auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPException;

public class XMPPAuctionException extends Exception {

	private static final long serialVersionUID = 1L;

	public XMPPAuctionException(final String message, final Exception exception) {
		super(message, exception);
	}

	public XMPPAuctionException(final XMPPException exception) {
		super(exception);
	}
}