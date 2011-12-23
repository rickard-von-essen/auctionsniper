package com.example.auctionsniper.systest;

import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static org.hamcrest.Matchers.equalTo;

import com.example.auctionsniper.Main;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

@SuppressWarnings("unchecked")
public class AuctionSniperDriver extends JFrameDriver {

	public AuctionSniperDriver(final int timeoutMillis) {
		super(new GesturePerformer(), JFrameDriver.topLevelFrame(named(Main.MAIN_WINDOW_NAME), showingOnScreen()),
				new AWTEventQueueProber(timeoutMillis, 100));
	}

	public void showSniperStatus(final String status) {
		new JTableDriver(this).hasCell(withLabelText(equalTo(status)));
	}
}