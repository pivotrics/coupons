package com.pivotrics.coupons.model;

import com.pivotrics.coupons.data.CouponDetails;

public class TransactionRequest {

	private String storeId;
	private String phoneNumber;
	private OrderDetails orderDetails;
	private CouponDetails couponDetails;
	private CustomerDetails customerDetails;
	private String sessionId;
	

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public CouponDetails getCouponDetails() {
		return couponDetails;
	}

	public void setCouponDetails(CouponDetails couponDetails) {
		this.couponDetails = couponDetails;
	}

	public OrderDetails getCartDetails() {
		return orderDetails;
	}

	public void setCartDetails(OrderDetails cartDetails) {
		this.orderDetails = cartDetails;
	}
	
	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str = this.phoneNumber + this.storeId;
		return str.toString();
	}

}
