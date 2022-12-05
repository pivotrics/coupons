package com.pivotrics.coupons.data;


import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="stores")
public class Stores implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int storeId;
	private String storeName;
	private String storeUrl;
	
	
	public String getStoreName() {
		return storeName;
	}
	
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
	public String getStoreUrl() {
		return storeUrl;
	}
	
	public void setStoreUrl(String storeUrl) {
		this.storeUrl = storeUrl;
	}
}
