package com.example.auctionsniper;

import org.jmock.example.announcer.Announcer;

public class AuctionSniper implements AuctionEventListener {

	private final Auction auction;
	private SniperSnapshot snapshot;
	private final Announcer<SniperListener> announcer = Announcer.to(SniperListener.class);

	public AuctionSniper(final Item item, final Auction auction) {
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(item);
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
			if (snapshot.item.allowsBid(bid)) {
				auction.bid(bid);
				snapshot = snapshot.bidding(price, bid);
			} else {
				snapshot = snapshot.losing(price);
			}
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

	@Override
	public void auctionFailed() {
		snapshot = snapshot.failed();
		announcer.announce().sniperStateChanged(snapshot);
	}
}
