package com.jd.bluedragon.distribution.api.request;


/**
 * 原包发货对象
 * Created by wangtingwei on 2015/3/11.
 */
public class PackageSendRequest extends DeliveryRequest {

    private static final long serialVersionUID = 8464241458768408318L;

    /**
     * 是否强制发货
     */
    private boolean isForceSend;

    /**
     * 是否取消上次发货，false - 不取消（默认），true - 取消上次发货
     */
    private Boolean isCancelLastSend;

    /**
     * 是否发送整板
     */
    private Integer sendForWholeBoard;

    /**
     * 业务来源
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

    public boolean getIsForceSend() {
        return isForceSend;
    }

    public void setIsForceSend(boolean isForceSend) {
        this.isForceSend = isForceSend;
    }

    public Boolean getIsCancelLastSend() {
        return isCancelLastSend;
    }

    public void setIsCancelLastSend(Boolean cancelLastSend) {
        isCancelLastSend = cancelLastSend;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

    public Integer getSendForWholeBoard() {
        return sendForWholeBoard;
    }

    public PackageSendRequest setSendForWholeBoard(Integer sendForWholeBoard) {
        this.sendForWholeBoard = sendForWholeBoard;
        return this;
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
