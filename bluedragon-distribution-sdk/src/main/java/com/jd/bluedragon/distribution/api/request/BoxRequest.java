package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.Date;

public class BoxRequest extends JdRequest {

	private static final long serialVersionUID = 8900218370299464985L;

	/** 箱号类型 */
	private String type;

	/** 箱号编号 */
	private String boxCode;

	/** 创建站点编号 */
	private Integer createSiteCode;

	/** 创建站点名称 */
	private String createSiteName;

	/** 接收站点编号 */
	private Integer receiveSiteCode;

	/** 接收站点名称 */
	private String receiveSiteName;

	/** 数量 */
	private Integer quantity;

	/** 操作类型 '1'验货 '2'分拣 '3'发货 */
	private Integer operateType;

	/** 运输方式 '1' 航空运输 '2' 公路运输 '3' 铁路运输 */
	private Integer transportType;
	
	/** 始發站点类型 */
	private String createSiteType;
	
	/** 目的站点类型 */
	private String receiveSiteType;

	/** 1混包0非混包*/
	private Integer mixBoxType;

	/** 预计发货时间*/
	private Date predictSendTime ;

	/** 路由信息*/
	private String router;

	/** 分组箱号组名 */
	private String groupName;

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

	public String getRouter() {
		return router;
	}

	public void setRouter(String router) {
		this.router = router;
	}

	public Integer getTransportType() {
		return transportType;
	}

	public void setTransportType(Integer transportType) {
		this.transportType = transportType;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
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

	public Integer getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getOperateType() {
		return this.operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}


	public String getCreateSiteType() {
		return createSiteType;
	}

	public void setCreateSiteType(String createSiteType) {
		this.createSiteType = createSiteType;
	}

	public String getReceiveSiteType() {
		return receiveSiteType;
	}

	public void setReceiveSiteType(String receiveSiteType) {
		this.receiveSiteType = receiveSiteType;
	}

	public Integer getMixBoxType() {
		return mixBoxType;
	}

	public void setMixBoxType(Integer mixBoxType) {
		this.mixBoxType = mixBoxType;
	}

	@Override
	public String toString() {
		return "BoxRequest{" +
				"type='" + type + '\'' +
				", boxCode='" + boxCode + '\'' +
				", createSiteCode=" + createSiteCode +
				", createSiteName='" + createSiteName + '\'' +
				", receiveSiteCode=" + receiveSiteCode +
				", receiveSiteName='" + receiveSiteName + '\'' +
				", quantity=" + quantity +
				", operateType=" + operateType +
				", transportType=" + transportType +
				", createSiteType='" + createSiteType + '\'' +
				", receiveSiteType='" + receiveSiteType + '\'' +
				", mixBoxType=" + mixBoxType +
				", predictSendTime=" + predictSendTime +
				", router='" + router + '\'' +
				'}';
	}
}
