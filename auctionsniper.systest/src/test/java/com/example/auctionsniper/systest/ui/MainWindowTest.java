package com.example.auctionsniper.systest.ui;

import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Test;

import com.example.auctionsniper.MainWindow;
import com.example.auctionsniper.SnipersTableModel;
import com.example.auctionsniper.UserRequestListener;
import com.example.auctionsniper.systest.AuctionSniperDriver;
import com.objogate.wl.swing.probe.ValueMatcherProbe;

public class MainWindowTest {

	private final SnipersTableModel tableModel = new SnipersTableModel();
	private final MainWindow mainWindow = new MainWindow(tableModel);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

	@Test
	public void makesUserRequestWhenJoinButtonClicked() {
		final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<String>(equalTo("an item-id"),
				"join request");

		mainWindow.addUserRequestListener(new UserRequestListener() {
			@Override
			public void joinAuction(final String itemId) {
				buttonProbe.setReceivedValue(itemId);
			}
		});

		driver.startBiddingFor("aan item-idd"); // TODO this is a stupid bug in
												// WindowLicker?
		driver.check(buttonProbe);
	}
}