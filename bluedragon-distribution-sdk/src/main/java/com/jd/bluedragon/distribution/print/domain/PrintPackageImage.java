package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;
/**
 * 
 * @ClassName: PrintPackageImage
 * @Description: 包裹标签打印图片信息
 * @author: wuyoude
 * @date: 2019年1月16日 下午2:47:20
 *
 */
public class PrintPackageImage implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 包裹号
	 */
	private String packageCode;
	/**
	 * 包裹标签图片的Base64字符串
	 */
	private String imageBase64;
	/**
	 * @return the packageCode
	 */
	public String getPackageCode() {
		return packageCode;
	}
	/**
	 * @param packageCode the packageCode to set
	 */
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	/**
	 * @return the imageBase64
	 */
	public String getImageBase64() {
		return imageBase64;
	}
	/**
	 * @param imageBase64 the imageBase64 to set
	 */
	public void setImageBase64(String imageBase64) {
		this.imageBase64 = imageBase64;
	}
}
