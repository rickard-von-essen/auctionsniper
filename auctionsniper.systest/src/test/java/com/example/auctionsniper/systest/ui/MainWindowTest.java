package com.example.auctionsniper.systest.ui;

import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Test;

import com.example.auctionsniper.SniperPortfolio;
import com.example.auctionsniper.UserRequestListener;
import com.example.auctionsniper.systest.AuctionSniperDriver;
import com.example.auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.probe.ValueMatcherProbe;

public class MainWindowTest {

	private final SniperPortfolio portfolio = new SniperPortfolio();
	private final MainWindow mainWindow = new MainWindow(portfolio);
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