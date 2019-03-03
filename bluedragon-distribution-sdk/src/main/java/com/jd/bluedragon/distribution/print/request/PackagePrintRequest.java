package com.jd.bluedragon.distribution.print.request;

import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;

/**
 * 
 * @ClassName: WaybillPrintRequest
 * @Description: 包裹标签打印请求实体
 * @author: wuyoude
 * @date: 2018年1月23日 下午10:36:18
 *
 */
public class PackagePrintRequest extends WaybillPrintRequest{
	private static final long serialVersionUID = 1L;
	/**
	 * 默认dpi-200
	 */
	private static Integer DPI_DEFAULT = 200;
    /**
     * 横向分辨率
     */
    private Integer dpiX = DPI_DEFAULT;
    /**
     * 纵向分辨率
     */
    private Integer dpiY = DPI_DEFAULT;

	/**
	 * @return the dpiX
	 */
	public Integer getDpiX() {
		return dpiX;
	}
	/**
	 * @param dpiX the dpiX to set
	 */
	public void setDpiX(Integer dpiX) {
		this.dpiX = dpiX;
	}
	/**
	 * @return the dpiY
	 */
	public Integer getDpiY() {
		return dpiY;
	}
	/**
	 * @param dpiY the dpiY to set
	 */
	public void setDpiY(Integer dpiY) {
		this.dpiY = dpiY;
	}

}
