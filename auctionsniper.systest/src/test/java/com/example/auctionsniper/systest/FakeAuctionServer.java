package com.example.auctionsniper.systest;

import static com.example.auctionsniper.Main.AUCTION_RESOURCE;
import static com.example.auctionsniper.Main.ITEM_ID_AS_LOGIN;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.ArrayBlockingQueue;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.example.auctionsniper.XMPPAuction;

public class FakeAuctionServer {

	private static final String XMPP_HOSTNAME = "localhost";
	private static final String AUCTION_PASSWORD = "auction";

	private final String itemId;
	private final XMPPConnection connection;
	private Chat currentChat;
	private final SingleMessageListener messageListener = new SingleMessageListener();

	public FakeAuctionServer(final String itemId) {
		this.itemId = itemId;
		this.connection = new XMPPConnection(XMPP_HOSTNAME);
	}

	public void startSellingItem() throws XMPPException {
		connection.connect();
		connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
		connection.getChatManager().addChatListener(new ChatManagerListener() {

			@Override
			public void chatCreated(final Chat chat, final boolean createdLocally) {
				currentChat = chat;
				chat.addMessageListener(messageListener);
			}
		});
	}

	public void hasReceivedJoinRequestsFromSniper(final String sniperId) throws InterruptedException {
		reseivesAMessageMatching(sniperId, equalTo(XMPPAuction.JOIN_COMMAND_FORMAT));
	}

	public void reportPrice(final int price, final int increment, final String bidder) throws XMPPException {
		currentChat.sendMessage(format("SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;",
				price, increment, bidder));
	}

	public void hasReceivedBid(final int bid, final String sniperId) throws InterruptedException {
		reseivesAMessageMatching(sniperId, equalTo(format(XMPPAuction.BID_COMMAND_FORMAT, bid)));
	}

	public void announceClosed() throws XMPPException {
		currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
	}

	public void stop() {
		connection.disconnect();
	}

	public String getItemId() {
		return itemId;
	}

	public class SingleMessageListener implements MessageListener {

		private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(1);

		@Override
		public void processMessage(final Chat chat, final Message message) {
			messages.add(message);
		}

		public void reseivesAMessage(final Matcher messageMatcher) throws InterruptedException {
			final Message message = messages.poll(5, SECONDS);
			assertThat(message, hasProperty("body", messageMatcher));
		}
	}

	private void reseivesAMessageMatching(final String sniperId, final Matcher<String> messageMatcher)
			throws InterruptedException {
		messageListener.reseivesAMessage(messageMatcher);
		assertThat(currentChat.getParticipant(), equalTo(sniperId));
	}
}