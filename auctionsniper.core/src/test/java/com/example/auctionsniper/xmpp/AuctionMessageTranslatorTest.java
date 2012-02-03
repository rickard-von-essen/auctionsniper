package com.example.auctionsniper.xmpp;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.example.auctionsniper.AuctionEventListener;
import com.example.auctionsniper.AuctionEventListener.PriceSource;

@RunWith(MockitoJUnitRunner.class)
public class AuctionMessageTranslatorTest {

	private static final String SNIPER_ID = "sniper";

	private final AuctionEventListener listener = mock(AuctionEventListener.class);
	private final XMPPFailureReporter failureReporter = mock(XMPPFailureReporter.class);
	private final AuctionMessageTranslator translator = new AuctionMessageTranslator(
			AuctionMessageTranslatorTest.SNIPER_ID, listener, failureReporter);

	private static final Chat UNUSED_CHAT = null;

	@Test
	public void notifiesAuctionClosedWhenCloseMessageReceived() {
		translator.processMessage(AuctionMessageTranslatorTest.UNUSED_CHAT, message("SOLVersion: 1.1; Event: CLOSE;"));

		verify(listener).auctionClosed();
	}

	@Test
	public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() throws Exception {
		translator.processMessage(AuctionMessageTranslatorTest.UNUSED_CHAT,
				message("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"));

		verify(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
	}

	@Test
	public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() throws Exception {
		translator.processMessage(AuctionMessageTranslatorTest.UNUSED_CHAT,
				message("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: "
						+ AuctionMessageTranslatorTest.SNIPER_ID + ";"));

		verify(listener).currentPrice(234, 5, PriceSource.FromSniper);
	}

	@Test
	public void notifiesAuctionFailedWhenBadMessageReceived() throws Exception {
		final String badMessage = "a bad message";
		translator.processMessage(AuctionMessageTranslatorTest.UNUSED_CHAT, message(badMessage));
		verify(listener).auctionFailed();
		verify(failureReporter).cannotTranslateMessage(eq(AuctionMessageTranslatorTest.SNIPER_ID), eq(badMessage),
				any(Exception.class));
	}

	@Test
	public void notifiesAuctionFailedWhenEventTypeMissing() throws Exception {
		translator.processMessage(AuctionMessageTranslatorTest.UNUSED_CHAT,
				message("SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: "
						+ AuctionMessageTranslatorTest.SNIPER_ID + ";"));

		verify(listener).auctionFailed();
	}

	private Message message(final String messageBody) {
		final Message message = new Message();
		message.setBody(messageBody);
		return message;
	}
}