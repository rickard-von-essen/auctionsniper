package com.example.auctionsniper;

public interface AuctionHouse {
	Auction auctionFor(Item item);

	void disconnect();
}