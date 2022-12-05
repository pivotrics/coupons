package com.pivotrics.coupons.model;

import java.sql.Date;

public class RulesRequestModel {
	private String issuer;
	private String targetStore;
	private double discount;
	private Date from;
	private Date to;

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getTargetStore() {
		return targetStore;
	}

	public void setTargetStore(String targetStore) {
		this.targetStore = targetStore;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

}
