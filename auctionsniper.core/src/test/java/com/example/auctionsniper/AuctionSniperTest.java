package com.example.auctionsniper;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.example.auctionsniper.AuctionEventListener.PriceSource;

@RunWith(MockitoJUnitRunner.class)
public class AuctionSniperTest {

	@Mock private Auction auction;
	@Mock private SniperListener listener;
	@InjectMocks private AuctionSniper sniper;

	@Test
	public void reportsLostIfAuctionClosesImmediately() {
		sniper.auctionClosed();
		verify(listener).sniperLost();
	}

	@Test
	public void reportsLostIfAuctionClosesWhenBidding() {
		final InOrder inOrder = inOrder(listener);

		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.auctionClosed();

		inOrder.verify(listener).sniperBidding();
		inOrder.verify(listener, atLeast(1)).sniperLost();
	}

	@Test
	public void reportsWonIfAuctionClosesWhenWinning() {
		final InOrder inOrder = inOrder(listener);

		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		sniper.auctionClosed();

		inOrder.verify(listener).sniperWinning();
		inOrder.verify(listener, atLeast(1)).sniperWon();
	}

	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
		final int price = 1001;
		final int increment = 25;

		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		verify(auction).bid(price + increment);
		verify(listener, atLeast(1)).sniperBidding();
	}

	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper() throws Exception {
		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		verify(listener, atLeast(1)).sniperWinning();
	}
}