package com.example.auctionsniper.systest.endtoend;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.example.auctionsniper.systest.ApplicationRunner;
import com.example.auctionsniper.systest.FakeAuctionServer;

/**
 * End-to-end test for Action Sniper application,
 */
@Category(EndToEndCategory.class)
public class AuctionSniperEndToEndTest {

	private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
	private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
	private final ApplicationRunner application = new ApplicationRunner();

	@Test
	public void sniperJoinsAuctionUntilActionCloses() throws Exception {
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestsFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.announceClosed();
		application.showSniperHasLostAuction(auction, 0, 0);
	}

	@Test
	public void sniperMakesAHigherBidButLoses() throws Exception {
		auction.startSellingItem();

		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestsFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098);

		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		auction.announceClosed();
		application.showSniperHasLostAuction(auction, 1000, 1098);
	}

	@Test
	public void sniperWinsAnAuctionByBiddingHigher() throws Exception {
		auction.startSellingItem();

		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestsFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098);

		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(auction, 1098);

		auction.announceClosed();
		application.showSniperHasWonAuction(auction, 1098);
	}

	@Test
	public void sniperBidsForMultipleItems() throws Exception {
		auction.startSellingItem();
		auction2.startSellingItem();

		application.startBiddingIn(auction, auction2);
		auction.hasReceivedJoinRequestsFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction2.hasReceivedJoinRequestsFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

		auction.reportPrice(1000, 98, "other bidder");
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		auction2.reportPrice(500, 21, "other bidder");
		auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);

		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);

		application.hasShownSniperIsWinning(auction, 1098);
		application.hasShownSniperIsWinning(auction2, 521);

		auction.announceClosed();
		auction2.announceClosed();

		application.showSniperHasWonAuction(auction, 1098);
		application.showSniperHasWonAuction(auction2, 521);
	}

	@Test
	public void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws Exception {
		auction.startSellingItem();
		application.startBiddingWithStopPrice(auction, 1100);
		auction.hasReceivedJoinRequestsFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098);

		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		auction.reportPrice(1197, 10, "third party");
		application.hasShownSniperIsLosing(auction, 1197, 1098);

		auction.reportPrice(1207, 10, "fourth party");
		application.hasShownSniperIsLosing(auction, 1207, 1098);

		auction.announceClosed();
		application.showSniperHasLostAuction(auction, 1207, 1098);
	}

	@Test
	public void sniperReportsInvalidAuctionMessageAndStopsRespondingToEvents() throws Exception {
		final String brokenMessage = "a broken message";
		auction.startSellingItem();
		auction2.startSellingItem();

		application.startBiddingIn(auction, auction2);
		auction.hasReceivedJoinRequestsFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

		auction.reportPrice(500, 20, "other bidder");
		auction.hasReceivedBid(520, ApplicationRunner.SNIPER_XMPP_ID);

		auction.sendInvalidMessageContaining(brokenMessage);
		application.showSniperHasFailed(auction, 500, 520);

		auction.reportPrice(520, 21, "other bidder");
		waitForAnotherAuctionEvent();

		application.reportsInvalidMessage(auction, brokenMessage);
		application.showSniperHasFailed(auction, 500, 520);
	}

	private void waitForAnotherAuctionEvent() throws Exception {
		auction2.hasReceivedJoinRequestsFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction2.reportPrice(600, 6, "other bidder");
		application.hasShownSniperIsBidding(auction2, 600, 606);
	}

	@After
	public void stopAuction() {
		auction.stop();
	}

	@After
	public void stopApplication() {
		application.stop();
	}
}
