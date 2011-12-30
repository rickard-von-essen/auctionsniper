package com.example.auctionsniper;

public interface SniperListener {

	void sniperLost();

	void sniperWon();

	void sniperStateChanged(SniperSnapshot sniperSnapshot);
}
