package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @ClassName: CheckBeforeSendResponse
 * @Description: 发货前校验结果，tipMessages初始化为new ArrayList<String>()
 * @author: wuyoude
 * @date: 2019年10月18日 下午3:19:47
 *
 */
public class CheckBeforeSendResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	public CheckBeforeSendResponse(){
		this.tipMessages = new ArrayList<String>();
	}
	/**
	 * 运单类型
	 */
	private Integer waybillType;
	/**
	 * 包裹数
	 */
	private Integer packageNum;
    /**
     * 提示信息列表
     */
    private List<String> tipMessages;
	/**
	 * @return the waybillType
	 */
	public Integer getWaybillType() {
		return waybillType;
	}
	/**
	 * @param waybillType the waybillType to set
	 */
	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}
	/**
	 * @return the packageNum
	 */
	public Integer getPackageNum() {
		return packageNum;
	}
	/**
	 * @param packageNum the packageNum to set
	 */
	public void setPackageNum(Integer packageNum) {
		this.packageNum = packageNum;
	}
	/**
	 * @return the tipMessages
	 */
	public List<String> getTipMessages() {
		return tipMessages;
	}
	/**
	 * @param tipMessages the tipMessages to set
	 */
	public void setTipMessages(List<String> tipMessages) {
		this.tipMessages = tipMessages;
	}
}
