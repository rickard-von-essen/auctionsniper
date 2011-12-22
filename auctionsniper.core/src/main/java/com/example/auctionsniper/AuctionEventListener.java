package com.example.auctionsniper;

import java.util.EventListener;

public interface AuctionEventListener extends EventListener {

	public enum PriceSource {
		FromSniper, FromOtherBidder;
	};

	void auctionClosed();

	void currentPrice(int currentPrice, int increment, PriceSource priceSource);
}
