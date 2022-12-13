package com.pivotrics.coupons.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pivotrics.coupons.data.CartDetailsRepository;
import com.pivotrics.coupons.data.CouponCodes;
import com.pivotrics.coupons.data.CouponCodesRepository;
import com.pivotrics.coupons.data.DiscountType;
import com.pivotrics.coupons.data.GeneratedCoupons;
import com.pivotrics.coupons.data.GeneratedCouponsRepository;
import com.pivotrics.coupons.data.OrderDetails;
import com.pivotrics.coupons.data.OrderDetailsRepository;
import com.pivotrics.coupons.data.Products;
import com.pivotrics.coupons.data.ProductsRepository;
import com.pivotrics.coupons.data.Rules;
import com.pivotrics.coupons.data.RulesRepository;
import com.pivotrics.coupons.data.Stores;
import com.pivotrics.coupons.data.StoresRepository;
import com.pivotrics.coupons.data.Transactions;
import com.pivotrics.coupons.data.TransactionsRepository;
import com.pivotrics.coupons.model.CouponCodeRequest;
import com.pivotrics.coupons.model.CouponDetailsResponse;
import com.pivotrics.coupons.model.Item;
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

	@Autowired
	CartDetailsRepository cartDetailsRepository;

	@Autowired
	ProductsRepository productsRepository;

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
		// transaction.setCreatedOn(LocalDateTime.now());
		Transactions result = transactionRepository.save(transaction);

		int transId = result.getTransId();
		OrderDetails orderDetails = new OrderDetails();
		orderDetails.setTransId(transId);
		orderDetails.setOrderId(request.getOrderId());
		orderDetails.setOrderStatus(request.getOrderStatus());
		OrderDetails ordResult = orderDetailsRepository.save(orderDetails);

		addProductDetails(request, ordResult);

		updateCouponDetails(request, ordResult);

		return transaction;
	}

	private void addProductDetails(TransactionRequest request, OrderDetails ordResult) {
//		CartDetails cartDetails = new CartDetails();
//
//		cartDetails.setOrderId(null);
//		cartDetails.setTotal(request.getOrderDetails().getTotal());
		List<Products> products = new ArrayList();

		if (request != null && request.getOrderDetails() != null && request.getOrderDetails().getItems() != null
				&& !request.getOrderDetails().getItems().isEmpty()) {
			products = request.getOrderDetails().getItems().stream().map(e -> createProduct(e, ordResult))
					.collect(Collectors.toList());

		}
//		cartDetails.setProducts(products);
	//	cartDetailsRepository.save(cartDetails);
	}

	private Products createProduct(Item item, OrderDetails ordResult) {
		Products product = new Products();
		if (item != null) {
			product.setDiscount(item.getDiscount());
			product.setPrice(item.getPrice());
			product.setProductId(item.getProductId());
			product.setProductName(item.getProductName());
			product.setQuantity(item.getQuantity());
			product.setOrderId(ordResult.getOrderId());
			productsRepository.save(product);
		}
		return product;
	}

	private void updateCouponDetails(TransactionRequest request, OrderDetails ordResult) {

		if (ordResult != null && request.getCouponDetails() != null
				&& request.getCouponDetails().getCouponCode() != null && request.getCouponDetails().isApplied()) {
			GeneratedCoupons couponDetails = generatedCouponsRepository
					.findByCouponCode(request.getCouponDetails().getCouponCode());
			if (couponDetails != null) {
				couponDetails.setRedeemed(true);
				couponDetails.setTransId(ordResult.getTransId());
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
	public GeneratedCoupons assignCouponToCustomer(TransactionRequest request, DiscountType discounType, Integer transid) {
		GeneratedCoupons coupon = new GeneratedCoupons();
		coupon.setCustomerPhoneNo(request.getPhoneNumber());
		coupon.setIssuerStore(request.getStoreId());
		coupon.setRedeemed(false);
		coupon.setSessionId(request.getSessionId());
		coupon.setDiscountType(discounType);
		coupon.setTransId(transid);
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
				.findByExternalNCustomerPhoneNo(request.getPhoneNumber(), DiscountType.PARTNER_DISCOUNT.name());
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
				couponDetailsResponse.setDiscountType(rule.getDiscountType().name());
				couponDetailsResponse.setStoreId(request.getStoreId());
			}
		}

		GeneratedCoupons internalCoupon = getInternalCoupon(request);

		if (internalCoupon != null) {

			Rules rule = rulesRepository.findRuleByIssuerAndDiscountType(internalCoupon.getIssuerStore().toString(),
					DiscountType.INSIDER_DISCOUNT.name());
			if (rule != null && rule.getDiscount() > couponDetailsResponse.getDiscount()) {
				internalCoupon.setRuleId(rule.getRuleId());
				generatedCouponsRepository.save(internalCoupon);
				couponDetailsResponse.setCouponCode(internalCoupon.getCouponCode());
				couponDetailsResponse.setDiscount(rule.getDiscount());
				couponDetailsResponse.setDiscountType(rule.getDiscountType().name());
				couponDetailsResponse.setStoreId(request.getStoreId());
			}
		}
		// TODO Auto-generated method stub
		return couponDetailsResponse;
	}

	private GeneratedCoupons getInternalCoupon(TransactionRequest request) {

		GeneratedCoupons coupon = null;

		coupon = generatedCouponsRepository.findBySessionId(request.getSessionId());

		if (coupon == null) {
			coupon = assignCouponToCustomer(request, DiscountType.INSIDER_DISCOUNT, null);
		}

		// TODO Auto-generated method stub
		return coupon;
	}

}
