package com.example.auctionsniper;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class MainWindow extends JFrame {

	private static final String SNIPERS_TABLE_NAME = "Snipers";
	public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
	public static final String APPLICATION_TITLE = "Auction Sniper";
	public static final String NEW_ITEM_ID_NAME = "item id";
	public static final String JOIN_BUTTON_NAME = "join button";
	private static final long serialVersionUID = 1L;

	private final SnipersTableModel snipers;

	public MainWindow(final SnipersTableModel snipers) {
		super("Auction Sniper");
		this.snipers = snipers;
		setName(MAIN_WINDOW_NAME);
		fillContentPane(makeSnipersTable(), makeControls());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private JPanel makeControls() {
		final JPanel controls = new JPanel(new FlowLayout());
		final JTextField itemIdField = new JTextField();
		itemIdField.setColumns(25);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		controls.add(itemIdField);

		final JButton joinAuctionButton = new JButton("Join auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		controls.add(joinAuctionButton);

		return controls;
	}

	private void fillContentPane(final JTable snipersTable, final JPanel controls) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		contentPane.add(controls, BorderLayout.NORTH);
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
