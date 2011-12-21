package com.example.auctionsniper;

import static com.example.auctionsniper.Main.MAIN_WINDOW_NAME;
import static com.example.auctionsniper.Main.SNIPER_STATUS_NAME;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class MainWindow extends JFrame {

	public static final String STATUS_JOINING = "Joining auction";
	public static final String STATUS_LOST = "Auction lost";
	public static final String STATUS_BIDDING = "Bidding";
	private static final long serialVersionUID = 1L;
	private final JLabel sniperStatus = createLabel(STATUS_JOINING);

	public MainWindow() {
		super("Auction Sniper");
		setName(MAIN_WINDOW_NAME);
		add(sniperStatus);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private JLabel createLabel(final String initialText) {
		final JLabel result = new JLabel(initialText);
		result.setName(SNIPER_STATUS_NAME);
		result.setBorder(new LineBorder(Color.BLACK));
		return result;
	}

	public void showStatus(final String status) {
		sniperStatus.setText(status);
	}
}
