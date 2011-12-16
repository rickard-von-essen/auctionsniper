package com.example.auctionsniper.systest;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * End-to-end test for Action Sniper application,
 */
@Category(EndToEndCategory.class)
public class AuctionSniperEndToEndTest {

	private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
	private final ApplicationRunner application = new ApplicationRunner();

	@Test
	public void sniperJoinsAuctionUntilActionCloses() throws Exception {
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestsFromSniper();
		auction.announceClosed();
		application.showSniperHasLostAuction();
	}

	@After
	public void stopAction() {
		auction.stop();
	}

	@After
	public void stopApplication() {
		application.stop();
	}
}
