package com.example.auctionsniper.ui;

import com.example.auctionsniper.SniperSnapshot;

public enum Column {
	ITEM_IDENTIFIER("Item") {
		@Override
		public Object valueIn(final SniperSnapshot snapshot) {
			return snapshot.item.itemId;
		}
	},
	LAST_PRICE("Last Price") {
		@Override
		public Object valueIn(final SniperSnapshot snapshot) {
			return snapshot.lastPrice;
		}
	},
	LAST_BID("Last Bid") {
		@Override
		public Object valueIn(final SniperSnapshot snapshot) {
			return snapshot.lastBid;
		}
	},
	SNIPER_STATE("State") {
		@Override
		public Object valueIn(final SniperSnapshot snapshot) {
			return SnipersTableModel.textFor(snapshot.state);
		}
	};

	public final String name;

	private Column(final String name) {
		this.name = name;
	}

	public static Column at(final int columnIndex) {
		return Column.values()[columnIndex];
	}

	abstract public Object valueIn(SniperSnapshot snapshot);
}
