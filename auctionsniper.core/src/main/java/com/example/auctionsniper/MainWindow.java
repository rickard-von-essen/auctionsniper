package com.example.auctionsniper;

import static com.example.auctionsniper.Main.MAIN_WINDOW_NAME;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class MainWindow extends JFrame {

	public static final String STATUS_JOINING = "Joining auction";
	public static final String STATUS_LOST = "Lost";
	public static final String STATUS_BIDDING = "Bidding";
	public static final String STATUS_WINNING = "Winning";
	public static final String STATUS_WON = "Won";
	private static final String SNIPERS_TABLE_NAME = "Snipers";

	private static final long serialVersionUID = 1L;
	private final SnipersTableModel snipers = new SnipersTableModel();

	public MainWindow() {
		super("Auction Sniper");
		setName(MAIN_WINDOW_NAME);
		fillContentPane(makeSnipersTable());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void fillContentPane(final JTable snipersTable) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
	}

	private JTable makeSnipersTable() {
		final JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}

	public void showStatus(final String status) {
		snipers.setStatusText(status);
	}

	public class SnipersTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -4113124037723131402L;
		private String statusText = STATUS_JOINING;

		@Override
		public int getColumnCount() {
			return 1;
		}

		public void setStatusText(final String status) {
			statusText = status;
			fireTableRowsUpdated(0, 0);
		}

		@Override
		public int getRowCount() {
			return 1;
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			return statusText;
		}
	}
}
