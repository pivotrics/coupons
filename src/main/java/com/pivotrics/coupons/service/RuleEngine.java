package com.pivotrics.coupons.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pivotrics.coupons.data.DiscountType;
import com.pivotrics.coupons.data.GeneratedCoupons;
import com.pivotrics.coupons.data.GlobalConstants;
import com.pivotrics.coupons.model.CustomerDetails;
import com.pivotrics.coupons.model.Item;
import com.pivotrics.coupons.model.TransactionRequest;

public class RuleEngine {

	String category;
	String quantity;
	String gender;
	CustomerDetails customer;
	Integer noOfproducts;

	String FEMALE = "Rashmi";
	String MALE = "Venkat";

	Map<String, OfferConfig> offerConfigMap = generateOfferConfigMap();

	public OfferConfig getApplicableDiscount(TransactionRequest request, DiscountType discountType) {

		CustomerDetails customer = getCustomer(request);

		noOfproducts = getNoOfproducts(request);

		List<OfferConfig> applicalbleOffers = new ArrayList();

		switch (noOfproducts) {

		case 1:

			Item item = getSinleProductCategory(request);
			if (item == null) {
				if (customer.getFirstName().equals(FEMALE)) {
					return offerConfigMap.get("RA_GENERIC");
				} else {

				}
			}

			switch (item.getCategory()) {

			case "Cosmetics":

				if (customer.getFirstName().equals(FEMALE)) {
					if (item.getQuantity() == 1) {
						if (DiscountType.INSIDER_DISCOUNT.equals(discountType)) {
							return offerConfigMap.get("RA_1P_CC_1Q");
						} else {
							return null;
						}
					}
					if (item.getQuantity() >= 2) {
						if (DiscountType.INSIDER_DISCOUNT.equals(discountType)) {
							return offerConfigMap.get("RA_1P_CC_2Q");
						} else {
							return null;
						}
					}
				}
				
				if (customer.getFirstName().equals(MALE)) {
					if (item.getQuantity() == 1) {
						if (DiscountType.INSIDER_DISCOUNT.equals(discountType)) {
							return offerConfigMap.get("VK_1P_CC_1Q");
						} else {
							return null;
						}
					}
					if (item.getQuantity() >= 2) {
						if (DiscountType.INSIDER_DISCOUNT.equals(discountType)) {
							return offerConfigMap.get("VK_1P_CC_2Q");
						} else {
							return null;
						}
					}
				}

				break;
			case GlobalConstants.TSHIRT:

				if (item != null) {

					if (customer.getFirstName().equals(FEMALE)) {
						if (item.getQuantity() == 1) {
							if (DiscountType.INSIDER_DISCOUNT.equals(discountType)) {
								return offerConfigMap.get("RA_1P_TSC_1Q");
							} else {
								return offerConfigMap.get("RA_1P_TSC_1Q_EXT");
							}
						}
						if (item.getQuantity() >= 2) {
							if (DiscountType.INSIDER_DISCOUNT.equals(discountType)) {
								return offerConfigMap.get("RA_1P_TSC_2Q");
							} else {
								OfferConfig offer = offerConfigMap.get("RA_1P_TSC_2Q_EXT");
								return offerConfigMap.get("RA_1P_TSC_2Q_EXT");
							}
						}
					}

					if (customer.getFirstName().equals(MALE)) {
						if (item.getQuantity() == 1) {
							if (DiscountType.INSIDER_DISCOUNT.equals(discountType)) {
								return offerConfigMap.get("VK_1P_TSC_1Q");
							} else {
								return null;
							}
						}
						if (item.getQuantity() >= 2) {
							if (DiscountType.INSIDER_DISCOUNT.equals(discountType)) {
								return offerConfigMap.get("VK_1P_TSC_2Q");
							} else {

								return null;
							}
						}
					}
				}
				break;

			case GlobalConstants.HOODIES:

				return offerConfigMap.get("RA_GENERIC");

			default:
				return offerConfigMap.get("RA_GENERIC");
			}

			break;

		case 2:
			List<Item> items = getProducts(request);

			Item item1 = items.get(0);
			Item item2 = items.get(1);

			if (customer.getFirstName().equals(FEMALE)) {
				if (DiscountType.INSIDER_DISCOUNT.equals(discountType)) {
					return offerConfigMap.get("RA_2P_C_Q");
				} else {
					return offerConfigMap.get("RA_2P_C_Q_EXT");
				}
			}
			
			if (customer.getFirstName().equals(MALE)) {
				if (DiscountType.INSIDER_DISCOUNT.equals(discountType)) {
					return offerConfigMap.get("VK_2P_C_Q");
				} else {
					return null;
				}
			}

			return offerConfigMap.get("RA_GENERIC");

		default:

			if (customer.getFirstName().equals(FEMALE)) {
				if (DiscountType.INSIDER_DISCOUNT.equals(discountType)) {
					return offerConfigMap.get("RA_MAX_DICSOUNT");
				} else {
					return offerConfigMap.get("TA_MAX_DICSOUNT");
				}
			}

			return offerConfigMap.get("RA_GENERIC");
		}

		return offerConfigMap.get("RA_GENERIC");

	}

	public OfferConfig getPartnerOfferConfig(TransactionRequest request, GeneratedCoupons config) {
		if (config == null) {
			return null;
		}

		ServiceHelper serviceHelper = new ServiceHelper();

		if (request != null && request.getCustomerDetails() != null) {

			String name = request.getCustomerDetails().getFirstName();

			if (FEMALE.equals(name)) {
				String primaryText = "Hey " + name + "! You have a discount of Rs." + config.getDiscount()
						+ " off from  " + config.getIssuerStore();
				return new OfferConfig(config.getDiscount(), primaryText, "Apply to redeem the discount",
						serviceHelper.generateCouponCode(FEMALE));
			}
		}
		return null;

	}

	private List<Item> getProducts(TransactionRequest request) {
		if (request != null && request.getOrderDetails() != null && request.getOrderDetails().getItems() != null) {
			return request.getOrderDetails().getItems();
		}

		// TODO Auto-generated method stub
		return null;
	}

	private String getTargetStore(TransactionRequest request) {
		if (request.getStoreId().equals(GlobalConstants.STORE1)) {
			return GlobalConstants.STORE2;
		} else {
			return GlobalConstants.STORE1;
		}
	}

	private CustomerDetails getCustomer(TransactionRequest request) {
		CustomerDetails customerDetails = new CustomerDetails();
		if (request != null) {
			customerDetails = request.getCustomerDetails();
		}
		return customerDetails;
	}

	private Item getSinleProductCategory(TransactionRequest request) {
		Item item = new Item();
		if (request != null && request.getOrderDetails() != null && request.getOrderDetails().getItems() != null) {
			item = request.getOrderDetails().getItems().get(0);
			return item;
		}

		return item;
	}

	private Integer getNoOfproducts(TransactionRequest request) {

		if (request != null && request.getOrderDetails() != null && request.getOrderDetails().getItems() != null) {
			return request.getOrderDetails().getItems().size();
		}
		return 0;
	}

	private Map<String, OfferConfig> generateOfferConfigMap() {
		// public OfferConfig(Integer discount, String primaryText, String
		// secondaryText, String couponCode) {

		ServiceHelper serviceHelper = new ServiceHelper();

		Map<String, OfferConfig> offerConfig = new HashMap<String, OfferConfig>();

		// RA offers start-------

		offerConfig.put("RA_1P_TSC_1Q", new OfferConfig(0, getPrimaryText(FEMALE, 10, "type1"), getSecondaryText(""),
				serviceHelper.generateCouponCode(FEMALE)));

//		offerConfig.put("RA_1P_TSC_1Q", getOfferConfigDetails(FEMALE, discount));

		offerConfig.put("RA_1P_TSC_2Q", new OfferConfig(20, getPrimaryText(FEMALE, 20, "type2"),
				getSecondaryText("type1"), serviceHelper.generateCouponCode(FEMALE)));

		offerConfig.put("RA_1P_TSC_1Q_EXT", new OfferConfig(30, getPrimaryText(FEMALE, 30, "type3"),
				getSecondaryText("type1"), serviceHelper.generateCouponCode(FEMALE), getCheckoutText("type1")));

		offerConfig.put("RA_1P_TSC_2Q_EXT", new OfferConfig(30, getPrimaryText(FEMALE, 40, "type3"),
				getSecondaryText("type1"), serviceHelper.generateCouponCode(FEMALE), getCheckoutText("type1")));

		offerConfig.put("RA_2P_C_Q", new OfferConfig(60, getPrimaryText(FEMALE, 60, "type3"), getSecondaryText("type2"),
				serviceHelper.generateCouponCode(FEMALE), getCheckoutText("type1")));

		offerConfig.put("RA_2P_C_Q_EXT", new OfferConfig(50, getPrimaryText(FEMALE, 50, "type3"),
				getSecondaryText("type2"), serviceHelper.generateCouponCode(FEMALE), getCheckoutText("type1")));

		offerConfig.put("RA_1P_CC_1Q", new OfferConfig(25, getPrimaryText(FEMALE, 25, "type2"),
				getSecondaryText("type1"), serviceHelper.generateCouponCode(FEMALE)));

		offerConfig.put("RA_1P_CC_2Q", new OfferConfig(35, getPrimaryText(FEMALE, 35, "type2"),
				getSecondaryText("type1"), serviceHelper.generateCouponCode(FEMALE)));

		// RA offers ends ------

		// VK offers start ------

		offerConfig.put("VK_1P_TSC_1Q", new OfferConfig(0, getPrimaryText(MALE, 10, "type1"), getSecondaryText(""),
				serviceHelper.generateCouponCode(MALE)));

		offerConfig.put("VK_1P_TSC_2Q", new OfferConfig(20, getPrimaryText(MALE, 20, "type2"),
				getSecondaryText("type1"), serviceHelper.generateCouponCode(MALE)));

		offerConfig.put("VK_1P_TSC_1Q_EXT", null);

		offerConfig.put("VK_1P_TSC_2Q_EXT", new OfferConfig(40, getPrimaryText(MALE, 40, "type3"),
				getSecondaryText("type1"), serviceHelper.generateCouponCode(MALE), getCheckoutText("type1")));
		
		
		offerConfig.put("VK_1P_CC_1Q", new OfferConfig(10, getPrimaryText(MALE, 10, "type2"),
				getSecondaryText("type1"), serviceHelper.generateCouponCode(MALE), getCheckoutText("type1")));
		
		offerConfig.put("VK_1P_CC_2Q", new OfferConfig(20, getPrimaryText(MALE, 10, "type2"),
				getSecondaryText("type1"), serviceHelper.generateCouponCode(MALE), getCheckoutText("type1")));
		
		offerConfig.put("VK_2P_C_Q", new OfferConfig(30, getPrimaryText(MALE, 30, "type3"), getSecondaryText("type2"),
				serviceHelper.generateCouponCode(MALE), getCheckoutText("type1")));
		
		
		

		// VK offers end----

		offerConfig.put("RA_GENERIC", new OfferConfig(0, getPrimaryText(FEMALE, 0, "GENERIC"), getSecondaryText(""),
				serviceHelper.generateCouponCode(FEMALE)));

		offerConfig.put("VK_GENERIC", new OfferConfig(0, getPrimaryText(MALE, 0, "GENERIC"), getSecondaryText(""),
				serviceHelper.generateCouponCode(MALE)));
		
	
		offerConfig.put("RA_MAX_DICSOUNT", new OfferConfig(35, getPrimaryText(FEMALE, 35, "type3"),
				getSecondaryText("type1"), serviceHelper.generateCouponCode(FEMALE), getCheckoutText("type1")));

		offerConfig.put("VK_MAX_DICSOUNT", new OfferConfig(35, getPrimaryText(MALE, 35, "type3"),
				getSecondaryText("type1"), serviceHelper.generateCouponCode(MALE), getCheckoutText("type1")));

		return offerConfig;
	}

	private String getCheckoutText(String type) {
		String text = null;
		if (type.equals("type1")) {
			text = "Place the Order to get a Discount on Partner store";
		}

		return text;
	}

	private String getSecondaryText(String type) {
		String text = null;
		if (type.equals("type1")) {
			text = "Add more products to get more discount!";
		}

		return text;
	}

	private String getPrimaryText(String name, double discount, String type) {

		String text = "";
		if (type.equals("type1")) {
			text = "Hey " + name + "! Add more products to get Rs.10 off.";
		}
		if (type.equals("type2")) {
			text = "Hey " + name + "! Apply discount to get Rs." + discount + " off ";
		}

		if (type.equals("type2")) {
			text = "Hey " + name + "! Apply discount to get Rs." + discount + " off ";
		}
		if (type.equals("type4")) {
			text = "Hey " + name + "! You have a discount of Rs." + discount + " off  ";
		}

		if (type.equals("GENERIC")) {
			text = "Hey " + name + "! Add more products to get Rs.10 off. ";
		}
		return text;
	}

	private Item getProduct(TransactionRequest request) {

		Item item = new Item();
		if (request != null && request.getOrderDetails() != null && request.getOrderDetails().getItems() != null
				&& request.getOrderDetails().getItems().size() > 0) {
			item = request.getOrderDetails().getItems().get(0);
		}
		return item;
	}

	private String getCategory(TransactionRequest request) {

		return null;
	}
}
