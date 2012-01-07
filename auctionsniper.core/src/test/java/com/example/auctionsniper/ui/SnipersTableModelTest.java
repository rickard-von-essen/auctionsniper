package com.example.auctionsniper.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.hamcrest.beans.SamePropertyValuesAs;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.example.auctionsniper.AuctionSniper;
import com.example.auctionsniper.Defect;
import com.example.auctionsniper.SniperSnapshot;
import com.example.auctionsniper.SniperState;

@RunWith(MockitoJUnitRunner.class)
public class SnipersTableModelTest {

	private final SnipersTableModel model = new SnipersTableModel();
	@Mock TableModelListener listener;

	@Before
	public void addListener() {
		model.addTableModelListener(listener);
	}

	@Test
	public void hasEnoughColumns() {
		assertThat(model.getColumnCount(), equalTo(Column.values().length));
	}

	@Test
	public void setsSniperValuesInColumns() {
		final AuctionSniper sniper = new AuctionSniper("item id", null);
		final SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);
		model.addSniper(sniper);

		model.sniperStateChanged(bidding);

		verify(listener).tableChanged(argThat(is(aRowChangeInRow(0))));
		assertRowMatchesSnapshot(0, bidding);
	}

	@Test
	public void setsUpColumnHeadins() throws Exception {
		for (final Column column : Column.values()) {
			assertThat(model.getColumnName(column.ordinal()), is(column.name));
		}
	}

	@Test
	public void notifiesListenersWhenAddingASniper() throws Exception {
		final AuctionSniper auction = new AuctionSniper("item123", null);
		assertThat(model.getRowCount(), is(0));

		model.addSniper(auction);
		verify(listener).tableChanged(argThat(is(anInsertionAtRow(0))));

		assertThat(model.getRowCount(), is(1));
		assertRowMatchesSnapshot(0, auction.getSnapshot());
	}

	@Test
	public void holdsSnipersInAdditionOrder() throws Exception {
		model.addSniper(new AuctionSniper("item 0", null));
		model.addSniper(new AuctionSniper("item 1", null));

		assertThat("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
		assertThat("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
	}

	@Test
	public void updatesCorrectRowForSniper() throws Exception {
		model.addSniper(new AuctionSniper("item 0", null));
		final AuctionSniper auction1 = new AuctionSniper("item 1", null);
		model.addSniper(auction1);
		model.sniperStateChanged(auction1.getSnapshot().bidding(200, 100));

		assertThat(200, cellValue(1, Column.LAST_PRICE));
		assertThat(100, cellValue(1, Column.LAST_BID));
	}

	@Test(expected = Defect.class)
	public void throwsDefectIfNotExistingSniperForAnUpdate() throws Exception {
		model.sniperStateChanged(new SniperSnapshot("non-exisiting item", 123, 456, SniperState.BIDDING));
	}

	private Matcher<Object> cellValue(final int rowIndex, final Column column) {
		return equalTo(model.getValueAt(rowIndex, column.ordinal()));
	}

	private Matcher<TableModelEvent> aRowChangeInRow(final int rowIndex) {
		return new SamePropertyValuesAs<TableModelEvent>(new TableModelEvent(model, rowIndex));
	}

	private Matcher<TableModelEvent> anInsertionAtRow(final int rowIndex) {
		return new SamePropertyValuesAs<TableModelEvent>(new TableModelEvent(model, rowIndex, rowIndex,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	private void assertRowMatchesSnapshot(final int rowNumber, final SniperSnapshot snapshot) {
		for (final Column column : Column.values()) {
			assertThat(model.getValueAt(rowNumber, column.ordinal()), is(column.valueIn(snapshot)));
		}
	}
}
