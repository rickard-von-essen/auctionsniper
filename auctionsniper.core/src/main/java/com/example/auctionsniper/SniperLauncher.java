package com.example.auctionsniper;

import com.example.auctionsniper.ui.SniperCollector;

public class SniperLauncher implements UserRequestListener {

	private final AuctionHouse auctionHouse;
	private final SniperCollector collector;

	public SniperLauncher(final AuctionHouse auctionHouse, final SniperCollector collector) {
		this.auctionHouse = auctionHouse;
		this.collector = collector;
	}

	@Override
	public void joinAuction(final Item item) {
		final Auction auction = auctionHouse.auctionFor(item);
		final AuctionSniper sniper = new AuctionSniper(item, auction);
		auction.addAuctionEventListener(sniper);
		collector.addSniper(sniper);
		auction.join();
	}
}
