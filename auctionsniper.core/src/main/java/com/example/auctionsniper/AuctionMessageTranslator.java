package com.example.auctionsniper;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class AuctionMessageTranslator implements MessageListener {

	private AuctionEventListener listener;

	public AuctionMessageTranslator(final AuctionEventListener listener) {
		this.listener = listener;
	}

	public AuctionMessageTranslator() {
	}

	@Override
	public void processMessage(final Chat chat, final Message message) {
		final Map<String, String> event = unpackEventFrom(message);

		final String type = event.get("Event");
		if ("CLOSE".equals(type)) {
			listener.auctionClosed();
		} else if ("PRICE".equals(type)) {
			listener.currentPrice(Integer.parseInt(event.get("CurrentPrice")), Integer.parseInt(event.get("Increment")));
		}
	}

	private Map<String, String> unpackEventFrom(final Message message) {
		final Map<String, String> event = new HashMap<String, String>();
		for (final String element : message.getBody().split(";")) {
			final String[] pair = element.split(":");
			event.put(pair[0].trim(), pair[1].trim());
		}
		return event;
	}
}