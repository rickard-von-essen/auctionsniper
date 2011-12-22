package com.example.auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Action Sniper application
 * 
 * <a href="http://www.growing-object-oriented-software.com/">Growing
 * Object-Oriented Software, Guided by Tests - Steve Freeman and Nat Pryce</a>
 * 
 * Adapted to build with Maven and mock with Moockito.
 */
public class Main {

	public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
	public static final String SNIPER_STATUS_NAME = "sniper status";

	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";

	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;
	private static final int ARG_ITEM_ID = 3;
	private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

	private MainWindow ui;
	@SuppressWarnings("unused") private Chat notToBeGCd;

	public Main() throws Exception {
		startUserInterface();
	}

	private class SniperStateDisplayer implements SniperListener {

		@Override
		public void sniperLost() {
			showStatus(MainWindow.STATUS_LOST);
		}

		@Override
		public void sniperBidding() {
			showStatus(MainWindow.STATUS_BIDDING);
		}

		/*
		 * public void sniperWinning() { showStatus(MainWindow.STATUS_WINNING);
		 * }
		 */

		private void showStatus(final String status) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ui.showStatus(status);
				}
			});
		}
	}

	private void startUserInterface() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ui = new MainWindow();
			}
		});
	}

	public static void main(final String... args) throws Exception {
		final Main main = new Main();
		final XMPPConnection connection = connectTo(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
		main.joinAuction(connection, args[ARG_ITEM_ID]);
	}

	private void joinAuction(final XMPPConnection connection, final String itemId) throws XMPPException {
		disconnectWhenUICloses(connection);

		final Chat chat = connection.getChatManager().createChat(actionId(itemId, connection), null);
		this.notToBeGCd = chat;

		final Auction auction = new XMPPAuction(chat);

		chat.addMessageListener(new AuctionMessageTranslator(new AuctionSniper(auction, new SniperStateDisplayer())));
		auction.join();
	}

	private void disconnectWhenUICloses(final XMPPConnection connection) {
		ui.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(final WindowEvent e) {
				connection.disconnect();
			}
		});
	}

	private static XMPPConnection connectTo(final String hostname, final String username, final String password)
			throws XMPPException {
		final XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		return connection;
	}

	private static String actionId(final String itemId, final XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}
}
