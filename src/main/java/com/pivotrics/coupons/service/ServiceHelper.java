package com.pivotrics.coupons.service;

import com.pivotrics.coupons.model.TransactionRequest;

public class ServiceHelper {

	public static String generateCouponCode(TransactionRequest request) {

		String couponCode = String.valueOf((long) (Math.random() * Math.pow(10, 10)));

		if (request != null && request.getCustomerDetails() != null) {

			String name = request.getCustomerDetails().getFirstName() + request.getCustomerDetails().getLastName();
			if (name != null) {
				couponCode = name.toUpperCase() + couponCode;
			}
		}
		return couponCode;
	}
}




