package com.jd.bluedragon.distribution.ver.domain;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;

import java.util.List;
import java.util.Map;

/**
 * @author dudong
 * @date 2016/2/29
 */
public class FilterContext {

    /**箱号*/
    private String boxCode;
    /**创建站点编号*/
    private Integer createSiteCode;
    /**收货站点编号*/
    private Integer receiveSiteCode;
    /**操作类型，正/逆向*/
    private Integer businessType;
    /**包裹号*/
    private String packageCode;
    /**运单号*/
    private String waybillCode;
    /**接收站点receiveSite的子类型SubType*/
    private String sReceiveSiteSubType;
    /**箱号的receiveSiteCode或者是receiveSiteCode*/
    private String sReceiveSiteCode;
    /**是否是自提柜订单分拣到与自提柜绑定的站点*/
    private Boolean isSelfOrderDisToSelfOrderSite;
    /**是否是便民自提订单分拣到与便民自提绑定的站点*/
    private Boolean isSelfOrderDisToSelfOrderSiteBianMin;
    /**是否是合作代收订单分拣到与合作代收绑定的站点*/
    private Boolean isSelfOrderDisToSelfOrderSiteDaiShou;
    /**是否是合作站点订单分拣到与合作站点绑定的自营站点*/
    private Boolean isPartnerOrderDisToSelfOrderSite = Boolean.FALSE;

    /**根据boxCode获取箱子*/
    private Box box;
    /**pda操作人信息*/
    private PdaOperateRequest pdaOperateRequest;
    /**根据receiveSiteCode获取收货站点,pda页面取的就是箱号目的网点*/
    private Site receiveSite;
    /**根据sReceiveSiteCode获取的站点*/
    private Site sReceiveBoxSite;
    /**根据waybillCode获取运单*/
    private WaybillCache waybillCache;
    /**根据createSiteCode获取所有规则*/
    private Map<String, Rule> ruleMap;
    /**运单预分拣站点或者null*/
    private Site waybillSite;
    /**纯提示语**/
    private List<String> tipMessages;
    /**
     * 存放当前运单的包裹数，传入包裹号时为1
     */
    private Integer packageNum;
    /**
     * 分拣操作时，箱号已分拣的包裹数
     */
    private Integer hasSortingPackageNum;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
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

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public PdaOperateRequest getPdaOperateRequest() {
        return pdaOperateRequest;
    }

    public void setPdaOperateRequest(PdaOperateRequest pdaOperateRequest) {
        this.pdaOperateRequest = pdaOperateRequest;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public Site getReceiveSite() {
        return receiveSite;
    }

    public void setReceiveSite(Site receiveSite) {
        this.receiveSite = receiveSite;
    }

    public String getsReceiveSiteSubType() {
        return sReceiveSiteSubType;
    }

    public void setsReceiveSiteSubType(String sReceiveSiteSubType) {
        this.sReceiveSiteSubType = sReceiveSiteSubType;
    }

    public String getsReceiveSiteCode() {
        return sReceiveSiteCode;
    }

    public void setsReceiveSiteCode(String sReceiveSiteCode) {
        this.sReceiveSiteCode = sReceiveSiteCode;
    }

    public Site getsReceiveBoxSite() {
        return sReceiveBoxSite;
    }

    public void setsReceiveBoxSite(Site sReceiveBoxSite) {
        this.sReceiveBoxSite = sReceiveBoxSite;
    }

    public WaybillCache getWaybillCache() {
        return waybillCache;
    }

    public void setWaybillCache(WaybillCache waybillCache) {
        this.waybillCache = waybillCache;
    }

    public Map<String, Rule> getRuleMap() {
        return ruleMap;
    }

    public void setRuleMap(Map<String, Rule> ruleMap) {
        this.ruleMap = ruleMap;
    }

    public Site getWaybillSite() {
        return waybillSite;
    }

    public void setWaybillSite(Site waybillSite) {
        this.waybillSite = waybillSite;
    }

    public Boolean getSelfOrderDisToSelfOrderSite() {
        return isSelfOrderDisToSelfOrderSite;
    }

    public void setSelfOrderDisToSelfOrderSite(Boolean selfOrderDisToSelfOrderSite) {
        isSelfOrderDisToSelfOrderSite = selfOrderDisToSelfOrderSite;
    }

    public Boolean getSelfOrderDisToSelfOrderSiteBianMin() {
        return isSelfOrderDisToSelfOrderSiteBianMin;
    }

    public void setSelfOrderDisToSelfOrderSiteBianMin(Boolean selfOrderDisToSelfOrderSiteBianMin) {
        isSelfOrderDisToSelfOrderSiteBianMin = selfOrderDisToSelfOrderSiteBianMin;
    }

    public Boolean getSelfOrderDisToSelfOrderSiteDaiShou() {
        return isSelfOrderDisToSelfOrderSiteDaiShou;
    }

    public void setSelfOrderDisToSelfOrderSiteDaiShou(Boolean selfOrderDisToSelfOrderSiteDaiShou) {
        isSelfOrderDisToSelfOrderSiteDaiShou = selfOrderDisToSelfOrderSiteDaiShou;
    }

    public Boolean getPartnerOrderDisToSelfOrderSite() {
        return isPartnerOrderDisToSelfOrderSite;
    }

    public void setPartnerOrderDisToSelfOrderSite(Boolean partnerOrderDisToSelfOrderSite) {
        isPartnerOrderDisToSelfOrderSite = partnerOrderDisToSelfOrderSite;
    }

    public List<String> getTipMessages() {
        return tipMessages;
    }

    public void setTipMessages(List<String> tipMessages) {
        this.tipMessages = tipMessages;
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
	 * @return the hasSortingPackageNum
	 */
	public Integer getHasSortingPackageNum() {
		return hasSortingPackageNum;
	}

	/**
	 * @param hasSortingPackageNum the hasSortingPackageNum to set
	 */
	public void setHasSortingPackageNum(Integer hasSortingPackageNum) {
		this.hasSortingPackageNum = hasSortingPackageNum;
	}

}
