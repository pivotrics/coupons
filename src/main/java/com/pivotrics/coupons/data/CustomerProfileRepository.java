package com.pivotrics.coupons.data;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomerProfileRepository extends CrudRepository<CustomerProfile, Long> {

	@Query(value = "SELECT * FROM customer_profile cp WHERE cp.phone_number = ?1 limit 1", nativeQuery = true)
	CustomerProfile findByCustomerPhoneNo(String customerPhoneNo);
}
