package com.example.auctionsniper;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuctionSniperTest {

	@Mock private Auction auction;
	@Mock private SniperListener listener;
	@InjectMocks private AuctionSniper sniper;

	@Test
	public void reportsLostWhenAuctionCloses() {
		sniper.auctionClosed();

		verify(listener).sniperLost();
	}

	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
		final int price = 1001;
		final int increment = 25;

		sniper.currentPrice(price, increment);
		verify(auction).bid(price + increment);
		verify(listener, atLeast(1)).sniperBidding();
	}
}