package com.example.auctionsniper.xmpp;

import static java.lang.String.format;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jmock.example.announcer.Announcer;

import com.example.auctionsniper.Auction;
import com.example.auctionsniper.AuctionEventListener;

public class XMPPAuction implements Auction {

	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";

	private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

	private final Chat chat;
	final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);

	public XMPPAuction(final XMPPConnection connection, final String itemId) {
		chat = connection.getChatManager().createChat(auctionId(itemId, connection),
				new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce()));
	}

	private static String auctionId(final String itemId, final XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
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

	@Override
	public void addAuctionEventListener(final AuctionEventListener auctionSniper) {
		auctionEventListeners.addListener(auctionSniper);
	}
}