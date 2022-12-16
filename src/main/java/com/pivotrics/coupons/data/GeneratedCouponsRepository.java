package com.pivotrics.coupons.data;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GeneratedCouponsRepository extends CrudRepository<GeneratedCoupons, Long> {

	@Query(value = "SELECT * FROM generated_coupons g WHERE g.customer_phone_no = ?1 and redeemed=false", nativeQuery = true)
	List<GeneratedCoupons> findByCustomerPhoneNo(String customerPhoneNo);
	
	@Query(value = "SELECT * FROM generated_coupons g WHERE g.customer_phone_no = ?1 and g.redeemed=false  and g.discount_type = ?2", nativeQuery = true)
	List<GeneratedCoupons> findByExternalNCustomerPhoneNo(String customerPhoneNo, String discountType);

	@Query(value = "SELECT * FROM generated_coupons g WHERE g.customer_phone_no = ?1 and g.redeemed=false  and g.discount_type = ?2 and g.issuer_store = ?3 and g.target_store = ?4", nativeQuery = true)
	List<GeneratedCoupons> findByPartnerDiscount(String customerPhoneNo, String discountType, String issuerStore, String targetStore);
	
	@Query(value = "SELECT * FROM generated_coupons g WHERE g.coupon_code = ?1", nativeQuery = true)
	GeneratedCoupons findByCouponCode(String couponCode);

	@Query(value = "SELECT * FROM generated_coupons g WHERE g.session_id = ?1", nativeQuery = true)
	GeneratedCoupons findBySessionId(String sessionId);
	
	@Query(value = "SELECT * FROM generated_coupons g WHERE g.session_id = ?1 and g.discount_type = ?2", nativeQuery = true)
	GeneratedCoupons findBySessionIdDiscountType(String sessionId,  String discountType);

	@Query(value = "SELECT * FROM generated_coupons g WHERE g.session_id = ?1 && g.discount_type = ?2", nativeQuery = true)
	GeneratedCoupons findByInternalLoyaty(String sessionId, DiscountType discountType);

}
