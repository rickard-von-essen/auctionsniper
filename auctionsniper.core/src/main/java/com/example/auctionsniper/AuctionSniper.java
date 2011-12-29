package com.example.auctionsniper;

public class AuctionSniper implements AuctionEventListener {

	private final SniperListener listener;
	private final Auction auction;
	private boolean isWinning = false;
	private final String itemId;

	public AuctionSniper(final String itemId, final Auction auction, final SniperListener listener) {
		this.itemId = itemId;
		this.auction = auction;
		this.listener = listener;
	}

	@Override
	public void auctionClosed() {
		if (isWinning) {
			listener.sniperWon();
		} else {
			listener.sniperLost();
		}
	}

	@Override
	public void currentPrice(final int price, final int increment, final PriceSource priceSource) {
		isWinning = priceSource == PriceSource.FromSniper;
		if (isWinning) {
			listener.sniperWinning();
		} else {
			final int bid = price + increment;
			auction.bid(bid);
			listener.sniperBidding(new SniperState(itemId, price, bid));
		}
	}
}
