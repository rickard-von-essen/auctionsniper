package com.example.auctionsniper;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MainWindow extends JFrame {

	private static final String SNIPERS_TABLE_NAME = "Snipers";
	public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
	public static final String APPLICATION_TITLE = "Auction Sniper";
	private static final long serialVersionUID = 1L;

	private final SnipersTableModel snipers;

	public MainWindow(final SnipersTableModel snipers) {
		super("Auction Sniper");
		this.snipers = snipers;
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
		snipers.sniperStateChanged(sniperSnapshot);
	}
}
