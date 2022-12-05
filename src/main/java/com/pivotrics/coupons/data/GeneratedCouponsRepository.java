package com.pivotrics.coupons.data;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GeneratedCouponsRepository extends CrudRepository<GeneratedCoupons, Long> {

	@Query(value = "SELECT * FROM generated_coupons g WHERE g.customer_phone_no = ?1 and redeemed=false", nativeQuery = true)
	List<GeneratedCoupons> findByCustomerPhoneNo(String customerPhoneNo);

	@Query(value = "SELECT * FROM generated_coupons g WHERE g.coupon_code = ?1", nativeQuery = true)
	GeneratedCoupons findByCouponCode(String couponCode);

}
