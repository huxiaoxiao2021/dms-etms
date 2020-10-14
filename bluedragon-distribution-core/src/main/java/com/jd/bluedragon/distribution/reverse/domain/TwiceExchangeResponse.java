package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;
/**
 * 二次换单信息
 * @author wuyoude
 *
 */
public class TwiceExchangeResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    /// 原运单号
    private String oldWaybillCode ;

    /// 理赔状态
    private String statusOfLP ;

    /// 物权归属
    private String goodOwner ;
    
    /// 可选退货目的地类型
    /// 
    /// 三位 000  111   第一位代表备件库 第二位代表商家 第三位代表自定义  1可选 0不可选
    /// 
    private String returnDestinationTypes;
    /**
     * 是否需要回填退货信息
     */
    private Boolean needFillReturnInfo = Boolean.FALSE;
    /**
     * 联系人（隐藏处理）
     */
    private String hideContact;
    /**
     * 联系人
     */
    private String contact;
    /**
     * 联系电话（隐藏处理）
     */
    private String hidePhone;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 地址（隐藏处理）
     */
    private String hideAddress;
    /**
     * 地址
     */
    private String address;
    
    public String getOldWaybillCode() {
        return oldWaybillCode;
    }

    public void setOldWaybillCode(String oldWaybillCode) {
        this.oldWaybillCode = oldWaybillCode;
    }

    public String getStatusOfLP() {
        return statusOfLP;
    }

    public void setStatusOfLP(String statusOfLP) {
        this.statusOfLP = statusOfLP;
    }

    public String getGoodOwner() {
        return goodOwner;
    }

    public void setGoodOwner(String goodOwner) {
        this.goodOwner = goodOwner;
    }

    public String getReturnDestinationTypes() {
        return returnDestinationTypes;
    }

    public void setReturnDestinationTypes(String returnDestinationTypes) {
        this.returnDestinationTypes = returnDestinationTypes;
    }

	public Boolean getNeedFillReturnInfo() {
		return needFillReturnInfo;
	}

	public void setNeedFillReturnInfo(Boolean needFillReturnInfo) {
		this.needFillReturnInfo = needFillReturnInfo;
	}

	public String getHideContact() {
		return hideContact;
	}

	public void setHideContact(String hideContact) {
		this.hideContact = hideContact;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getHidePhone() {
		return hidePhone;
	}

	public void setHidePhone(String hidePhone) {
		this.hidePhone = hidePhone;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getHideAddress() {
		return hideAddress;
	}

	public void setHideAddress(String hideAddress) {
		this.hideAddress = hideAddress;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
