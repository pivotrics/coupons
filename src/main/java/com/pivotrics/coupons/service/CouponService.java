package com.pivotrics.coupons.service;

import java.util.List;

import com.pivotrics.coupons.data.CouponCodes;
import com.pivotrics.coupons.data.DiscountType;
import com.pivotrics.coupons.data.GeneratedCoupons;
import com.pivotrics.coupons.data.Rules;
import com.pivotrics.coupons.data.Stores;
import com.pivotrics.coupons.data.Transactions;
import com.pivotrics.coupons.model.CouponCodeRequest;
import com.pivotrics.coupons.model.CouponDetailsResponse;
import com.pivotrics.coupons.model.CouponDetailsResponseWrapper;
import com.pivotrics.coupons.model.GetCouponCodeRequestModel;
import com.pivotrics.coupons.model.RulesRequestModel;
import com.pivotrics.coupons.model.TransactionRequest;

public interface CouponService {

	List<Stores> getAllStores();

	Transactions createTransaction(TransactionRequest request);

	Rules addRules(RulesRequestModel request);

	GeneratedCoupons assignCouponToCustomer(TransactionRequest request, DiscountType discounType, Integer transId);

	List<CouponCodes> addCouponCode(CouponCodeRequest request);

	CouponDetailsResponseWrapper getCouponCode(TransactionRequest request);
}
