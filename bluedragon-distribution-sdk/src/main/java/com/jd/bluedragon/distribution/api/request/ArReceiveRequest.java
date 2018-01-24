package com.jd.bluedragon.distribution.api.request;


public class ArReceiveRequest extends ReceiveRequest{
	private static final long serialVersionUID = 1L;
    /**
     * 摆渡车型
     */
    private Integer shuttleBusType;

    /**
     * 摆渡车牌号
     */
    private String shuttleBusNum;
    /**
     * 备注信息
     */
    private String remark;
	/**
	 * @return the shuttleBusType
	 */
	public Integer getShuttleBusType() {
		return shuttleBusType;
	}
	/**
	 * @param shuttleBusType the shuttleBusType to set
	 */
	public void setShuttleBusType(Integer shuttleBusType) {
		this.shuttleBusType = shuttleBusType;
	}
	/**
	 * @return the shuttleBusNum
	 */
	public String getShuttleBusNum() {
		return shuttleBusNum;
	}
	/**
	 * @param shuttleBusNum the shuttleBusNum to set
	 */
	public void setShuttleBusNum(String shuttleBusNum) {
		this.shuttleBusNum = shuttleBusNum;
	}
	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
