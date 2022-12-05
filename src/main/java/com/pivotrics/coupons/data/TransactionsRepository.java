package com.pivotrics.coupons.data;

import org.springframework.data.repository.CrudRepository;

public interface TransactionsRepository extends CrudRepository<Transactions, Long> {
	
}