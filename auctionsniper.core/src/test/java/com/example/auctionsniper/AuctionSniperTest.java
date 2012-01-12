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

	private static final Item ITEM = new Item("item-431", 1234);
	@Mock private Auction auction;
	@Mock private SniperListener listener;
	private AuctionSniper sniper;

	@Before
	public void createSniper() {
		sniper = new AuctionSniper(ITEM, auction);
		sniper.addSniperListener(listener);
	}

	@Test
	public void reportsLostIfAuctionClosesImmediately() {
		sniper.auctionClosed();
		verify(listener).sniperStateChanged(argThat(is(aSniperThatIs(SniperState.LOST))));
	}

	@Test
	public void reportsLostIfAuctionClosesWhenBidding() {
		final InOrder inOrder = inOrder(listener);

		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		inOrder.verify(listener).sniperStateChanged(argThat(is(aSniperThatIs(SniperState.BIDDING))));

		sniper.auctionClosed();
		inOrder.verify(listener).sniperStateChanged(argThat(is(aSniperThatIs(SniperState.LOST))));
	}

	@Test
	public void reportsWonIfAuctionClosesWhenWinning() {
		final InOrder inOrder = inOrder(listener);

		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		inOrder.verify(listener).sniperStateChanged(argThat(is(aSniperThatIs(SniperState.WINNING))));

		sniper.auctionClosed();
		inOrder.verify(listener).sniperStateChanged(argThat(is(aSniperThatIs(SniperState.WON))));
	}

	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
		final int price = 1001;
		final int increment = 25;
		final int bid = price + increment;

		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		verify(auction).bid(price + increment);
		verify(listener, atLeast(1)).sniperStateChanged(new SniperSnapshot(ITEM, price, bid, SniperState.BIDDING));
	}

	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper() throws Exception {
		final InOrder inOrder = inOrder(listener);

		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
		sniper.currentPrice(135, 45, PriceSource.FromSniper);
		inOrder.verify(listener, atLeast(1))
				.sniperStateChanged(new SniperSnapshot(ITEM, 123, 135, SniperState.BIDDING));
		inOrder.verify(listener, atLeast(1))
				.sniperStateChanged(new SniperSnapshot(ITEM, 135, 135, SniperState.WINNING));
	}

	@Test
	public void doesNotBidAndReportsLosingIfSubsecquentPricesIsAboveStopPrice() throws Exception {
		final InOrder inOrder = inOrder(listener);
		final int bid = 123 + 45;

		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);

		inOrder.verify(listener, atLeast(1)).sniperStateChanged(argThat(is(aSniperThatIs(SniperState.BIDDING))));
		inOrder.verify(listener).sniperStateChanged(new SniperSnapshot(ITEM, 2345, bid, SniperState.LOSING));
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