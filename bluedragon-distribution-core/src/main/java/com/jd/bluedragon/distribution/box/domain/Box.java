package com.jd.bluedragon.distribution.box.domain;

import java.util.Date;

public class Box {

	public static final String BOX_TYPE_REVERSE_LUXURY = "S"; // 奢侈品
	public static final String BOX_TYPE_REVERSE_NORMAL = "C"; // 普通物品

	public static final String BOX_TYPE_FORWARD = "B"; // 正向物流发货
	public static final String BOX_TYPE_FORWARD_RESCHEDULE = "F"; // 返调度
	public static final String BOX_TYPE_REVERSE_REJECTION = "T"; // 逆向物流拒收
	public static final String BOX_TYPE_REVERSE_AFTER_SERVICE = "G"; // 逆向物流售后
	public static final String BOX_TYPE_REVERSE_AFTER_PICKUP = "Q"; // 取件
	public static final String BOX_TYPE_DILIVERYMAN__PICKUP = "Z"; // 配送员上门接货
	public static final String BOX_TYPE_WEARHOUSE = "Y";// 亚洲一号中间仓批次号作为箱号

	public static final Integer BOX_TRANSPORT_TYPE_AIR = 1; // 航空
	public static final Integer BOX_TRANSPORT_TYPE_HIGHWAY = 2; // 公路
	public static final Integer BOX_TRANSPORT_TYPE_RAILWAY = 3; // 铁路
	public static final Integer BOX_TRANSPORT_TYPE_CITY = 4; // 同城四小时

	public static final Integer STATUS_DEFALUT = 0; // 未使用
	public static final Integer STATUS_PRINT = 1; // 已经打印
	public static final Integer BOX_STATUS_SORT = 2; // 分拣
	public static final Integer BOX_STATUS_INSPECT_PROCESSING = 3; // 验货异常差异比较中
	public static final Integer BOX_STATUS_INSPECT = 4; // 验货完成
	public static final Integer BOX_STATUS_SEND = 5; // 发货完成
	public static final Integer BOX_STATUS_DEPARTURE_PROCESSING = 6; // 发车处理中
	public static final Integer BOX_STATUS_DEPARTURE = 7; // 发车完成

	public static final String TYPE_BC = "BC"; // 正向普通箱号
	public static final String TYPE_BS = "BS"; // 正向奢侈品箱号
	public static final String TYPE_TC = "TC"; // 退货普通箱号
	public static final String TYPE_TS = "TS"; // 退货奢侈品箱号
	public static final String TYPE_GC = "GC"; // 取件普通箱号
	public static final String TYPE_GS = "GS"; // 取件奢侈品箱号

	public static final Integer TRANSPORT_TYPE_AIR = 1;//运输方式  航空

	/** 全局唯一ID */
	private Long id;

	/** 箱号 */
	private String code;

	/** 箱号集合 */
	private String codes;

	/** 箱号类型 */
	private String type;

	/** 创建站点编号 */
	private Integer createSiteCode;

	/** 创建站点名称 */
	private String createSiteName;

	/** 接收站点编号 */
	private Integer receiveSiteCode;

	/** 接收站点名称 */
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

	/** 打印次数 */
	private Integer times;

	/** 打印数量 */
	private Integer quantity;

	/** 状态 '0' 新增 '1’打印完毕 */
	private Integer status;

	private String statuses;

	/** 是否删除 '0' 删除 '1' 使用 */
	private Integer yn;

	/** 运输方式 '1' 航空运输 '2' 公路运输 '3' 铁路运输 */
	private Integer transportType;

	/**
	 * 混包类型 1混包，0非混包
	 */
	private Integer mixBoxType;

    /**
     * 站点类型
     */
    private Integer siteType;

    /**长*/
    private Float length;

    /**宽*/
    private Float width;

    /**高*/
    private Float height;

	/** 预计发货时间*/
	private Date predictSendTime ;

	/** 路由信息 站点数字ID*/
	private String router;

	/** 箱子路由信息 站点名称 */
	private String routerName;

	/** 分组箱号组名 */
	private String groupName;

	private String groupSendCode;
	/**
	 * 包裹数
	 */
	private Integer packageNum = 0;

	public String getGroupSendCode() {
		return groupSendCode;
	}

	public void setGroupSendCode(String groupSendCode) {
		this.groupSendCode = groupSendCode;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Date getPredictSendTime() {
		return predictSendTime;
	}

	public void setPredictSendTime(Date predictSendTime) {
		this.predictSendTime = predictSendTime;
	}

	public String getRouterName() {
		return routerName;
	}

	public void setRouterName(String routerName) {
		this.routerName = routerName;
	}

	public String getRouter() {
		return router;
	}

	public void setRouter(String router) {
		this.router = router;
	}

	public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Integer getMixBoxType() {
		return mixBoxType;
	}

	public void setMixBoxType(Integer mixBoxType) {
		this.mixBoxType = mixBoxType;
	}

	public Integer getTransportType() {
		return transportType;
	}

	public void setTransportType(Integer transportType) {
		this.transportType = transportType;
	}

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

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
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

	public Date getCreateTime() {
		return this.createTime == null ? null : (Date) this.createTime.clone();
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime == null ? null : (Date) createTime.clone();
	}

	public Integer getUpdateUserCode() {
		return this.updateUserCode;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return this.updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public void setUpdateUserCode(Integer updateUserCode) {
		this.updateUserCode = updateUserCode;
	}

	public Date getUpdateTime() {
		return this.updateTime == null ? null : (Date) this.updateTime.clone();
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime == null ? null : (Date) updateTime.clone();
	}

	public Integer getTimes() {
		return this.times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatuses() {
		return this.statuses;
	}

	public void setStatuses(String statuses) {
		this.statuses = statuses;
	}

	public Integer getYn() {
		return this.yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Integer getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getCodes() {
		return this.codes;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}

    public Float getLength() {
        return length;
    }

    public void setLength(Float length) {
        this.length = length;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    @Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}

		Box other = (Box) obj;
		if (this.code == null) {
			return this.code == other.code;
		}

		return this.code.equals(other.code);
	}

	@Override
	public int hashCode() {
		return 360 + this.code.hashCode();
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

}