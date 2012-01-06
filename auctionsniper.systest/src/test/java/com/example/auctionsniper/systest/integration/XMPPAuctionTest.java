package com.example.auctionsniper.systest.integration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.example.auctionsniper.AuctionEventListener;
import com.example.auctionsniper.systest.ApplicationRunner;
import com.example.auctionsniper.systest.FakeAuctionServer;
import com.example.auctionsniper.xmpp.XMPPAuction;

@Category(IntegrationCategory.class)
public class XMPPAuctionTest {

	private final FakeAuctionServer server = new FakeAuctionServer("item-54321");
	private XMPPConnection connection;

	@Before
	public void openConnection() throws XMPPException {
		connection = new XMPPConnection(ApplicationRunner.XMPP_HOSTNAME);
		connection.connect();
		connection.login(ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD,
				ApplicationRunner.AUCTION_RESOURCE);
	}

	@After
	public void closeConnection() {
		if (connection != null) {
			connection.disconnect();
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

		final XMPPAuction auction = new XMPPAuction(connection, server.getItemId());
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