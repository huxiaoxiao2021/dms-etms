package com.jd.bluedragon.distribution.order.domain;

import java.math.BigDecimal;

public class OrderBankResponse {

	private BigDecimal discount;
	private BigDecimal shouldPay;

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getShouldPay() {
		return shouldPay;
	}

	public void setShouldPay(BigDecimal shouldPay) {
		this.shouldPay = shouldPay;
	}

}
