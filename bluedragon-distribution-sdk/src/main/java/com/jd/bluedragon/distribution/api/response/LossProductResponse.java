package com.jd.bluedragon.distribution.api.response;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

public class LossProductResponse extends JdResponse {

	private static final long serialVersionUID = -3213961303422764479L;

	private Integer actualQuantity;
	private Integer lossQuantity;
	private Integer returnQuantity;
	private List<Product> actualProducts;
	private List<Product> lossProducts;

	public Integer getActualQuantity() {
		return this.actualQuantity;
	}

	public void setActualQuantity(Integer actualQuantity) {
		this.actualQuantity = actualQuantity;
	}

	public Integer getLossQuantity() {
		return this.lossQuantity;
	}

	public void setLossQuantity(Integer lossQuantity) {
		this.lossQuantity = lossQuantity;
	}

	public Integer getReturnQuantity() {
		return this.returnQuantity;
	}

	public void setReturnQuantity(Integer returnQuantity) {
		this.returnQuantity = returnQuantity;
	}

	public List<Product> getActualProducts() {
		return this.actualProducts;
	}

	public void setActualProducts(List<Product> actualProducts) {
		this.actualProducts = actualProducts;
	}

	public List<Product> getLossProducts() {
		return this.lossProducts;
	}

	public void setLossProducts(List<Product> lossProducts) {
		this.lossProducts = lossProducts;
	}
}