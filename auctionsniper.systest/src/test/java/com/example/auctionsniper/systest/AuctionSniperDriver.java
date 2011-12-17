package com.example.auctionsniper.systest;

import static org.hamcrest.Matchers.equalTo;

import com.example.auctionsniper.Main;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

@SuppressWarnings("unchecked")
public class AuctionSniperDriver extends JFrameDriver {

	public AuctionSniperDriver(final int timeoutMillis) {
		super(new GesturePerformer(), JFrameDriver.topLevelFrame(named(Main.MAIN_WINDOW_NAME), showingOnScreen()),
				new AWTEventQueueProber(timeoutMillis, 100));
	}

	public void showSniperStatus(final String status) {
		new JLabelDriver(this, named(Main.SNIPER_STATUS_NAME)).hasText(equalTo(status));
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}