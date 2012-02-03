package com.example.auctionsniper.xmpp;

import static java.lang.String.format;

import java.util.logging.Logger;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter {

	private final Logger logger;

	public LoggingXMPPFailureReporter(final Logger logger) {
		this.logger = logger;
	}

	@Override
	public void cannotTranslateMessage(final String sniperId, final String message, final Exception exception) {
		logger.severe(format("<%s> Could not translate message \"%s\" because \"%s\"", sniperId, message,
				exception.toString()));
	}
}