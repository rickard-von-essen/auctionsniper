package com.example.auctionsniper.systest;

import static com.example.auctionsniper.ui.SnipersTableModel.textFor;

import com.example.auctionsniper.Main;
import com.example.auctionsniper.SniperState;
import com.example.auctionsniper.ui.MainWindow;
import com.example.auctionsniper.ui.SnipersTableModel;

public class ApplicationRunner {

	public static final String AUCTION_RESOURCE = "Auction";
	public static final String XMPP_HOSTNAME = "localhost";
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = String.format("%s@%s/%s", SNIPER_ID, XMPP_HOSTNAME, AUCTION_RESOURCE);

	private AuctionSniperDriver driver;

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
		driver.showSniperStatus(itemId, 0, 0, textFor(SniperState.JOINING));
	}

	private void startSniper() {
		final Thread thread = new Thread("Test Application") {
			@Override
			public void run() {
				try {
					Main.main(new String[] { XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD });
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

	public void showSniperHasWonAuction(final FakeAuctionServer auction, final int lastPrice) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastPrice, SnipersTableModel.textFor(SniperState.WON));
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}

	public void hasShownSniperIsLosing(final FakeAuctionServer auction, final int i, final int j) {
		// TODO Auto-generated method stub
	}
}