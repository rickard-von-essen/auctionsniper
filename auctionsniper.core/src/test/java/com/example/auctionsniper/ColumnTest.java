package com.example.auctionsniper;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ColumnTest {

	@Test
	public void checkColumnAtReturnsCorrectValue() {
		assertThat(Column.at(0), is(Column.ITEM_IDENTIFIER));
		assertThat(Column.at(1), is(Column.LAST_PRICE));
		assertThat(Column.at(2), is(Column.LAST_BID));
		assertThat(Column.at(3), is(Column.SNIPER_STATE));
	}

	@Test
	public void checkValueInReturnsCorrectValue() throws Exception {
		final String itemId = "item id";
		final int lastPrice = 555;
		final int lastBid = 666;
		final SniperSnapshot snapshot = new SniperSnapshot(itemId, lastPrice, lastBid, SniperState.BIDDING);

		assertThat((String) Column.ITEM_IDENTIFIER.valueIn(snapshot), is(itemId));
		assertThat((Integer) Column.LAST_PRICE.valueIn(snapshot), is(lastPrice));
		assertThat((Integer) Column.LAST_BID.valueIn(snapshot), is(lastBid));
		assertThat((String) Column.SNIPER_STATE.valueIn(snapshot), is("Bidding"));
	}
}