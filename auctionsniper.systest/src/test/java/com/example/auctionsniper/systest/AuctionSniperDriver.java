package com.example.auctionsniper.systest;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static java.lang.String.valueOf;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import com.example.auctionsniper.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

@SuppressWarnings("unchecked")
public class AuctionSniperDriver extends JFrameDriver {

	public AuctionSniperDriver(final int timeoutMillis) {
		super(new GesturePerformer(),
				JFrameDriver.topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME), showingOnScreen()),
				new AWTEventQueueProber(timeoutMillis, 100));
	}

	public void showSniperStatus(final String itemId, final int lastPrice, final int lastBid, final String statusText) {
		final JTableDriver table = new JTableDriver(this);
		table.hasRow(matching(withLabelText(itemId), withLabelText(valueOf(lastPrice)),
				withLabelText(valueOf(lastBid)), withLabelText(statusText)));
	}

	public void hasColumnTitles() {
		final JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
		headers.hasHeaders(matching(withLabelText("Item"), withLabelText("Last Price"), withLabelText("Last Bid"),
				withLabelText("State")));
	}

	public void startBiddingFor(final String itemId) {
		itemIdField().replaceAllText(itemId);
		bidButton().click();
	}

	private JTextFieldDriver itemIdField() {
		final JTextFieldDriver newItemId = new JTextFieldDriver(this, JTextField.class,
				named(MainWindow.NEW_ITEM_ID_NAME));
		newItemId.focusWithMouse();
		return newItemId;
	}

	private JButtonDriver bidButton() {
		return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
	}
}