package com.example.auctionsniper;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

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

	protected MainWindow ui;
	@SuppressWarnings("unused")
	private Chat notToBeGCd;

	public Main() throws Exception {
		startUserInterface();
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
		final Chat chat = connection.getChatManager().createChat(actionId(itemId, connection), new MessageListener() {
			@Override
			public void processMessage(final Chat aChat, final Message message) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						ui.showStatus(MainWindow.STATUS_LOST);
					}
				});
			}
		});
		this.notToBeGCd = chat;

		chat.sendMessage(new Message());
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
