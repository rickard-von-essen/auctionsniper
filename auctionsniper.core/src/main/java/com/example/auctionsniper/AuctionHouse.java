package com.example.auctionsniper;

public interface AuctionHouse {
	Auction auctionFor(String itemId);

	void disconnect();
}