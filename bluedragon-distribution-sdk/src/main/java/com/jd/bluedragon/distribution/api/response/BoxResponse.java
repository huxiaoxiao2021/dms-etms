package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public static final Integer CODE_BOX_ROUTER = 20106;
    public static final String MESSAGE_BOX_ROUTER = "获取路由信息异常";

    public static final Integer CODE_BC_BOX_NO_BINDING = 20107;
    public static final String MESSAGE_BC_NO_BINDING = "此箱号未绑定循环集包袋";


    /**
     * 全局唯一ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 箱号
     */
    private String boxCodes;

    /**
     * 创建站点编号
     */
    private Integer createSiteCode;

    /**
     * 创建站点名称
     */
    private String createSiteName;

    /**
     * 接收站点编号
     */
    private Integer receiveSiteCode;

    /**
     * 接收站点名称
     */
    private String receiveSiteName;

    /**
     * 箱号类型
     */
    private String type;

    /**
     * 运输方式 '1' 航空运输 '2' 公路运输 '3' 铁路运输
     */
    private Integer transportType;

    /**
     * 箱子的路由信息
     */
    private String[] routerInfo;

    /**
     * 箱子路由信息 汉字描述，超过5个站点，打印系统直接用他打印
     */
    private String routerText;

    /**
     * 箱子路由信息 站点id
     */
    private String[] routerFullId;

    /**
     * 站点类型
     **/
    private Integer siteType;

    /**
     * 预计发货时间
     */
    private Date predictSendTime;

    /**
     * 混包类型 1混包，0非混包
     */
    private Integer mixBoxType;

    /**
     * 目的道口号
     **/
    private String destinationCrossCode;

    /**
     * 目的笼车号
     **/
    private String destinationTabletrolleyCode;
    
    private Integer packageNum;

    /**
     * 始发目的站点类型
     * */
    private String createSiteType;
    private String receiveSiteType;
    /**
     * 是否众邮箱号
     * */
    private boolean whetherZyBoxCode;

    /**
     * 箱号类型
     */
    private Map<String,String> boxTypes;

    /**
     * 循环集包袋
     */
    private String materialCode;


    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }


    public Map<String, String> getBoxTypes() {
        return boxTypes;
    }

    public void setBoxTypes(Map<String, String> boxTypes) {
        this.boxTypes = boxTypes;
    }

    public String getRouterText() {
        return routerText;
    }

    public void setRouterText(String routerText) {
        this.routerText = routerText;
    }

    public Date getPredictSendTime() {
        return predictSendTime;
    }

    public void setPredictSendTime(Date predictSendTime) {
        this.predictSendTime = predictSendTime;
    }

    public String[] getRouterFullId() {
        return routerFullId;
    }

    public void setRouterFullId(String[] routerFullId) {
        this.routerFullId = routerFullId;
    }

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

    public Integer getMixBoxType() {
        return mixBoxType;
    }

    public void setMixBoxType(Integer mixBoxType) {
        this.mixBoxType = mixBoxType;
    }

    public String getDestinationCrossCode() {
        return destinationCrossCode;
    }

    public void setDestinationCrossCode(String destinationCrossCode) {
        this.destinationCrossCode = destinationCrossCode;
    }

    public String getDestinationTabletrolleyCode() {
        return destinationTabletrolleyCode;
    }

    public void setDestinationTabletrolleyCode(String destinationTabletrolleyCode) {
        this.destinationTabletrolleyCode = destinationTabletrolleyCode;
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

    public boolean isWhetherZyBoxCode() {
        return whetherZyBoxCode;
    }

    public void setWhetherZyBoxCode(boolean whetherZyBoxCode) {
        this.whetherZyBoxCode = whetherZyBoxCode;
    }
}
