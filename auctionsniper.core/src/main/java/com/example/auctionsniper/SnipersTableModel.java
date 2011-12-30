package com.example.auctionsniper;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -4113124037723131402L;
	private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
	private String stateText = MainWindow.STATUS_JOINING;
	private SniperSnapshot sniperSnapshot = STARTING_UP;
	private static final String[] STATUS_TEXT = { MainWindow.STATUS_JOINING, MainWindow.STATUS_BIDDING,
			MainWindow.STATUS_WINNING };

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	public void setStateText(final String state) {
		stateText = state;
		fireTableRowsUpdated(0, 0);
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		switch (Column.at(columnIndex)) {
		case ITEM_IDENTIFIER:
			return sniperSnapshot.itemId;
		case LAST_PRICE:
			return sniperSnapshot.lastPrice;
		case LAST_BID:
			return sniperSnapshot.lastBid;
		case SNIPER_STATE:
			return stateText;
		default:
			throw new IllegalArgumentException("No column at " + columnIndex);
		}
	}

	public void sniperStatusChanged(final SniperSnapshot sniperSnapshot) {
		this.sniperSnapshot = sniperSnapshot;
		this.stateText = STATUS_TEXT[sniperSnapshot.state.ordinal()];
		fireTableRowsUpdated(0, 0);
	}
}