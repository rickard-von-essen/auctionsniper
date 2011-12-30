package com.example.auctionsniper;

import static com.example.auctionsniper.Main.MAIN_WINDOW_NAME;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MainWindow extends JFrame {

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

	public void sniperStatusChanged(final SniperSnapshot sniperSnapshot) {
		snipers.sniperStatusChanged(sniperSnapshot);
	}
}
