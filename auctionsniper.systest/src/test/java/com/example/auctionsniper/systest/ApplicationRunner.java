package com.example.auctionsniper.systest;

import static org.hamcrest.text.StringContainsInOrder.stringContainsInOrder;

import java.io.IOException;
import java.util.Arrays;

import com.example.auctionsniper.Main;
import com.example.auctionsniper.SniperState;
import com.example.auctionsniper.ui.MainWindow;
import com.example.auctionsniper.ui.SnipersTableModel;

public class ApplicationRunner {

	public static final String AUCTION_RESOURCE = "Auction";
	public static final String XMPP_HOSTNAME = "localhost";
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = String.format("%s@%s/%s", ApplicationRunner.SNIPER_ID,
			ApplicationRunner.XMPP_HOSTNAME, ApplicationRunner.AUCTION_RESOURCE);

	private AuctionSniperDriver driver;
	private final AuctionLogDriver logDriver = new AuctionLogDriver();

	public void startBiddingIn(final FakeAuctionServer... auctions) {
		startSniper();
		for (final FakeAuctionServer auction : auctions) {
			startBiddingFor(auction, Integer.MAX_VALUE);
		}
	}

	public void startBiddingWithStopPrice(final FakeAuctionServer auction, final int stopPrice) {
		startSniper();
		startBiddingFor(auction, stopPrice);
	}

	private void startBiddingFor(final FakeAuctionServer auction, final int stopPrice) {
		final String itemId = auction.getItemId();
		driver.startBiddingFor(itemId, stopPrice);
		driver.showSniperStatus(itemId, 0, 0, SnipersTableModel.textFor(SniperState.JOINING));
	}

	private void startSniper() {
		logDriver.clearLog();
		final Thread thread = new Thread("Test Application") {
			@Override
			public void run() {
				try {
					Main.main(new String[] { ApplicationRunner.XMPP_HOSTNAME, ApplicationRunner.SNIPER_ID,
							ApplicationRunner.SNIPER_PASSWORD });
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
	}

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

	public void hasShownSniperIsLosing(final FakeAuctionServer auction, final int lastPrice, final int lastBid) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(SniperState.LOSING));
	}

	public void showSniperHasWonAuction(final FakeAuctionServer auction, final int lastPrice) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastPrice, SnipersTableModel.textFor(SniperState.WON));
	}

	public void showSniperHasFailed(final FakeAuctionServer auction, final int lastPrice, final int lastBid) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(SniperState.FAILED));
	}

	public void reportsInvalidMessage(final FakeAuctionServer auction, final String brokenMessage) throws IOException {
		logDriver.hasEntry(stringContainsInOrder(Arrays.asList(new String[] { "LoggingXMPPFailureReporter",
				"Could not translate message", brokenMessage, "because", "ArrayIndexOutOfBoundsException" })));
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
		if (logDriver != null) {
			logDriver.clearLog();
		}
	}
}