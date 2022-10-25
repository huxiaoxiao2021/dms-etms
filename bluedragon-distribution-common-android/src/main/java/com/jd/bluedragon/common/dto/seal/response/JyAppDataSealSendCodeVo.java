package com.jd.bluedragon.common.dto.seal.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
/**
 * 封车页面保存数据
 * @author wuyoude
 *
 */
public class JyAppDataSealSendCodeVo implements Serializable{
	
	private static final long serialVersionUID = 9073687136529228574L;

	/**
	 * 运力编码
	 */
	private String sendCode;
	/**
	 * 重量
	 */
	private BigDecimal weight;
	/**
	 * 体积
	 */
	private BigDecimal volume;
	
	public String getSendCode() {
		return sendCode;
	}
	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
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
}
