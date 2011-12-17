package com.example.auctionsniper.systest;

import static com.example.auctionsniper.MainWindow.STATUS_JOINING;
import static com.example.auctionsniper.MainWindow.STATUS_LOST;

import com.example.auctionsniper.Main;

public class ApplicationRunner {

	protected static final String XMPP_HOSTNAME = "localhost";
	protected static final String SNIPER_ID = "sniper";
	protected static final String SNIPER_PASSWORD = "sniper";

	private AuctionSniperDriver driver;

	public void startBiddingIn(final FakeAuctionServer auction) {
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
}