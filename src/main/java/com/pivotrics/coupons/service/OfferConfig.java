package com.pivotrics.coupons.service;

import java.util.HashMap;
import java.util.Map;

public class OfferConfig {

	double discount;
	String primaryText;
	String secondaryText;
	String couponCode;
	String checkoutText;
	String issuerStore;
	String targetStore;
	

	public OfferConfig() {
		
	}

	public OfferConfig(double discount, String primaryText, String secondaryText, String couponCode) {
		this.discount = discount;
		this.primaryText = primaryText;
		this.secondaryText = secondaryText;
		this.couponCode = couponCode;
	}

	public OfferConfig(double discount, String primaryText, String secondaryText, String couponCode, String checkoutText) {
		this.discount = discount;
		this.primaryText = primaryText;
		this.secondaryText = secondaryText;
		this.couponCode = couponCode;
		this.checkoutText = checkoutText;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public String getPrimaryText() {
		return primaryText;
	}

	public void setPrimaryText(String primaryText) {
		this.primaryText = primaryText;
	}

	public String getSecondaryText() {
		return secondaryText;
	}

	public void setSecondaryText(String secondaryText) {
		this.secondaryText = secondaryText;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getCheckoutText() {
		return checkoutText;
	}

	public void setCheckoutText(String checkoutText) {
		this.checkoutText = checkoutText;
	}

	public String getIssuerStore() {
		return issuerStore;
	}

	public void setIssuerStore(String issuerStore) {
		this.issuerStore = issuerStore;
	}

	public String getTargetStore() {
		return targetStore;
	}

	public void setTargetStore(String targetStore) {
		this.targetStore = targetStore;
	}
	

}
