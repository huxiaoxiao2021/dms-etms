package com.jd.bluedragon.utils;

import java.math.BigDecimal;

public class BigDecimalHelper {

	private static final int DEF_DIV_SCALE = 3;

	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	public static double div(double v1, double v2) {
		return BigDecimalHelper.div(v1, v2, BigDecimalHelper.DEF_DIV_SCALE);
	}

	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static double div(BigDecimal b1, BigDecimal b2) {
		return BigDecimalHelper.div(b1, b2, BigDecimalHelper.DEF_DIV_SCALE);
	}

	public static double div(BigDecimal b1, BigDecimal b2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static BigDecimal toBigDecimal(String sNumber) {
		if(StringHelper.isEmpty(sNumber)){
			return null;
		}
		return new BigDecimal(sNumber).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

}
