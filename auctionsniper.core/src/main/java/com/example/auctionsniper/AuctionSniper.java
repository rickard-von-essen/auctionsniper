package com.example.auctionsniper;

public class AuctionSniper implements AuctionEventListener {

	private final SniperListener listener;
	private final Auction auction;
	private SniperSnapshot snapshot;

	public AuctionSniper(final String itemId, final Auction auction, final SniperListener listener) {
		this.auction = auction;
		this.listener = listener;
		this.snapshot = SniperSnapshot.joining(itemId);
	}

	@Override
	public void auctionClosed() {
		snapshot = snapshot.closed();
		notifyChange();
	}

	@Override
	public void currentPrice(final int price, final int increment, final PriceSource priceSource) {
		switch (priceSource) {
		case FromSniper:
			snapshot = snapshot.winning(price);
			break;
		case FromOtherBidder:
			final int bid = price + increment;
			auction.bid(bid);
			snapshot = snapshot.bidding(price, bid);
			break;
		}
		notifyChange();
	}

	private void notifyChange() {
		listener.sniperStateChanged(snapshot);
	}
}
