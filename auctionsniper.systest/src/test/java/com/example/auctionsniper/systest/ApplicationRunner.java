package com.example.auctionsniper.systest;

import com.example.auctionsniper.Main;
import com.example.auctionsniper.SniperState;
import com.example.auctionsniper.SnipersTableModel;

public class ApplicationRunner {

	protected static final String XMPP_HOSTNAME = "localhost";
	protected static final String SNIPER_ID = "sniper";
	protected static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + XMPP_HOSTNAME + "/Auction";

	private AuctionSniperDriver driver;
	private String itemId;

	public void startBiddingIn(final FakeAuctionServer auction) {
		itemId = auction.getItemId();

		final Thread thread = new Thread("Test Application") {
			@Override
			public void run() {
				try {
					Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
				} catch (final Exception error) {
					error.printStackTrace();
				}
			};
		};
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.showSniperStatus("", 0, 0, SnipersTableModel.textFor(SniperState.JOINING));
	}

	public void showSniperHasLostAuction(final int lastPrice, final int lastBid) {
		driver.showSniperStatus(itemId, lastPrice, lastBid, SnipersTableModel.textFor(SniperState.LOST));
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}

	public void hasShownSniperIsBidding(final int lastPrice, final int lastBid) {
		driver.showSniperStatus(itemId, lastPrice, lastBid, SnipersTableModel.textFor(SniperState.BIDDING));
	}

	public void hasShownSniperIsWinning(final int winningBid) {
		driver.showSniperStatus(itemId, winningBid, winningBid, SnipersTableModel.textFor(SniperState.WINNING));
	}

	public void showSniperHasWonAuction(final int lastPrice) {
		driver.showSniperStatus(itemId, lastPrice, lastPrice, SnipersTableModel.textFor(SniperState.WON));
	}
}