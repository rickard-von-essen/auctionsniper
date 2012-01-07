package com.example.auctionsniper;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.example.auctionsniper.ui.SniperCollector;

@RunWith(MockitoJUnitRunner.class)
public class SniperLauncherTest {

	@Mock private SniperCollector collector;
	@Mock private AuctionHouse auctionHouse;
	@InjectMocks private SniperLauncher launcher;

	@Mock private Auction auction;

	@Test
	public void addsNewSniperToCollectorAndThenJoinsAuction() {
		final String itemId = "item 123";
		when(auctionHouse.auctionFor(itemId)).thenReturn(auction);
		final InOrder inOrder = inOrder(auction, collector);

		launcher.joinAuction(itemId);

		inOrder.verify(auction).addAuctionEventListener(argThat(is(sniperForItem(itemId))));
		inOrder.verify(collector).addSniper(argThat(is(sniperForItem(itemId))));
		inOrder.verify(auction).join();
	}

	private Matcher<AuctionSniper> sniperForItem(final String itemId) {
		return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), "a sniper that is ", "was") {

			@Override
			protected String featureValueOf(final AuctionSniper actual) {
				return actual.getSnapshot().itemId;
			}
		};
	}
}