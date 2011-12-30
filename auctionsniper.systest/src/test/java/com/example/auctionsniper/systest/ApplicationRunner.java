package com.example.auctionsniper.systest;

import static com.example.auctionsniper.MainWindow.STATUS_JOINING;
import static com.example.auctionsniper.MainWindow.STATUS_LOST;

import com.example.auctionsniper.Main;
import com.example.auctionsniper.MainWindow;

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
		driver.showSniperStatus(STATUS_JOINING);
	}

	public void showSniperHasLostAuction() {
		driver.showSniperStatus(STATUS_LOST);
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}

	public void hasShownSniperIsBidding(final int lastPrice, final int lastBid) {
		driver.showSniperStatus(itemId, lastPrice, lastBid, MainWindow.STATUS_BIDDING);
	}

	public void hasShownSniperIsWinning(final int winningBid) {
		driver.showSniperStatus(itemId, winningBid, winningBid, MainWindow.STATUS_WINNING);
	}

	public void showSniperHasWonAuction(final int lastPrice) {
		driver.showSniperStatus(itemId, lastPrice, lastPrice, MainWindow.STATUS_WON);
	}
}