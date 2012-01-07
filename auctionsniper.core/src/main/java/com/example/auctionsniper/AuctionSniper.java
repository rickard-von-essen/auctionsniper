package com.example.auctionsniper;

import org.jmock.example.announcer.Announcer;

public class AuctionSniper implements AuctionEventListener {

	private final Auction auction;
	private SniperSnapshot snapshot;
	private final Announcer<SniperListener> announcer = Announcer.to(SniperListener.class);

	public AuctionSniper(final String itemId, final Auction auction) {
		this.auction = auction;
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
		announcer.announce().sniperStateChanged(snapshot);
	}

	public SniperSnapshot getSnapshot() {
		return snapshot;
	}

	public void addSniperListener(final SniperListener listener) {
		announcer.addListener(listener);
	}
}
