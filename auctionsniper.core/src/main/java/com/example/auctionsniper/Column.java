package com.example.auctionsniper;

public enum Column {
	ITEM_IDENTIFIER, LAST_PRICE, LAST_BID, SNIPER_STATE;

	public static Column at(final int columnIndex) {
		return Column.values()[columnIndex];
	}
}
