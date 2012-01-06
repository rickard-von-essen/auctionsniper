package com.example.auctionsniper;

public interface Auction {

	void bid(int bid);

	void join();

	void addAuctionEventListener(AuctionEventListener auctionSniper);
}