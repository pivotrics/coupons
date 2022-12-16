package com.pivotrics.coupons.model;

public class CouponDetailsResponse {

	private String couponCode;
	private double discount;
	private String discountType;
	private String storeId;
	private String issuerStoreId;
	private String primaryMessage;
	private String secondaryMessage;
	private String targetStoreId;

	
	public String getTargetStoreId() {
		return targetStoreId;
	}

	public void setTargetStoreId(String targetStoreId) {
		this.targetStoreId = targetStoreId;
	}

	public String getPrimaryMessage() {
		return primaryMessage;
	}

	public void setPrimaryMessage(String primaryMessage) {
		this.primaryMessage = primaryMessage;
	}

	public String getSecondaryMessage() {
		return secondaryMessage;
	}

	public void setSecondaryMessage(String secondaryMessage) {
		this.secondaryMessage = secondaryMessage;
	}

	public String getDiscountType() {
		return discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public String getIssuerStoreId() {
		return issuerStoreId;
	}

	public void setIssuerStoreId(String issuerStoreId) {
		this.issuerStoreId = issuerStoreId;
	}

	
	

}
