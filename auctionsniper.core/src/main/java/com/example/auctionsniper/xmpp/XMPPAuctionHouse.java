package com.example.auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.example.auctionsniper.Auction;
import com.example.auctionsniper.AuctionHouse;

public class XMPPAuctionHouse implements AuctionHouse {

	public static final String AUCTION_RESOURCE = "Auction";

	private final XMPPConnection connection;

	public XMPPAuctionHouse(final String hostname, final String username, final String password) throws XMPPException {
		connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
	}

	@Override
	public Auction auctionFor(final String itemId) {
		return new XMPPAuction(connection, itemId);
	}

	public static XMPPAuctionHouse connect(final String hostname, final String username, final String password)
			throws XMPPException {
		return new XMPPAuctionHouse(hostname, username, password);
	}

	@Override
	public void disconnect() {
		connection.disconnect();
	}
}