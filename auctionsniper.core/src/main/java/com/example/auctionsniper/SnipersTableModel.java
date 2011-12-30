package com.example.auctionsniper;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -4113124037723131402L;
	private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
	private SniperSnapshot snapshot = STARTING_UP;
	private static final String[] STATUS_TEXT = { "Joining auction", "Bidding", "Winning", "Lost", "Won" };

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		switch (Column.at(columnIndex)) {
		case ITEM_IDENTIFIER:
			return snapshot.itemId;
		case LAST_PRICE:
			return snapshot.lastPrice;
		case LAST_BID:
			return snapshot.lastBid;
		case SNIPER_STATE:
			return textFor(snapshot.state);
		default:
			throw new IllegalArgumentException("No column at " + columnIndex);
		}
	}

	public static String textFor(final SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}

	public void sniperStatusChanged(final SniperSnapshot snapshot) {
		this.snapshot = snapshot;
		fireTableRowsUpdated(0, 0);
	}
}