package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;

/**
 * @ClassName: WeightOperFlow
 * @Description: 称重操作信息
 * @author: wuyoude
 * @date: 2018年1月22日 下午10:53:40
 *
 */
public class WeightOperFlow implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 标签条码
	 */
	private String barcode;
	/**
	 * 扫描设备编码
	 */
	private String machineCode;
	/**
	 * 重量
	 */
	private double weight;
	/**
	 * 长
	 */
	private double length;
	/**
	 * 宽
	 */
	private double width;
	/**
	 * 高
	 */
	private double high;
	/**
	 * 体积
	 */
	private double volume;
	/**
	 * 设备扫描时间
	 */
	private long scannerTime;
	/**
	 * @return the barcode
	 */
	public String getBarcode() {
		return barcode;
	}
	/**
	 * @param barcode the barcode to set
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	/**
	 * @return the machineCode
	 */
	public String getMachineCode() {
		return machineCode;
	}
	/**
	 * @param machineCode the machineCode to set
	 */
	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}
	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	/**
	 * @return the length
	 */
	public double getLength() {
		return length;
	}
	/**
	 * @param length the length to set
	 */
	public void setLength(double length) {
		this.length = length;
	}
	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}
	/**
	 * @return the high
	 */
	public double getHigh() {
		return high;
	}
	/**
	 * @param high the high to set
	 */
	public void setHigh(double high) {
		this.high = high;
	}
	/**
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}
	/**
	 * @param volume the volume to set
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}
	/**
	 * @return the scannerTime
	 */
	public long getScannerTime() {
		return scannerTime;
	}
	/**
	 * @param scannerTime the scannerTime to set
	 */
	public void setScannerTime(long scannerTime) {
		this.scannerTime = scannerTime;
	}
}
