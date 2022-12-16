package com.pivotrics.coupons.service;

import com.pivotrics.coupons.model.TransactionRequest;

public class ServiceHelper {

	public static String generateCouponCode(TransactionRequest request) {

		String couponCode = String.valueOf((long) (Math.random() * Math.pow(10, 10)));

		if (request != null && request.getCustomerDetails() != null) {

			String name = request.getCustomerDetails().getFirstName() + request.getCustomerDetails().getLastName();
			if (name != null) {
				couponCode = name.toLowerCase() + couponCode;
			}
		}
		return couponCode;
	}
	public static String generateCouponCode(String name) {

		String couponCode = String.valueOf((long) (Math.random() * Math.pow(10, 10)));
		if (name != null) {
			couponCode = name.toLowerCase() + couponCode;
		}
		return couponCode;
	}
}




