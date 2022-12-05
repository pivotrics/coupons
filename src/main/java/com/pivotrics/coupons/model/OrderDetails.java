package com.pivotrics.coupons.model;

import java.util.List;

public class OrderDetails {

	private List<Item> items;
	private double totalPrice;

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

}
