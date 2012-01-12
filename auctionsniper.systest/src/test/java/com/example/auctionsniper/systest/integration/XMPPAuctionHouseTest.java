package com.example.auctionsniper.systest.integration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;

import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.example.auctionsniper.Auction;
import com.example.auctionsniper.AuctionEventListener;
import com.example.auctionsniper.Item;
import com.example.auctionsniper.systest.ApplicationRunner;
import com.example.auctionsniper.systest.FakeAuctionServer;
import com.example.auctionsniper.xmpp.XMPPAuctionHouse;

@Category(IntegrationCategory.class)
public class XMPPAuctionHouseTest {

	private static final Item ITEM = new Item("item-54321", Integer.MAX_VALUE);
	private final FakeAuctionServer server = new FakeAuctionServer(ITEM.itemId);
	private XMPPAuctionHouse auctionHouse;

	@Before
	public void openConnection() throws XMPPException {
		auctionHouse = XMPPAuctionHouse.connect(ApplicationRunner.XMPP_HOSTNAME, ApplicationRunner.SNIPER_ID,
				ApplicationRunner.SNIPER_PASSWORD);
	}

	@After
	public void closeConnection() {
		if (auctionHouse != null) {
			auctionHouse.disconnect();
		}
	}

	@Before
	public void startAuction() throws XMPPException {
		server.startSellingItem();
	}

	@After
	public void stopAuction() {
		server.stop();
	}

	@Test
	public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
		final CountDownLatch auctionWasClosed = new CountDownLatch(1);

		final Auction auction = auctionHouse.auctionFor(ITEM);
		auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

		auction.join();
		server.hasReceivedJoinRequestsFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		server.announceClosed();

		assertThat(auctionWasClosed.await(2, SECONDS), is(true));
	}

	private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
		return new AuctionEventListener() {
			@Override
			public void currentPrice(final int currentPrice, final int increment, final PriceSource priceSource) {
				// not implemented
			}

			@Override
			public void auctionClosed() {
				auctionWasClosed.countDown();
			}
		};
	}
}