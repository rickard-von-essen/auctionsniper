package com.example.auctionsniper;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
		model.sniperStatusChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));

		verify(listener).tableChanged(any(TableModelEvent.class));

		assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
		assertColumnEquals(Column.LAST_PRICE, 555);
		assertColumnEquals(Column.LAST_BID, 666);
		assertColumnEquals(Column.SNIPER_STATE, SnipersTableModel.textFor(SniperState.BIDDING));
	}

	private void assertColumnEquals(final Column column, final Object expected) {
		final int rowIndex = 0;
		final int columnIndex = column.ordinal();
		assertThat(expected, is(model.getValueAt(rowIndex, columnIndex)));
	}
}
