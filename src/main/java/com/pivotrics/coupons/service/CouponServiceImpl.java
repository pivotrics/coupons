package com.pivotrics.coupons.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pivotrics.coupons.data.CartDetailsRepository;
import com.pivotrics.coupons.data.CouponCodes;
import com.pivotrics.coupons.data.CouponCodesRepository;
import com.pivotrics.coupons.data.CustomerProfile;
import com.pivotrics.coupons.data.CustomerProfileRepository;
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
import com.pivotrics.coupons.model.CouponDetailsResponseWrapper;
import com.pivotrics.coupons.model.Item;
import com.pivotrics.coupons.model.RulesRequestModel;
import com.pivotrics.coupons.model.TransactionRequest;
import com.pivotrics.coupons.service.ServiceHelper;
import com.pivotrics.coupons.data.GlobalConstants;

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

	@Autowired
	CustomerProfileRepository customerProfileRepository;

	RuleEngine ruleEngine = new RuleEngine();

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
		if (request.getOrderDetails() != null) {
			orderDetails.setTotal(request.getOrderDetails().getTotal());
		}
		OrderDetails ordResult = orderDetailsRepository.save(orderDetails);

		addProductDetails(request, ordResult);

		updateCouponDetails(request, ordResult);

		return transaction;
	}

	private void addCustomerDetails(TransactionRequest request) {
		CustomerProfile result = null;
		result = customerProfileRepository.findByCustomerPhoneNo(request.getPhoneNumber());
		if (result == null) {
			if (request != null && request.getCustomerDetails() != null) {
				CustomerProfile customer = new CustomerProfile();
				customer.setFirstName(request.getCustomerDetails().getFirstName());
				customer.setLastName(request.getCustomerDetails().getLastName());
				customer.setEmail(request.getCustomerDetails().getEmail());
				customer.setPhoneNumber(request.getPhoneNumber());
				customerProfileRepository.save(customer);
			}

		}

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
		// cartDetailsRepository.save(cartDetails);
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
	public GeneratedCoupons assignCouponToCustomer(TransactionRequest request, DiscountType discounType,
			Integer transid) {

		OfferConfig offerConfig = ruleEngine.getApplicableDiscount(request, discounType);
		if (offerConfig == null) {
			return null;
		}
		GeneratedCoupons coupon = new GeneratedCoupons();
		coupon.setCustomerPhoneNo(request.getPhoneNumber());
		coupon.setIssuerStore(request.getStoreId());
		coupon.setRedeemed(false);
		coupon.setSessionId(request.getSessionId());
		coupon.setDiscountType(discounType);
		coupon.setTransId(transid);
		System.out.print(discounType);
		String couponCode = ServiceHelper.generateCouponCode(request);
		if (offerConfig != null) {
			coupon.setCouponCode(offerConfig.couponCode);
			coupon.setDiscount(offerConfig.discount);
		}
		if (discounType.equals(DiscountType.PARTNER_DISCOUNT)) {
			if (request.getStoreId().equals(GlobalConstants.STORE1)) {
				coupon.setTargetStore(GlobalConstants.STORE2);
			} else {
				coupon.setTargetStore(GlobalConstants.STORE1);
			}
		}

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
	public CouponDetailsResponseWrapper getCouponCode(TransactionRequest request) {

		CouponDetailsResponseWrapper wrapper = new CouponDetailsResponseWrapper();

		List<CouponDetailsResponse> couponDetailsList = new ArrayList();

		addCustomerDetails(request);
		String targetStore = request.getStoreId();
		String issuerStore = null;
		if (request.getStoreId().equals(GlobalConstants.STORE1)) {
			issuerStore = GlobalConstants.STORE2;
		} else {
			issuerStore = GlobalConstants.STORE1;
		}

		List<GeneratedCoupons> couponDetails = generatedCouponsRepository.findByPartnerDiscount(
				request.getPhoneNumber(), DiscountType.PARTNER_DISCOUNT.name(), issuerStore, targetStore);
		if (couponDetails != null && couponDetails.size() > 0) {
			GeneratedCoupons coupon = getPartnerStoreCoupon(couponDetails, request);

			if (coupon != null) {

				RuleEngine ruleEngine = new RuleEngine();

				OfferConfig offerConfig = ruleEngine.getPartnerOfferConfig(request, coupon);

				if (offerConfig != null) {
					coupon.setDiscount(offerConfig.discount);
					generatedCouponsRepository.save(coupon);

					CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
					couponDetailsResponse.setCouponCode(offerConfig.couponCode);
					couponDetailsResponse.setDiscount(offerConfig.discount);
					couponDetailsResponse.setDiscountType(coupon.getDiscountType().name());
					couponDetailsResponse.setStoreId(coupon.getIssuerStore());
					couponDetailsResponse.setPrimaryMessage(offerConfig.primaryText);
					couponDetailsResponse.setSecondaryMessage(offerConfig.secondaryText);
					couponDetailsResponse.setIssuerStoreId(issuerStore);
					couponDetailsResponse.setTargetStoreId(targetStore);
					wrapper.setPartnerDiscount(couponDetailsResponse);
					// couponDetailsList.add(couponDetailsResponse);

				}
			}
		}

		GeneratedCoupons internalCoupon = getInternalCoupon(request);

		if (internalCoupon != null) {

			OfferConfig offerInternal = ruleEngine.getApplicableDiscount(request, DiscountType.INSIDER_DISCOUNT);

			if (offerInternal != null) {
				internalCoupon.setDiscount(offerInternal.discount);
				generatedCouponsRepository.save(internalCoupon);
				CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
				couponDetailsResponse.setCouponCode(offerInternal.couponCode);
				couponDetailsResponse.setDiscount(offerInternal.discount);
				couponDetailsResponse.setDiscountType(internalCoupon.getDiscountType().name());
				couponDetailsResponse.setStoreId(internalCoupon.getIssuerStore());
				couponDetailsResponse.setPrimaryMessage(offerInternal.primaryText);
				couponDetailsResponse.setSecondaryMessage(offerInternal.secondaryText);
				couponDetailsResponse.setIssuerStoreId(request.getStoreId());
				couponDetailsList.add(couponDetailsResponse);
				wrapper.setInsiderDiscount(couponDetailsResponse);
			}
		}
		// TODO Auto-generated method stub
		return wrapper;
	}

//	public CouponDetailsResponse getCouponCodeOld(TransactionRequest request) {
//
//		CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
//		addCustomerDetails(request);
//		String targetStore = request.getStoreId();
//		List<GeneratedCoupons> couponDetails = generatedCouponsRepository
//				.findByExternalNCustomerPhoneNo(request.getPhoneNumber(), DiscountType.PARTNER_DISCOUNT.name());
//		if (couponDetails != null && couponDetails.size() > 0) {
//			GeneratedCoupons coupon = getPartnerStoreCoupon(couponDetails, request);
//
//			if (coupon != null) {
//				List<Rules> rules = rulesRepository.findRuleByIssuerAndTragetStore(coupon.getIssuerStore().toString(),
//						targetStore);
//				if (rules != null && rules.size() > 0) {
//					Rules rule = rules.get(0);
//					coupon.setRuleId(rule.getRuleId());
//					generatedCouponsRepository.save(coupon);
//					couponDetailsResponse.setCouponCode(coupon.getCouponCode());
//					couponDetailsResponse.setDiscount(rule.getDiscount());
//					couponDetailsResponse.setDiscountType(rule.getDiscountType().name());
//					couponDetailsResponse.setStoreId(request.getStoreId());
//					couponDetailsResponse.setIssuerStoreId(rule.getIssuer());
//				}
//			}
//		}
//
//		GeneratedCoupons internalCoupon = getInternalCoupon(request);
//
//		if (internalCoupon != null) {
//
//			Rules rule = rulesRepository.findRuleByIssuerAndDiscountType(internalCoupon.getIssuerStore().toString(),
//					DiscountType.INSIDER_DISCOUNT.name());
//			if (rule != null && rule.getDiscount() > couponDetailsResponse.getDiscount()) {
//				internalCoupon.setRuleId(rule.getRuleId());
//				generatedCouponsRepository.save(internalCoupon);
//				couponDetailsResponse.setCouponCode(internalCoupon.getCouponCode());
//				couponDetailsResponse.setDiscount(rule.getDiscount());
//				couponDetailsResponse.setDiscountType(rule.getDiscountType().name());
//				couponDetailsResponse.setStoreId(request.getStoreId());
//			}
//		}
//		// TODO Auto-generated method stub
//		return couponDetailsResponse;
//	}

	private GeneratedCoupons getPartnerStoreCoupon(List<GeneratedCoupons> couponDetails, TransactionRequest request) {
		List<GeneratedCoupons> generatedCoupons = couponDetails.stream()
				.filter(e -> !e.getIssuerStore().equals(request.getStoreId())).collect(Collectors.toList());
		// TODO Auto-generated method stub
		if (generatedCoupons != null && generatedCoupons.size() > 0) {
			return generatedCoupons.get(0);
		}
		return null;
	}

	private GeneratedCoupons getInternalCoupon(TransactionRequest request) {

		GeneratedCoupons coupon = null;

		coupon = generatedCouponsRepository.findBySessionIdDiscountType(request.getSessionId(),
				DiscountType.INSIDER_DISCOUNT.name());

		if (coupon == null) {
			coupon = assignCouponToCustomer(request, DiscountType.INSIDER_DISCOUNT, null);
		}

		// TODO Auto-generated method stub
		return coupon;
	}

}
