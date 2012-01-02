package com.example.auctionsniper;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {

	private static final long serialVersionUID = -4113124037723131402L;
	private final List<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();
	private static final String[] STATUS_TEXT = { "Joining auction", "Bidding", "Winning", "Lost", "Won" };

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public int getRowCount() {
		return snapshots.size();
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
	}

	@Override
	public String getColumnName(final int columnIndex) {
		return Column.at(columnIndex).name;
	}

	public static String textFor(final SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}

	@Override
	public void sniperStateChanged(final SniperSnapshot snapshot) {
		final int row = rowMatching(snapshot);
		snapshots.set(row, snapshot);
		fireTableRowsUpdated(row, row);
	}

	private int rowMatching(final SniperSnapshot snapshot) {
		for (int i = 0; i < snapshots.size(); i++) {
			if (snapshot.isForSameItemAs(snapshots.get(i))) {
				return i;
			}
		}
		throw new Defect("Cannot find match for " + snapshot);
	}

	public void addSniper(final SniperSnapshot snapshot) {
		snapshots.add(snapshot);
		final int rowIndex = snapshots.size();
		fireTableRowsInserted(rowIndex, rowIndex);
	}
}