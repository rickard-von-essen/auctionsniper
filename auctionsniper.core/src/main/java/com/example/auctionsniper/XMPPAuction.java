package com.example.auctionsniper;

import static java.lang.String.format;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {

	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";

	private final Chat chat;

	XMPPAuction(final Chat chat) {
		this.chat = chat;
	}

	@Override
	public void bid(final int bid) {
		sendMessage(format(BID_COMMAND_FORMAT, bid));
	}

	@Override
	public void join() {
		sendMessage(JOIN_COMMAND_FORMAT);
	}

	private void sendMessage(final String message) {
		try {
			chat.sendMessage(message);
		} catch (final XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}