package com.example.auctionsniper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Item {
	public final int stopPrice;
	public final String itemId;

	public Item(final String itemId, final int stopPrice) {
		this.itemId = itemId;
		this.stopPrice = stopPrice;
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public boolean allowsBid(final int bid) {
		return bid <= stopPrice;
	}
}