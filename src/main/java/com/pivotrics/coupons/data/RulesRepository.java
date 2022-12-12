package com.pivotrics.coupons.data;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RulesRepository extends CrudRepository<Rules, Long> {
	
	@Query(value = "SELECT * FROM Rules r WHERE r.issuer = ?1 and r.target_store=?2", 
			  nativeQuery = true)
	List<Rules> findRuleByIssuerAndTragetStore(String issuer, String targetStore);
	
	
	@Query(value = "SELECT * FROM Rules r WHERE r.issuer = ?1 and r.discount_type=?2", 
			  nativeQuery = true)
	Rules findRuleByIssuerAndDiscountType(String issuer, String discountType);
	
	
	
}

	