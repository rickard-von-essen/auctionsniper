package com.example.auctionsniper;

public class AuctionSniper implements AuctionEventListener {

	private final SniperListener listener;
	private final Auction auction;
	private boolean isWinning = false;

	public AuctionSniper(final Auction auction, final SniperListener listener) {
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
		switch (priceSource) {
		case FromSniper:
			listener.sniperWinning();
			break;
		default:
			auction.bid(price + increment);
			listener.sniperBidding();
		}
	}
}
