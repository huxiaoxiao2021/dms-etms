package com.jd.bluedragon.common.dto.seal.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
/**
 * 封车页面保存数据
 * @author wuyoude
 *
 */
public class JyAppDataSealVo implements Serializable{
	
	private static final long serialVersionUID = 9073687136529228574L;
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * send_vehicle业务主键
	 */
	private String sendVehicleBizId;

	/**
	 * send_detail业务主键
	 */
	private String sendDetailBizId;

	/**
	 * 任务简码
	 */
	private String itemSimpleCode;

	/**
	 * 运力编码
	 */
	private String transportCode;

	/**
	 * 托盘
	 */
	private String palletCount;

	/**
	 * 重量
	 */
	private BigDecimal weight;

	/**
	 * 体积
	 */
	private BigDecimal volume;
    /**
     * 批次号列表
     */
    private List<String> sendCodeList;

    /**
     * 封签号列表
     */
    private List<String> sealCodeList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSendVehicleBizId() {
		return sendVehicleBizId;
	}

	public void setSendVehicleBizId(String sendVehicleBizId) {
		this.sendVehicleBizId = sendVehicleBizId;
	}

	public String getSendDetailBizId() {
		return sendDetailBizId;
	}

	public void setSendDetailBizId(String sendDetailBizId) {
		this.sendDetailBizId = sendDetailBizId;
	}

	public String getItemSimpleCode() {
		return itemSimpleCode;
	}

	public void setItemSimpleCode(String itemSimpleCode) {
		this.itemSimpleCode = itemSimpleCode;
	}

	public String getTransportCode() {
		return transportCode;
	}

	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	public String getPalletCount() {
		return palletCount;
	}

	public void setPalletCount(String palletCount) {
		this.palletCount = palletCount;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public List<String> getSendCodeList() {
		return sendCodeList;
	}

	public void setSendCodeList(List<String> sendCodeList) {
		this.sendCodeList = sendCodeList;
	}

	public List<String> getSealCodeList() {
		return sealCodeList;
	}

	public void setSealCodeList(List<String> sealCodeList) {
		this.sealCodeList = sealCodeList;
	}
}
