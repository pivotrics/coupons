package com.pivotrics.coupons.model;

public class CouponDetailsResponseWrapper {

	private CouponDetailsResponse insiderDiscount;
	private CouponDetailsResponse partnerDiscount;

	public CouponDetailsResponse getInsiderDiscount() {
		return insiderDiscount;
	}

	public void setInsiderDiscount(CouponDetailsResponse insiderDiscount) {
		this.insiderDiscount = insiderDiscount;
	}

	public CouponDetailsResponse getPartnerDiscount() {
		return partnerDiscount;
	}

	public void setPartnerDiscount(CouponDetailsResponse partnerDiscount) {
		this.partnerDiscount = partnerDiscount;
	}

}
