package com.example.auctionsniper.xmpp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.example.auctionsniper.AuctionEventListener;
import com.example.auctionsniper.AuctionEventListener.PriceSource;
import com.example.auctionsniper.xmpp.AuctionMessageTranslator;

@RunWith(MockitoJUnitRunner.class)
public class AuctionMessageTranslatorTest {

	private static final String SNIPER_ID = "sniper";

	private final AuctionEventListener listener = mock(AuctionEventListener.class);
	private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener);

	private static final Chat UNUSED_CHAT = null;

	@Test
	public void notifiesAuctionClosedWhenCloseMessageReceived() {
		final Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: CLOSE;");
		translator.processMessage(UNUSED_CHAT, message);

		verify(listener).auctionClosed();
	}

	@Test
	public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() throws Exception {
		final Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");
		translator.processMessage(UNUSED_CHAT, message);

		verify(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
	}

	@Test
	public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() throws Exception {
		final Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";");
		translator.processMessage(UNUSED_CHAT, message);

		verify(listener).currentPrice(234, 5, PriceSource.FromSniper);
	}
}
