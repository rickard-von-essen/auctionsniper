package com.example.auctionsniper;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import com.example.auctionsniper.ui.SniperCollector;

public class SniperPortfolio implements SniperCollector {

	public interface PortfolioListener extends EventListener {
		void sniperAdded(AuctionSniper sniper);
	}

	private final List<AuctionSniper> snipers = new ArrayList<AuctionSniper>();
	private final List<PortfolioListener> listeners = new ArrayList<PortfolioListener>();

	public void addPortfolioListener(final PortfolioListener listener) {
		listeners.add(listener);
	}

	@Override
	public void addSniper(final AuctionSniper sniper) {
		snipers.add(sniper);
		for (final PortfolioListener listener : listeners) {
			listener.sniperAdded(sniper);
		}
	}
}
