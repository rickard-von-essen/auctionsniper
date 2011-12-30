package com.example.auctionsniper;

public class AuctionSniper implements AuctionEventListener {

	private final SniperListener listener;
	private final Auction auction;
	private boolean isWinning = false;
	private SniperSnapshot snapshot;

	public AuctionSniper(final String itemId, final Auction auction, final SniperListener listener) {
		this.auction = auction;
		this.listener = listener;
		this.snapshot = SniperSnapshot.joining(itemId);
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
			snapshot = snapshot.winning(price);
		} else {
			final int bid = price + increment;
			auction.bid(bid);
			snapshot = snapshot.bidding(price, bid);
		}
		listener.sniperStateChanged(snapshot);
	}
}
