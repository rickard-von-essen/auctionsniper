package com.example.auctionsniper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class SniperSnapshot {

	public final int lastBid;
	public final int lastPrice;
	public final Item item;
	public final SniperState state;

	public SniperSnapshot(final Item item, final int lastPrice, final int lastBid, final SniperState state) {
		this.item = item;
		this.lastPrice = lastPrice;
		this.lastBid = lastBid;
		this.state = state;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public static SniperSnapshot joining(final Item item) {
		return new SniperSnapshot(item, 0, 0, SniperState.JOINING);
	}

	public SniperSnapshot winning(final int newLastPrice) {
		return new SniperSnapshot(item, newLastPrice, lastBid, SniperState.WINNING);
	}

	public SniperSnapshot bidding(final int newLastPrice, final int newLastBid) {
		return new SniperSnapshot(item, newLastPrice, newLastBid, SniperState.BIDDING);
	}

	public SniperSnapshot closed() {
		return new SniperSnapshot(item, lastPrice, lastBid, state.whenAuctionClosed());
	}

	public boolean isForSameItemAs(final SniperSnapshot sniperSnapshot) {
		return item.equals(sniperSnapshot.item);
	}

	public SniperSnapshot losing(final int lastPrice) {
		return new SniperSnapshot(item, lastPrice, lastBid, SniperState.LOSING);
	}
}
