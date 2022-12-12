package com.pivotrics.coupons.model;

import java.util.List;

public class OrderDetails {

	private List<Item> items;
	private double total;

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

}
