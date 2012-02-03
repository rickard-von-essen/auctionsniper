package com.example.auctionsniper.xmpp;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.example.auctionsniper.Auction;
import com.example.auctionsniper.AuctionHouse;
import com.example.auctionsniper.Item;

public class XMPPAuctionHouse implements AuctionHouse {

	public static final String AUCTION_RESOURCE = "Auction";
	private static final String LOGGER_NAME = "XMPP Auction Logger";
	public static final String LOG_FILE_NAME = "auction.log";

	private final XMPPConnection connection;
	private final XMPPFailureReporter failureReporter;

	public XMPPAuctionHouse(final String hostname, final String username, final String password)
			throws XMPPAuctionException {
		connection = connectionFor(hostname, username, password);
		failureReporter = new LoggingXMPPFailureReporter(makeLogger());
	}

	private XMPPConnection connectionFor(final String hostname, final String username, final String password)
			throws XMPPAuctionException {
		try {
			final XMPPConnection connection = new XMPPConnection(hostname);
			connection.connect();
			connection.login(username, password, XMPPAuctionHouse.AUCTION_RESOURCE);
			return connection;
		} catch (final XMPPException exception) {
			throw new XMPPAuctionException(exception);
		}
	}

	private Logger makeLogger() throws XMPPAuctionException {
		final Logger logger = Logger.getLogger(XMPPAuctionHouse.LOGGER_NAME);
		logger.setUseParentHandlers(false);
		logger.addHandler(simpleFileHandler());
		return logger;
	}

	private Handler simpleFileHandler() throws XMPPAuctionException {
		try {
			final FileHandler handler = new FileHandler(XMPPAuctionHouse.LOG_FILE_NAME);
			handler.setFormatter(new SimpleFormatter());
			return handler;
		} catch (final Exception exception) {
			throw new XMPPAuctionException("Could not create logger FileHandler "
					+ getFullPath(XMPPAuctionHouse.LOG_FILE_NAME), exception);
		}
	}

	private String getFullPath(final String logFileName) {
		return new File(logFileName).getAbsolutePath();
	}

	@Override
	public Auction auctionFor(final Item item) {
		return new XMPPAuction(connection, item, failureReporter);
	}

	public static XMPPAuctionHouse connect(final String hostname, final String username, final String password)
			throws XMPPAuctionException {
		return new XMPPAuctionHouse(hostname, username, password);
	}

	@Override
	public void disconnect() {
		connection.disconnect();
	}
}