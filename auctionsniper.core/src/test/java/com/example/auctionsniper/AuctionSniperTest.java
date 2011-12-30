package com.example.auctionsniper;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
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

		inOrder.verify(listener).sniperStateChanged(argThat(is(aSniperThatIs(SniperState.BIDDING))));
		inOrder.verify(listener, atLeast(1)).sniperLost();
	}

	@Test
	public void reportsWonIfAuctionClosesWhenWinning() {
		final InOrder inOrder = inOrder(listener);

		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		sniper.auctionClosed();

		inOrder.verify(listener).sniperStateChanged(argThat(is(aSniperThatIs(SniperState.WINNING))));
		inOrder.verify(listener, atLeast(1)).sniperWon();
	}

	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
		final int price = 1001;
		final int increment = 25;
		final int bid = price + increment;

		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		verify(auction).bid(price + increment);
		verify(listener, atLeast(1)).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
	}

	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper() throws Exception {
		final InOrder inOrder = inOrder(listener);

		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
		sniper.currentPrice(135, 45, PriceSource.FromSniper);
		inOrder.verify(listener, atLeast(1)).sniperStateChanged(
				new SniperSnapshot(ITEM_ID, 123, 135, SniperState.BIDDING));
		inOrder.verify(listener, atLeast(1)).sniperStateChanged(
				new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
	}

	private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
		return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is ", "was") {
			@Override
			protected SniperState featureValueOf(final SniperSnapshot actual) {
				return actual.state;
			}
		};
	}
}