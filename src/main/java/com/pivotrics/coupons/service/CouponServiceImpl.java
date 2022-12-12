package com.pivotrics.coupons.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pivotrics.coupons.data.CouponCodes;
import com.pivotrics.coupons.data.CouponCodesRepository;
import com.pivotrics.coupons.data.DiscountType;
import com.pivotrics.coupons.data.GeneratedCoupons;
import com.pivotrics.coupons.data.GeneratedCouponsRepository;
import com.pivotrics.coupons.data.OrderDetails;
import com.pivotrics.coupons.data.OrderDetailsRepository;
import com.pivotrics.coupons.data.Rules;
import com.pivotrics.coupons.data.RulesRepository;
import com.pivotrics.coupons.data.Stores;
import com.pivotrics.coupons.data.StoresRepository;
import com.pivotrics.coupons.data.Transactions;
import com.pivotrics.coupons.data.TransactionsRepository;
import com.pivotrics.coupons.model.CouponCodeRequest;
import com.pivotrics.coupons.model.CouponDetailsResponse;
import com.pivotrics.coupons.model.GetCouponCodeRequestModel;
import com.pivotrics.coupons.model.RulesRequestModel;
import com.pivotrics.coupons.model.TransactionRequest;

@Service
public class CouponServiceImpl implements CouponService {

	@Autowired
	StoresRepository storesRepository;

	@Autowired
	TransactionsRepository transactionRepository;

	@Autowired
	OrderDetailsRepository orderDetailsRepository;

	@Autowired
	RulesRepository rulesRepository;

	@Autowired
	CouponCodesRepository couponCodeRepository;

	@Autowired
	GeneratedCouponsRepository generatedCouponsRepository;

	@Override
	public List<Stores> getAllStores() {

		List<Stores> storeList = (List<Stores>) storesRepository.findAll();

		return storeList;
	}

	@Override
	public Transactions createTransaction(TransactionRequest request) {

		Transactions transaction = new Transactions();
		transaction.setStoreId(request.getStoreId());
		transaction.setPhoneNo(request.getPhoneNumber());
		Transactions result = transactionRepository.save(transaction);

		int transId = result.getTransId();
		OrderDetails orderDetails = new OrderDetails();
		orderDetails.setTransId(transId);
		OrderDetails ordResult = orderDetailsRepository.save(orderDetails);
		updateCouponDetails(request);

		return transaction;
	}

	private void updateCouponDetails(TransactionRequest request) {

		if (request.getCouponDetails() != null && request.getCouponDetails().getCouponCode() != null
				&& request.getCouponDetails().isApplied()) {
			GeneratedCoupons couponDetails = generatedCouponsRepository
					.findByCouponCode(request.getCouponDetails().getCouponCode());
			if (couponDetails != null) {
				couponDetails.setRedeemed(true);
				generatedCouponsRepository.save(couponDetails);
			}
		}
	}

	@Override
		public Rules addRules(RulesRequestModel request) {

		Rules rules = new Rules();
		rules.setIssuer(request.getIssuer());
		rules.setTargetStore(request.getTargetStore());
		rules.setDiscount(request.getDiscount());
		Rules result = rulesRepository.save(rules);
		return result;
	}

	@Override
	public GeneratedCoupons assignCouponToCustomer(TransactionRequest request, DiscountType discounType ) {
		GeneratedCoupons coupon = new GeneratedCoupons();
		coupon.setCustomerPhoneNo(request.getPhoneNumber());
		coupon.setIssuerStore(request.getStoreId());
		coupon.setRedeemed(false);
		coupon.setSessionId(request.getSessionId());
		coupon.setDiscountType(discounType);
		System.out.print(discounType);
		String couponCode = String.valueOf((long) (Math.random() * Math.pow(10, 10)));
		coupon.setCouponCode(couponCode.toString().toUpperCase());
		GeneratedCoupons response = generatedCouponsRepository.save(coupon);
		return response;

	}

	@Override
	public List<CouponCodes> addCouponCode(CouponCodeRequest request) {
		List<CouponCodes> couponCodes = request.getCouponCodes().stream().map(e -> new CouponCodes(e, true))
				.collect(Collectors.toList());
		couponCodeRepository.saveAll(couponCodes);
		return couponCodes;
	}

	@Override
	public CouponDetailsResponse getCouponCode(TransactionRequest request) {

		CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
		String targetStore = request.getStoreId();
		List<GeneratedCoupons> couponDetails = generatedCouponsRepository
				.findByCustomerPhoneNo(request.getPhoneNumber());
		if (couponDetails != null && couponDetails.size() > 0) {
			GeneratedCoupons coupon = couponDetails.get(0);

			List<Rules> rules = rulesRepository.findRuleByIssuerAndTragetStore(coupon.getIssuerStore().toString(),
					targetStore);
			if (rules != null && rules.size() > 0) {
				Rules rule = rules.get(0);
				coupon.setRuleId(rule.getRuleId());
				generatedCouponsRepository.save(coupon);
				couponDetailsResponse.setCouponCode(coupon.getCouponCode());
				couponDetailsResponse.setDiscount(rule.getDiscount());
			}
		}
		
		GeneratedCoupons internalCoupon = getInternalCoupon(request);
		
		if(internalCoupon != null) {
			
			Rules rule = rulesRepository.findRuleByIssuerAndDiscountType(internalCoupon.getIssuerStore().toString(),
					DiscountType.INTERNAL_LOYALTY.name());
			if (rule != null && rule.getDiscount() > couponDetailsResponse.getDiscount()  ) {
				internalCoupon.setRuleId(rule.getRuleId());
				generatedCouponsRepository.save(internalCoupon);
				couponDetailsResponse.setCouponCode(internalCoupon.getCouponCode());
				couponDetailsResponse.setDiscount(rule.getDiscount());
			}
		}
		// TODO Auto-generated method stub
		return couponDetailsResponse;
	}

	private GeneratedCoupons getInternalCoupon(TransactionRequest request) {
		
		GeneratedCoupons coupon = null;
		
		coupon = generatedCouponsRepository.findBySessionId(request.getSessionId());
		
		if(coupon == null) {
			coupon =  assignCouponToCustomer(request, DiscountType.INTERNAL_LOYALTY);		  	
		} 
		
		// TODO Auto-generated method stub
		return coupon;
	}

}
