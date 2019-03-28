package com.jd.bluedragon.distribution.print.request;

import java.util.List;

import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;

/**
 * 
 * @ClassName: BatchPackagePrintRequest
 * @Description: 批量包裹标签打印
 * @author: wuyoude
 * @date: 2019年3月4日 上午11:22:30
 *
 */
public class BatchPackagePrintRequest extends WaybillPrintRequest{
	private static final long serialVersionUID = 1L;
	/**
	 * 打印单号列表
	 */
	private List<String> barCodeList;
	/**
	 * @return the barCodeList
	 */
	public List<String> getBarCodeList() {
		return barCodeList;
	}
	/**
	 * @param barCodeList the barCodeList to set
	 */
	public void setBarCodeList(List<String> barCodeList) {
		this.barCodeList = barCodeList;
	}
	
}
