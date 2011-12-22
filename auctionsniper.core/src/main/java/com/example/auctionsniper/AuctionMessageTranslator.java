package com.example.auctionsniper;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import com.example.auctionsniper.AuctionEventListener.PriceSource;

public class AuctionMessageTranslator implements MessageListener {

	private final AuctionEventListener listener;
	private final String sniperId;

	public AuctionMessageTranslator(final String sniperId, final AuctionEventListener listener) {
		this.sniperId = sniperId;
		this.listener = listener;
	}

	@Override
	public void processMessage(final Chat chat, final Message message) {
		final AuctionEvent event = AuctionEvent.from(message.getBody());

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
			return fields.get(fieldName);
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