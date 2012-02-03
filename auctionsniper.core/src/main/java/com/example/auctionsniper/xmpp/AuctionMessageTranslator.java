package com.example.auctionsniper.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import com.example.auctionsniper.AuctionEventListener;
import com.example.auctionsniper.AuctionEventListener.PriceSource;

public class AuctionMessageTranslator implements MessageListener {

	private final AuctionEventListener listener;
	private final String sniperId;
	private final XMPPFailureReporter failureReporter;

	public AuctionMessageTranslator(final String sniperId, final AuctionEventListener listener,
			final XMPPFailureReporter failureReporter) {
		this.sniperId = sniperId;
		this.listener = listener;
		this.failureReporter = failureReporter;
	}

	@Override
	public void processMessage(final Chat chat, final Message message) {
		final String messageBody = message.getBody();
		try {
			translate(messageBody);
		} catch (final Exception parseException) {
			failureReporter.cannotTranslateMessage(sniperId, messageBody, parseException);
			listener.auctionFailed();
		}
	}

	private void translate(final String messageBody) {
		final AuctionEvent event = AuctionEvent.from(messageBody);

		if ("CLOSE".equals(event.type())) {
			listener.auctionClosed();
		} else if ("PRICE".equals(event.type())) {
			listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
		}
	}

	private static class AuctionEvent {

		private final Map<String, String> fields = new HashMap<String, String>();

		String type() {
			return get("Event");
		}

		public PriceSource isFrom(final String sniperId) {
			return sniperId.equals(bidder()) ? PriceSource.FromSniper : PriceSource.FromOtherBidder;
		}

		private String bidder() {
			return get("Bidder");
		}

		int currentPrice() {
			return getInt("CurrentPrice");
		}

		int increment() {
			return getInt("Increment");
		}

		private int getInt(final String fieldName) {
			return Integer.parseInt(get(fieldName));
		}

		private String get(final String fieldName) {
			final String value = fields.get(fieldName);
			if (null == value) {
				throw new MissingValueException(fieldName);
			}
			return value;
		}

		private void addField(final String field) {
			final String[] pair = field.split(":");
			fields.put(pair[0].trim(), pair[1].trim());
		}

		private static AuctionEvent from(final String messageBody) {
			final AuctionEvent event = new AuctionEvent();
			for (final String field : fieldsIn(messageBody)) {
				event.addField(field);
			}
			return event;
		}

		private static String[] fieldsIn(final String messageBody) {
			return messageBody.split(";");
		}
	}
}