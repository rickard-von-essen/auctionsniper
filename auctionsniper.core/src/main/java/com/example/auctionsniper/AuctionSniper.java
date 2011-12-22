package com.example.auctionsniper;

public class AuctionSniper implements AuctionEventListener {

	private final SniperListener listener;
	private final Auction auction;

	public AuctionSniper(final Auction auction, final SniperListener listener) {
		this.auction = auction;
		this.listener = listener;
	}

	@Override
	public void auctionClosed() {
		listener.sniperLost();
	}

	@Override
	public void currentPrice(final int price, final int increment) {
		auction.bid(price + increment);
		listener.sniperBidding();
	}
}
