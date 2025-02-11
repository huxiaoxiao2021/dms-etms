package com.jd.bluedragon.utils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SiteHelper {

    private static final Logger log = LoggerFactory.getLogger(SiteHelper.class);

    public static final int SORT_TYPE_SORTING_CENTER;
    public static final int SORT_SUBTYPE_SORTING_CENTER;
    public static final List<Integer> SORT_THIRD_TYPE_SORTING_CENTER = new ArrayList<>();

    static {
        SORT_TYPE_SORTING_CENTER = Integer.valueOf(PropertiesHelper.newInstance().getValue("basic.sortType.sortingCenter"));
        SORT_SUBTYPE_SORTING_CENTER = Integer.valueOf(PropertiesHelper.newInstance().getValue("basic.sortSubType.sortingCenter"));
        String thirdTypes = PropertiesHelper.newInstance().getValue("basic.sortThirdType.sortingCenter");

        if (StringUtils.isNotBlank(thirdTypes)) {
            try {
                String[] list = thirdTypes.split(Constants.SEPARATOR_COMMA);
                for (String thirdType : list) {
                    SORT_THIRD_TYPE_SORTING_CENTER.add(Integer.valueOf(thirdType));
                }
            } catch (Exception e) {
                log.error("读取分拣中心三级类型配置失败.", e);
            }
        }
    }

    public static Boolean isPartner(Site site) {
        if (site == null || site.getType() == null) {
            return Boolean.FALSE;
        }

        if (16 == site.getType().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断站点是否自提点
     * @param site
     * @return
     */
    public static Boolean isPickup(Site site) {
        if (site == null || site.getType() == null) {
            return Boolean.FALSE;
        }

        if (8 == site.getType().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 如果站点子类型是16，则返回true
     * @param site
     * @return
     */
    public static Boolean isPartnerBySiteSubType(Site site) {
        if (site == null || site.getType() == null) {
            return Boolean.FALSE;
        }

        if (16 == site.getSubType().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isDistributionCenter(Site site) {
        if (site == null || site.getType() == null) {
            return Boolean.FALSE;
        }

        if (64 == site.getType().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
	
	public static Boolean isZhengZhouDistributionCenter(Site site) {
		if (site == null || site.getType() == null) {
			return Boolean.FALSE;
		}
		
		if (1792 == site.getCode().intValue()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

    public static Boolean isDelivery(Site site) {
        if (site == null || site.getType() == null) {
            return Boolean.FALSE;
        }

        if (4 == site.getType().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 是否为三方-合作站点
     * @param site
     * @return
     */
    public static Boolean isThreePartner(Site site) {
        if (site == null || site.getType() == null || site.getSubType() == null) {
            return Boolean.FALSE;
        }

        if (16 == site.getType().intValue() && 22 == site.getSubType().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    /**
     * 是否为三方-校园派
     * @param site
     * @return
     */
    public static Boolean isSchoolyard(Site site) {
        if (site == null || site.getType() == null || site.getSubType() == null) {
            return Boolean.FALSE;
        }

        if (16 == site.getType().intValue() && 128 == site.getSubType().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 是否为爱回收
     * 16-1604
     * @param site
     * @return
     */
    public static Boolean isRecovery(Site site) {
        if (site == null || site.getType() == null || site.getSubType() == null) {
            return Boolean.FALSE;
        }

        if (16 == site.getType().intValue() && 1604 == site.getSubType().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断是否是加盟商站点
     * @return
     */
    public static boolean isAllianceBusiSite(Site site) {
        if (site == null || site.getType() == null || site.getSubType() == null) {
            return Boolean.FALSE;
        }
        return site.getType() == 16 && site.getSubType() == 88;
    }

    /**
     * 判断是否是乡镇共配站
     * 16-1605
     */
    public static Boolean isRuralSite(Site site) {
        if (site == null || site.getType() == null || site.getSubType() == null) {
            return Boolean.FALSE;
        }

        if (site.getType() == 16 && site.getSubType() == 1605) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断是否是自提点
     * 8
     */
    public static Boolean isSelfSite(Site site) {
        return site != null && site.getType() != null && site.getType() == 8;
    }

    /**
     * 判断是否是营业部
     * 4-4
     */
    public static Boolean isSalesDeptSite(Site site) {

        if (site == null || site.getType() == null || site.getSubType() == null) {
            return Boolean.FALSE;
        }

        if (site.getType() == 4 && site.getSubType() == 4) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断是否是自营营业部
     * 4-4-1
     */
    public static Boolean isSelfSalesDeptSite(Site site) {
        return Objects.equals(site.getType(), 4) 
                && Objects.equals(site.getSubType(), 4)
                && Objects.equals(site.getThirdType(), 1);
    }

    /**
     * 可能存在所属站的情况，调用基础资料basicSiteQueryWS.getSiteExtensionBySiteId接口获取所属站信息
     */
    public static Boolean isMayBelongSiteExist(Site site) {
        return SiteHelper.isSelfSite(site)
                || SiteHelper.isThreePartner(site)
                || SiteHelper.isSchoolyard(site)
                || SiteHelper.isRecovery(site)
                || SiteHelper.isAllianceBusiSite(site)
                || SiteHelper.isRuralSite(site)
                || SiteHelper.isSalesDeptSite(site);
    }

    /**
     * 判断是否是经济网
     * @param site
     * @return
     */
    public static  Boolean isEconomicNet(BaseStaffSiteOrgDto site){
        if (site == null || site.getSiteType() == null || site.getSubType() == null) {
            return Boolean.FALSE;
        }
        if (site.getSiteType().equals(10000)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean isForward(String ruleSiteType, String receiveSiteType) {
        return SiteHelper.matchSiteTypeRule(ruleSiteType, receiveSiteType);
    }

    public static Boolean isReverse(String ruleSiteType, String receiveSiteType) {
        return SiteHelper.matchSiteTypeRule(ruleSiteType, receiveSiteType);
    }

    public static Boolean isWarehouse(String ruleSiteType, String receiveSiteType) {
        return SiteHelper.matchSiteTypeRule(ruleSiteType, receiveSiteType);
    }

    public static Boolean isAfterSale(String ruleSiteType, String receiveSiteType) {
        return SiteHelper.matchSiteTypeRule(ruleSiteType, receiveSiteType);
    }

    public static Boolean matchSiteTypeRule(String ruleSiteType, String receiveSiteType) {
        if (ruleSiteType == null || receiveSiteType == null) {
            return Boolean.FALSE;
        }

        String[] ruleSiteTypeArray = ruleSiteType.split(Constants.SEPARATOR_COMMA);
        Arrays.sort(ruleSiteTypeArray);
        if (-1 < Arrays.binarySearch(ruleSiteTypeArray, receiveSiteType)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean matchSiteRule(String ruleSite, String receiveSite) {
        if (ruleSite == null || receiveSite == null) {
            return Boolean.FALSE;
        }

        String[] ruleSiteArray = ruleSite.split(Constants.SEPARATOR_COMMA);
        Arrays.sort(ruleSiteArray);
        if (-1 < Arrays.binarySearch(ruleSiteArray, receiveSite)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 是否速递中心，速递中心和配送站一样，Type=4
     * @param site
     * @return
     */
    public static Boolean isFastStation(Site site){
    	 if (site == null) {
             return Boolean.FALSE;
         }
        if(site.getType() == null){
            return Boolean.FALSE;
        }

         if (4 == site.getType().intValue()) {
             return Boolean.TRUE;
         }
         return Boolean.FALSE;
    }
    
    
    /**
     * 是否库房，固定类型900
     * @param site
     * @return
     */
    public static Boolean isStoreHouse(Site site){
	   	 if (site == null || site.getType() == null) {
	         return Boolean.FALSE;
	     }
	
	     if (900 == site.getType().intValue()) {
	         return Boolean.TRUE;
	     }
	     return Boolean.FALSE;    	
    }
    
    /**
     * 是否自提柜站点
     * @param site
     * @return
     */
    public static Boolean isSelfD(Site site){
	   	 if (site == null || site.getType() == null) {
	         return Boolean.FALSE;
	     }
	
	     if (28 == site.getSubType().intValue()) {
	         return Boolean.TRUE;
	     }
	     return Boolean.FALSE;    	
    }
    
    
    /**
     *  判断自提柜的归属站点是否和预分拣站点匹配
     *  @param belongSiteCode 自提柜所属站点
     *  @param receiveSiteCode 预分拣站点
     *  @return 如果匹配返回TRUE,否则返回FALSE
     * */
    public static Boolean isMatchOfBoxBelongSiteAndReceivedSite(Integer belongSiteCode, String receiveSiteCode){
    	if(belongSiteCode == null || receiveSiteCode == null){
    		return Boolean.FALSE;
    	}
    	
    	if(String.valueOf(belongSiteCode).trim().equals(receiveSiteCode.trim())){
    		return Boolean.TRUE;
    	}
    	
    	return Boolean.FALSE;
    }

    /**
     * 判断站点是否开通自提业务(京配-自提或者自提)
     *
     *
     * @param site 站点
     * @return 开通自提业务返回 True，否则返回False
     * */
    public static Boolean hasPickupBusiness(Site site){
        if(null == site || null == site.getSiteBusinessType()){
            return false;
        }

        if(Site.BUSINESS_TYPE_JP_ZT.equals(site.getSiteBusinessType())
                || Site.BUSINESS_TYPE_ZT.equals(site.getSiteBusinessType())
                || Site.BUSINESS_TYPE_JP_ZT_SHENGXIAN_ZT.equals(site.getSiteBusinessType())
                || Site.BUSINESS_TYPE_ZT_SHENGXIAN_ZT.equals(site.getSiteBusinessType())
                || Site.BUSINESS_TYPE_ZT_NJP.equals(site.getSiteBusinessType())){
            return true;
        }

        return false;
    }
    
    /**
     * 判断站点是只有自提业务(自提或者生鲜自提)
     *
     *
     * @param site 站点
     * @return 开通自提业务返回 True，否则返回False
     * */
    public static Boolean hasOnlyPickupBusiness(Site site){
        if(null == site || null == site.getSiteBusinessType()){
            return false;
        }

        if(Site.BUSINESS_TYPE_ZT.equals(site.getSiteBusinessType())
                || Site.BUSINESS_TYPE_ZT_SHENGXIAN_ZT.equals(site.getSiteBusinessType())){
            return true;
        }

        return false;
    }
    
    /**
     * 判断站点是否开通自提业务(京配-自提或者自提)
     *
     *
     * @param site 站点
     * @return 开通自提业务返回 True，否则返回False
     * */
    public static Boolean hasShengXianPickupBusiness(Site site){
        if(null == site || null == site.getSiteBusinessType()){
            return false;
        }

        if(Site.BUSINESS_TYPE_JP_ZT_SHENGXIAN_ZT.equals(site.getSiteBusinessType())
                || Site.BUSINESS_TYPE_ZT_SHENGXIAN_ZT.equals(site.getSiteBusinessType())){
            return true;
        }

        return false;
    }

    /**
     * 是否备件库，固定类型903
     * @param site
     * @return
     */
    public static Boolean isSpsHouse(Site site){
        if (site == null || site.getType() == null) {
            return Boolean.FALSE;
        }

        if (903 == site.getType().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    /**
     * 三级分类为：大家电综合站点
     * @param site
     * @return
     */
    public static Boolean isBigElectricApplianceSite(Site site){
        if (null == site || null == site.getType() || null == site.getSubType()) {
            return Boolean.FALSE;
        }

        if (18 == site.getSubType().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断是分拣中心
     * 依据青龙基础资料 sortType, sortSubType, sortThirdType判断
     * @param site
     * @return
     */
    public static Boolean siteIsSortingCenter(BaseSiteInfoDto site) {
        if (null == site
                || null == site.getSortType()
                || null == site.getSortSubType()
                || null == site.getSortThirdType()) {
            return Boolean.FALSE;
        }

        return Objects.equals(SORT_TYPE_SORTING_CENTER, site.getSortType())
                && Objects.equals(SORT_SUBTYPE_SORTING_CENTER, site.getSortSubType())
                && SORT_THIRD_TYPE_SORTING_CENTER.contains(site.getSortThirdType());
    }

    /**
     * 是否为分拣中转站
     */
    public static boolean isSortTransferSite(Site site) {
        return site != null && Objects.equals(site.getSortType(), Constants.SORTING_SORT_TYPE) 
                && Objects.equals(site.getSortSubType(), Constants.SORTING_SORT_SUBTYPE) 
                && Objects.equals(site.getSortThirdType(), Constants.SORTING_SORT_THIRD_TYPE);
    }    

    /**
     * 新版判断是分拣中心
     *  
     * @param site
     * @return
     */
    public static Boolean isSortingCenter(Site site){
        if (null == site || null == site.getSortType()) {
            return Boolean.FALSE;
        }

        return Objects.equals(site.getSortType(), Constants.SORTING_SORT_TYPE);
    }

    /**
     * 新版判断是接货仓
     * 
     * @param site
     * @return
     */
    public static Boolean isReceiveWms(Site site){
        if (null == site || null == site.getSortType()) {
            return Boolean.FALSE;
        }

        return Objects.equals(site.getSortType(), Constants.JHC_SORT_TYPE);
    }

    /**
     * 判断是否是邮政站点
     * @param baseSite
     * @return
     */
    public static Boolean isPostalSite(BaseStaffSiteOrgDto baseSite){
        if(baseSite == null){
            return Boolean.FALSE;
        }
        if(Objects.equals(Constants.THIRD_SITE_TYPE,baseSite.getSiteType())
            && Objects.equals(Constants.THIRD_SITE_SUB_TYPE,baseSite.getSubType())
            && Objects.equals(Constants.THIRD_SITE_THIRD_TYPE_SMS,baseSite.getThirdType())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
}
