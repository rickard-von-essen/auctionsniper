package com.example.auctionsniper;

import static org.mockito.Mockito.verify;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuctionMessageTranslatorTest {

	@Mock private AuctionEventListener listener;
	@InjectMocks private AuctionMessageTranslator translator;

	private static final Chat UNUSED_CHAT = null;

	@Test
	public void notifiesAuctionClosedWhenCloseMessageReceived() {
		final Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: CLOSE;");
		translator.processMessage(UNUSED_CHAT, message);

		verify(listener).auctionClosed();
	}

	@Test
	public void notifiesBidDetailsWhenCurrentPriceMessageReceived() throws Exception {
		final Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");
		translator.processMessage(UNUSED_CHAT, message);

		verify(listener).currentPrice(192, 7);
	}
}
