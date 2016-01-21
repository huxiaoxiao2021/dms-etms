package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import javax.print.DocFlavor;

public class BoxResponse extends JdResponse {

	private static final long serialVersionUID = 6421643159029953636L;



	public static final Integer CODE_BOX_NOT_FOUND = 20101;
	public static final String MESSAGE_BOX_NOT_FOUND = "无箱号信息";

	public static final Integer CODE_BOX_NO_PRINT = 20102;
	public static final String MESSAGE_BOX_NO_PRINT = "箱号无打印";

	public static final Integer CODE_BOX_SENDED = 20104;
	public static final String MESSAGE_BOX_SENDED = "此箱号已经发货";

    public static final Integer CODE_SITE_SENDED = 20105;
    public static final String MESSAGE_SITE_SENDED = "此箱号对应站点异常";



	/** 全局唯一ID */
	private Long id;

	/** 创建时间 */
	private String createTime;

	/** 箱号 */
	private String boxCode;

	/** 箱号 */
	private String boxCodes;

	/** 创建站点编号 */
	private Integer createSiteCode;

	/** 创建站点名称 */
	private String createSiteName;

	/** 接收站点编号 */
	private Integer receiveSiteCode;

	/** 接收站点名称 */
	private String receiveSiteName;

	private String type;

	/** 运输方式 '1' 航空运输 '2' 公路运输 '3' 铁路运输 */
	private Integer transportType;
	
	/** 箱子的路由信息 */
	private String[] routerInfo;

    /** 站点类型**/
    private Integer siteType;

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Integer getTransportType() {
		return transportType;
	}

	public void setTransportType(Integer transportType) {
		this.transportType = transportType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BoxResponse() {
		super();
	}

	public BoxResponse(Integer code, String message) {
		super(code, message);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBoxCode() {
		return this.boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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

	public String getBoxCodes() {
		return this.boxCodes;
	}

	public void setBoxCodes(String boxCodes) {
		this.boxCodes = boxCodes;
	}

	public String[] getRouterInfo() {
		return routerInfo;
	}

	public void setRouterInfo(String[] routerInfo) {
		this.routerInfo = routerInfo;
	}
	
}
