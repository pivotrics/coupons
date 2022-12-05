package com.pivotrics.coupons.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pivotrics.coupons.data.CouponCodes;
import com.pivotrics.coupons.data.GeneratedCoupons;
import com.pivotrics.coupons.data.Rules;
import com.pivotrics.coupons.data.Stores;
import com.pivotrics.coupons.data.Transactions;
import com.pivotrics.coupons.model.CouponCodeRequest;
import com.pivotrics.coupons.model.CouponDetailsResponse;
import com.pivotrics.coupons.model.GetCouponCodeRequestModel;
import com.pivotrics.coupons.model.RulesRequestModel;
import com.pivotrics.coupons.model.TransactionRequest;
import com.pivotrics.coupons.service.CouponService;

@RestController
@RequestMapping("/coupons")
public class CouponsController {

	@Autowired
	private Environment env;

	@Autowired
	private CouponService couponService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/status/check")
	public String status() {

		return "Working on port " + env.getProperty("local.server.port");
	}

	@GetMapping("/getAllStores")
	public List<Stores> getAllStores() {
		logger.info("inside getAllStores ...");

		List<Stores> listStores = couponService.getAllStores();

		return listStores;
	}

	@PostMapping(value = "/order-details", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> addOrderDetails(@RequestBody TransactionRequest transactionRequest) {

		Transactions response = couponService.createTransaction(transactionRequest);
		GeneratedCoupons generatedCoupons = couponService.assignCouponToCustomer(transactionRequest);
		logger.info("inside addOrderDetails " + transactionRequest.toString());

		return ResponseEntity.status(HttpStatus.CREATED).body("order details added successfully");
	}

	@PostMapping(value = "/rules/add", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Rules> addRules(@RequestBody RulesRequestModel rulesRequestModel) {

		Rules response = couponService.addRules(rulesRequestModel);
		logger.info("rules added successfully");

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping(value = "/couponCode/add", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<List<CouponCodes>> addRules(@RequestBody CouponCodeRequest couponCodeRequest) {

		List<CouponCodes> response = couponService.addCouponCode(couponCodeRequest);
		logger.info("rules added successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping(value = "/get-coupon-code", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<CouponDetailsResponse> getCouponCode(@RequestBody GetCouponCodeRequestModel request) {


		CouponDetailsResponse response = couponService.getCouponCode(request.getStoreId(), request.customerPhoneNo);
		
		logger.info("Coupon code retrieved successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
