package com.example.auctionsniper.systest;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import com.example.auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.ComponentDriver;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import com.objogate.wl.swing.matcher.IterableComponentsMatcher;
import com.objogate.wl.swing.matcher.JLabelTextMatcher;

@SuppressWarnings("unchecked")
public class AuctionSniperDriver extends JFrameDriver {

	public AuctionSniperDriver(final int timeoutMillis) {
		super(new GesturePerformer(), JFrameDriver.topLevelFrame(ComponentDriver.named(MainWindow.MAIN_WINDOW_NAME),
				ComponentDriver.showingOnScreen()), new AWTEventQueueProber(timeoutMillis, 100));
	}

	public void showSniperStatus(final String itemId, final int lastPrice, final int lastBid, final String statusText) {
		final JTableDriver table = new JTableDriver(this);
		table.hasRow(IterableComponentsMatcher.matching(JLabelTextMatcher.withLabelText(itemId),
				JLabelTextMatcher.withLabelText(String.valueOf(lastPrice)),
				JLabelTextMatcher.withLabelText(String.valueOf(lastBid)), JLabelTextMatcher.withLabelText(statusText)));
	}

	public void hasColumnTitles() {
		final JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
		headers.hasHeaders(IterableComponentsMatcher.matching(JLabelTextMatcher.withLabelText("Item"),
				JLabelTextMatcher.withLabelText("Last Price"), JLabelTextMatcher.withLabelText("Last Bid"),
				JLabelTextMatcher.withLabelText("State")));
	}

	public void startBiddingFor(final String itemId, final int stopPrice) {
		// TODO This don't select all text there seams to be another bug in
		// WindowLicker
		textField(MainWindow.NEW_ITEM_ID_NAME).replaceAllText(itemId);
		textField(MainWindow.STOP_PRICE_NAME).replaceAllText(String.valueOf(stopPrice));
		bidButton().click();
	}

	private JTextFieldDriver textField(final String textFieldId) {
		final JTextFieldDriver newItemId = new JTextFieldDriver(this, JTextField.class,
				ComponentDriver.named(textFieldId));
		newItemId.focusWithMouse();
		return newItemId;
	}

	private JButtonDriver bidButton() {
		return new JButtonDriver(this, JButton.class, ComponentDriver.named(MainWindow.JOIN_BUTTON_NAME));
	}
}