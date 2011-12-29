package com.example.auctionsniper;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.example.auctionsniper.AuctionEventListener.PriceSource;

@RunWith(MockitoJUnitRunner.class)
public class AuctionSniperTest {

	private static final String ITEM_ID = "item-431";
	@Mock private Auction auction;
	@Mock private SniperListener listener;
	private AuctionSniper sniper;

	@Before
	public void createSniper() {
		sniper = new AuctionSniper(ITEM_ID, auction, listener);
	}

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

		inOrder.verify(listener).sniperBidding(any(SniperState.class));
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
		final int bid = price + increment;

		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		verify(auction).bid(price + increment);
		verify(listener, atLeast(1)).sniperBidding(new SniperState(ITEM_ID, price, bid));
	}

	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper() throws Exception {
		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		verify(listener, atLeast(1)).sniperWinning();
	}
}