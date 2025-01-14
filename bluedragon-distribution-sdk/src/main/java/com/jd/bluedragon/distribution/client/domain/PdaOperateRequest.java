package com.jd.bluedragon.distribution.client.domain;

public class PdaOperateRequest {
	/**
	 * 正常情况
	 */
	public static final Integer NORMAL_TYPE = 1;
	/**
	 * 分拣类型
	 * 分拣类型是 收货站点和业务类型不能为空
	 */
	public static final Integer SORTING_TYPE = 2;
	public static final String SORTING_ERROR = "收货站点和业务类型不能为空";
	/**
	 * 操作类型
	 */
	Integer operateType = 1;
	/**
	 * 包裹号
	 */
	String packageCode;
	/**
	 * 分拣中心编码
	 */
	Integer createSiteCode;
	/**
	 * 分拣中心名称
	 */
	String createSiteName;
	/**
	 * 操作人编码
	 */
	Integer operateUserCode;
	/**
	 * 操作人名称
	 */
	String operateUserName;
	/**
	 * 操作时间
	 */
	String operateTime;
	/**
	 * 收货站点
	 */
	Integer receiveSiteCode;
	/**
	 * 业务类型
	 */
	Integer businessType;
	/**
	 * 箱号
	 */
	String boxCode;

	/**
	 * 设备编号
	 */
	String machineCode;

	/**
	 * 是否报丢 1报丢
	 */
	Integer isLoss;

	/**
	 * 是否校验验货集齐
	 */
	Integer isGather;

    /**
     * 业务操作节点
     */
    Integer operateNode;

    Integer onlineStatus;

	/**
	 * 拦截链的二进制编码（有效位编码为1）
	 */
	long interceptChainBitCode;

	boolean jyCollectPackageFlag;

    private String positionCode;

    /**
     * 网格key:关联场地网格业务主键
     */
    private String workGridKey;
    /**
     * 网格工序key:关联场地网格工序业务主键
     */
    private String workStationGridKey;

    /**
     * 操作来源
     */
    private Integer bizSource;

	public boolean getJyCollectPackageFlag() {
		return jyCollectPackageFlag;
	}

	public void setJyCollectPackageFlag(boolean jyCollectPackageFlag) {
		this.jyCollectPackageFlag = jyCollectPackageFlag;
	}

	public long getInterceptChainBitCode() {
		return interceptChainBitCode;
	}

	public void setInterceptChainBitCode(long interceptChainBitCode) {
		this.interceptChainBitCode = interceptChainBitCode;
	}

	public Integer getOperateType() {
		return operateType;
	}
	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}
	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}
	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}
	public Integer getBusinessType() {
		return businessType;
	}
	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}
	public String getBoxCode() {
		return boxCode;
	}
	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}
	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	public Integer getCreateSiteCode() {
		return createSiteCode;
	}
	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}
	public String getCreateSiteName() {
		return createSiteName;
	}
	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}
	public Integer getOperateUserCode() {
		return operateUserCode;
	}
	public void setOperateUserCode(Integer operateUserCode) {
		this.operateUserCode = operateUserCode;
	}
	public String getOperateUserName() {
		return operateUserName;
	}
	public void setOperateUserName(String operateUserName) {
		this.operateUserName = operateUserName;
	}
	public String getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public Integer getIsLoss() {
		return isLoss;
	}

	public void setIsLoss(Integer isLoss) {
		this.isLoss = isLoss;
	}

	public Integer getIsGather() {
		return isGather;
	}

	public void setIsGather(Integer isGather) {
		this.isGather = isGather;
	}

    public Integer getOperateNode() {
        return operateNode;
    }

    public void setOperateNode(Integer operateNode) {
        this.operateNode = operateNode;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getWorkGridKey() {
        return workGridKey;
    }

    public void setWorkGridKey(String workGridKey) {
        this.workGridKey = workGridKey;
    }

    public String getWorkStationGridKey() {
        return workStationGridKey;
    }

    public void setWorkStationGridKey(String workStationGridKey) {
        this.workStationGridKey = workStationGridKey;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }
}
