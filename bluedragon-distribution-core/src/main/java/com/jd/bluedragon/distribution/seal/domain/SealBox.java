package com.jd.bluedragon.distribution.seal.domain;

import java.util.Date;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.SealBoxRequest;
import com.jd.bluedragon.distribution.api.response.SealBoxResponse;

public class SealBox {

    /** 全局唯一ID */
    private Long id;

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SealBox other = (SealBox) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	/** 封箱编号 */
    private String code;

    /** 箱号 */
    private String boxCode;

    /** 创建站点编号 */
    private Integer createSiteCode;

    /** 创建站点名称 */
    private String createSiteName;

    /** 接收站点编号 */
    private Integer receiveSiteCode;

    /** 接受站点名称 */
    private String receiveSiteName;

    /** 创建人编号 */
    private Integer createUserCode;

    /** 创建人 */
    private String createUser;

    /** 创建时间 */
    private Date createTime;

    /** 最后操作人编号 */
    private Integer updateUserCode;

    /** 最后操作人 */
    private String updateUser;

    /** 最后修改时间 */
    private Date updateTime;

    /** 是否删除 '0' 删除 '1' 使用 */
    private Integer yn;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBoxCode() {
        return this.boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return this.createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getReceiveSiteCode() {
        return this.receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return this.receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getCreateUserCode() {
        return this.createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
    	return createTime!=null?(Date)createTime.clone():null;
    }

    public void setCreateTime(Date createTime) {
    	this.createTime = createTime!=null?(Date)createTime.clone():null;
    }

    public Integer getUpdateUserCode() {
        return this.updateUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
    	return updateTime!=null?(Date)updateTime.clone():null;
    }

    public void setUpdateTime(Date updateTime) {
    	this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
    }

    public Integer getYn() {
        return this.yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public static SealBox toSealBox(SealBoxRequest request) {
        SealBox sealBox = new SealBox();
        sealBox.setCreateSiteCode(request.getSiteCode());
        sealBox.setCreateSiteName(request.getSiteName());
        sealBox.setCreateUser(request.getUserName());
        sealBox.setCreateUserCode(request.getUserCode());
        sealBox.setCode(request.getSealCode());
        sealBox.setBoxCode(request.getBoxCode());
        return sealBox;
    }

    public static SealBoxResponse toSealBoxResponse(SealBox sealBox) {
        SealBoxResponse response = new SealBoxResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        response.setId(sealBox.getId());
        response.setBoxCode(sealBox.getBoxCode());
        response.setSealCode(sealBox.getCode());
        return response;
    }

    public static SealBox toSealBox2(SealBoxRequest request) {
        SealBox sealBox = new SealBox();
        sealBox.setCode(request.getSealCode());
        sealBox.setReceiveSiteCode(request.getSiteCode());
        sealBox.setReceiveSiteName(request.getSiteName());
        sealBox.setUpdateUser(request.getUserName());
        sealBox.setUpdateUserCode(request.getUserCode());
        return sealBox;
    }

}