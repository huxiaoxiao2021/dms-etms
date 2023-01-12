package com.jd.bluedragon.distribution.api.request;


import com.jd.ql.dms.common.domain.JdRequest;

/**
 * Created by xumei3 on 2018/3/27.
 */
public class BoardCombinationRequest extends JdRequest {

    private static final long serialVersionUID = 1L;

    /** 板号 **/
    private String boardCode;

    /** 目的地编码 **/
    private Integer receiveSiteCode;

    /** 目的地名称 **/
    private String receiveSiteName;

    /** 箱号或包裹号 **/
    private String boxOrPackageCode;

    /** 是否强制组板 **/
    private boolean isForceCombination;

    /**
     * 错组标识: 1是错组，0是非错组
     */
    private Integer flowDisaccord;

    private Integer onlineStatus;

    /**
     * 用户erp
     */
    private String userErp;
    /**
     * 操作来源：1:pda 2:分拣机
     */
    private Integer bizSource;
    /**
     *@see com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum
     * 操作者类型编码
     */
	private Integer operatorTypeCode;
    /**
     * 操作者id
     */
	private String operatorId;    

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getBoxOrPackageCode() {
        return boxOrPackageCode;
    }

    public void setBoxOrPackageCode(String boxOrPackageCode) {
        this.boxOrPackageCode = boxOrPackageCode;
    }

    public boolean getIsForceCombination() {
        return isForceCombination;
    }

    public void setIsForceCombination(boolean isForceCombination) {
        this.isForceCombination = isForceCombination;
    }

    public Integer getFlowDisaccord() {
        return flowDisaccord;
    }

    public void setFlowDisaccord(Integer flowDisaccord) {
        this.flowDisaccord = flowDisaccord;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

	public Integer getOperatorTypeCode() {
		return operatorTypeCode;
	}

	public void setOperatorTypeCode(Integer operatorTypeCode) {
		this.operatorTypeCode = operatorTypeCode;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
}
