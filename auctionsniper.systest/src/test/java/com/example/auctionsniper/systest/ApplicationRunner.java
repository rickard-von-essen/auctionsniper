package com.example.auctionsniper.systest;

import static com.example.auctionsniper.SnipersTableModel.textFor;

import com.example.auctionsniper.Main;
import com.example.auctionsniper.MainWindow;
import com.example.auctionsniper.SniperState;
import com.example.auctionsniper.SnipersTableModel;

public class ApplicationRunner {

	protected static final String XMPP_HOSTNAME = "localhost";
	protected static final String SNIPER_ID = "sniper";
	protected static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + XMPP_HOSTNAME + "/Auction";

	private AuctionSniperDriver driver;

	public void startBiddingIn(final FakeAuctionServer... auctions) {

		final Thread thread = new Thread("Test Application") {
			@Override
			public void run() {
				try {
					Main.main(arguments(auctions));
				} catch (final Exception error) {
					error.printStackTrace();
				}
			}

		};
		thread.setDaemon(true);
		thread.start();

		driver = new AuctionSniperDriver(1000);
		driver.hasTitle(MainWindow.APPLICATION_TITLE);
		driver.hasColumnTitles();
		for (final FakeAuctionServer auction : auctions) {
			driver.showSniperStatus(auction.getItemId(), 0, 0, textFor(SniperState.JOINING));
		}
	}

	private static String[] arguments(final FakeAuctionServer... auctions) {
		final String[] arguments = new String[auctions.length + 3];
		arguments[0] = XMPP_HOSTNAME;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPER_PASSWORD;
		for (int i = 0; i < auctions.length; i++) {
			arguments[i + 3] = auctions[i].getItemId();
		}

		return arguments;
	};

	public void showSniperHasLostAuction(final FakeAuctionServer auction, final int lastPrice, final int lastBid) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(SniperState.LOST));
	}

	public void hasShownSniperIsBidding(final FakeAuctionServer auction, final int lastPrice, final int lastBid) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(SniperState.BIDDING));
	}

	public void hasShownSniperIsWinning(final FakeAuctionServer auction, final int winningBid) {
		driver.showSniperStatus(auction.getItemId(), winningBid, winningBid,
				SnipersTableModel.textFor(SniperState.WINNING));
	}

	public void showSniperHasWonAuction(final FakeAuctionServer auction, final int lastPrice) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastPrice, SnipersTableModel.textFor(SniperState.WON));
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}
}