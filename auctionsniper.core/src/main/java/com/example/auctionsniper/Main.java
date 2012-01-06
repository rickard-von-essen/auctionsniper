package com.example.auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.example.auctionsniper.ui.MainWindow;
import com.example.auctionsniper.ui.SnipersTableModel;
import com.example.auctionsniper.xmpp.XMPPAuction;

/**
 * Action Sniper application
 * 
 * <a href="http://www.growing-object-oriented-software.com/">Growing
 * Object-Oriented Software, Guided by Tests - Steve Freeman and Nat Pryce</a>
 * 
 * Adapted to build with Maven and mock with Moockito.
 */
public class Main {

	public static final String SNIPER_STATUS_NAME = "sniper status";
	public static final String AUCTION_RESOURCE = "Auction";

	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;

	private final SnipersTableModel snipers = new SnipersTableModel();
	private MainWindow ui;
	private final List<Auction> notToBeGCd = new ArrayList<Auction>();

	public Main() throws Exception {
		startUserInterface();
	}

	private class SwingThreadSniperListener implements SniperListener {
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

	private void startUserInterface() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ui = new MainWindow(snipers);
			}
		});
	}

	public static void main(final String... args) throws Exception {
		final Main main = new Main();
		final XMPPConnection connection = connectTo(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
		main.disconnectWhenUICloses(connection);
		main.addUserRequestListenerFor(connection);
	}

	private void addUserRequestListenerFor(final XMPPConnection connection) {
		ui.addUserRequestListener(new UserRequestListener() {
			@Override
			public void joinAuction(final String itemId) {
				snipers.addSniper(SniperSnapshot.joining(itemId));
				final Auction auction = new XMPPAuction(connection, itemId);
				notToBeGCd.add(auction);
				auction.addAuctionEventListener(new AuctionSniper(itemId, auction, new SwingThreadSniperListener()));
				auction.join();
			}
		});
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
}
