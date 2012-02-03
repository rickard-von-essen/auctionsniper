package com.example.auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jmock.example.announcer.Announcer;

import com.example.auctionsniper.Auction;
import com.example.auctionsniper.AuctionEventListener;
import com.example.auctionsniper.Item;

public class XMPPAuction implements Auction {

	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";

	private static final String AUCTION_ID_FORMAT = XMPPAuction.ITEM_ID_AS_LOGIN + "@%s/"
			+ XMPPAuction.AUCTION_RESOURCE;

	private final Chat chat;
	final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);

	public XMPPAuction(final XMPPConnection connection, final Item itemId, final XMPPFailureReporter failureReporter) {
		final AuctionMessageTranslator translator = translatorFor(connection, failureReporter);
		chat = connection.getChatManager().createChat(XMPPAuction.auctionId(itemId, connection), translator);
		addAuctionEventListener(chatDisconnectorFor(translator));

	}

	private AuctionMessageTranslator translatorFor(final XMPPConnection connection,
			final XMPPFailureReporter failureReporter) {
		return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce(), failureReporter);
	}

	private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
		return new AuctionEventListener() {

			@Override
			public void auctionFailed() {
				chat.removeMessageListener(translator);
			}

			@Override
			public void currentPrice(final int currentPrice, final int increment, final PriceSource priceSource) {
			}

			@Override
			public void auctionClosed() {
			}
		};
	}

	private static String auctionId(final Item item, final XMPPConnection connection) {
		return String.format(XMPPAuction.AUCTION_ID_FORMAT, item.itemId, connection.getServiceName());
	}

	@Override
	public void bid(final int bid) {
		sendMessage(String.format(XMPPAuction.BID_COMMAND_FORMAT, bid));
	}

	@Override
	public void join() {
		sendMessage(XMPPAuction.JOIN_COMMAND_FORMAT);
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