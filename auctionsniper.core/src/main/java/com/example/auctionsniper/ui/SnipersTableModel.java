package com.example.auctionsniper.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import com.example.auctionsniper.AuctionSniper;
import com.example.auctionsniper.Defect;
import com.example.auctionsniper.SniperListener;
import com.example.auctionsniper.SniperPortfolio.PortfolioListener;
import com.example.auctionsniper.SniperSnapshot;
import com.example.auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {

	private static final long serialVersionUID = -4113124037723131402L;
	private final List<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();
	private static final String[] STATUS_TEXT = { "Joining auction", "Bidding", "Winning", "Losing", "Lost", "Won" };

	private class SwingThreadSniperListener implements SniperListener {
		private final SnipersTableModel snipers;

		public SwingThreadSniperListener(final SnipersTableModel snipers) {
			this.snipers = snipers;
		}

		@Override
		public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					snipers.sniperStateChanged(sniperSnapshot);
				}
			});
		}
	}

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

	private void addSniperSnapshot(final SniperSnapshot sniperSnapshot) {
		snapshots.add(sniperSnapshot);
		final int rowIndex = snapshots.size() - 1;
		fireTableRowsInserted(rowIndex, rowIndex);
	}

	@Override
	public void sniperAdded(final AuctionSniper sniper) {
		addSniperSnapshot(sniper.getSnapshot());
		sniper.addSniperListener(new SwingThreadSniperListener(this));
	}
}