package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.base.domain.SiteWareHouseMerchant;
import com.jd.bluedragon.distribution.middleend.sorting.domain.DmsCustomSite;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.domain.PsStoreInfo;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.dto.SimpleBaseSite;
import com.jd.ql.dms.report.domain.*;

import java.util.List;
import java.util.Map;


public interface BaseMajorManager {

	public abstract BaseStaffSiteOrgDto getBaseSiteBySiteId(Integer paramInteger);

	public abstract List<BaseDataDict> getBaseDataDictList(
			Integer paramInteger1, Integer paramInteger2, Integer paramInteger3);

	public abstract BaseStaffSiteOrgDto getBaseStaffByStaffId(Integer paramInteger);
	
	public abstract List<BaseStaffSiteOrgDto> getDmsSiteAll();

	public abstract BaseOrg getBaseOrgByOrgId(Integer orgId);
	
	/**
	 * 根据分拣中心id与目的地id获取任务区和电子标签信息
	 * @param 
	 * @return
	 */
	public Integer getBaseGoodsPositionDmsCodeSiteCode(String createCode,String receiveCode);
	
	public List<BaseStaffSiteOrgDto> getBaseSiteAll();
	
	List<BaseStaffSiteOrgDto> getBaseSiteByOrgIdSubType(Integer orgId, Integer targetType);

	public List<BaseStaffSiteOrgDto> getBaseSiteByOrgIdSiteType(Integer orgId, Integer siteType);

	public abstract BaseStaffSiteOrgDto getBaseSiteByDmsCode(String siteCode);

	/**
	 * 根据基础资料字典ID获取字典数据
	 * @param id 基础资料字典ID
	 * @return
	 * */
	public BaseDataDict getBaseDataDictById(Integer id);

	/**
	 * 获取库房信息
	 * @param storeType 库房类型 wms
	 * @param cky2 配送中心==cky2?
	 * @param storeID 库房ID
	 * @param sys 调用系统 dms   */
	public PsStoreInfo getStoreByCky2(String storeType, Integer cky2, Integer storeID, String sys);
	
	/**
	 * 根据机构ID,获取对应的分拣中心
	 * @param orgId 
	 * @return
	 */
	public List<SimpleBaseSite> getDmsListByOrgId(Integer orgId);
	
	/**
	 * 根据站点编号或DMSCODE获得站点信息调用dmsver
	 *
	 * @return BaseStaffSiteOrgDto @
	 */
	public BaseStaffSiteOrgDto queryDmsBaseSiteByCodeDmsver(String siteCode);

	public List<BaseDataDict> getValidBaseDataDictList(Integer parentGroup, Integer nodeLevel, Integer typeGroup);

	Map<Integer,BaseDataDict> getValidBaseDataDictListToMap(Integer parentGroup, Integer nodeLevel, Integer typeGroup);

	public List<BaseStaffSiteOrgDto> getBaseStaffListByOrgId(Integer orgid,int num);

    public BaseStaffSiteOrgDto getThirdStaffByJdAccountNoCache(final String jdAccount);

    public BaseStaffSiteOrgDto getBaseStaffByStaffIdNoCache(Integer paramInteger);

    public BaseStaffSiteOrgDto getBaseStaffByErpNoCache(String erp);

	public BaseStaffSiteOrgDto getBaseStaffIgnoreIsResignByErp(String erpCode);

    /**
     * 根据员工编号获取用户信息
     * @param staffNo 员工编号
     * @return 员工信息
     * @author fanggang7
     * @time 2020-11-05 13:56:29 周四
     */
    BaseStaffSiteOrgDto getBaseStaffInAllRoleByStaffNo(Integer staffNo);

    public Pager<List<SiteWareHouseMerchant>>  getBaseSiteByPage(int pageIndex);

    /**
     * 分页获取库房
     *
     * @param pageIndex
     * @return
     */
    public Pager<List<SiteWareHouseMerchant>> getBaseStoreInfoByPage(Integer pageIndex);

    /**
     * 分页获取所有商家
     * @param pageIndex
     * @return
     */
    public Pager<List<SiteWareHouseMerchant>>  getTraderListByPage(int pageIndex);

	/**
	 * 获取所有库房信息
	 * @return
     */
	public List<BaseStaffSiteOrgDto> getBaseAllStore();

	/**
	 * 根据站点ID查询站点和扩展信息，返回值包含归属站点ID和名称（belongCode,belongName）
	 * @param sitecode 三方-合作站点ID
	 * @return
	 */
	public Integer getPartnerSiteBySiteId(Integer sitecode);

	/**
	 * 获取基础资料维护的所有城市和分拣中心绑定关系的记录
	 * @return
	 */
	public List<BaseDataDict> getAllCityBindDms();
	/**
	 * 根据站点Id获取站点信息
	 * https://cf.jd.com/pages/viewpage.action?pageId=135891308
	 * @param siteId 站点ID
	 * @return
	 */
	BaseSiteInfoDto getBaseSiteInfoBySiteId(Integer siteId);

	/**
	 * 加盟商基础资料中获取 预付款是否充足
	 * @param allianceBusiId
	 * @return
	 */
	boolean allianceBusiMoneyEnough(String allianceBusiId);

	/**
	 * 获取常用功能菜单
	 * @param siteCode
	 * @param erp
	 * 所属系统 1：WinCE版分拣系统 2：安卓版分拣系统 3：青龙分拣打印系统 4：青龙分拣WEB
	 * @return
	 */
	String menuConstantAccount(String siteCode,String erp,Integer source);

	/**
	 * 获取分拣中心自定义站点
	 * @param paramInteger
	 * @return
	 */
	DmsCustomSite getDmsCustomSiteBySiteId(Integer paramInteger);

	/**
	 * 根据商家Id查询
	 * @param merchantId
	 * @return
	 */
	BasicTraderNeccesaryInfoDTO getBaseTraderNeccesaryInfoById(Integer merchantId);

    /**
     * 更新网点基本属性
     * @param baseStaffSiteDTO
     * @return
     */
    boolean updateBaseSiteBasicProperty(BaseStaffSiteDTO baseStaffSiteDTO);

	/**
	 * 根据条件查询站点
	 * @param siteQueryCondition
	 * @param limit
	 * @return
	 */
    List<BasicSite> querySiteByConditionFromEs(SiteQueryCondition siteQueryCondition, Integer limit);

	/**
	 * 根据条件查询站点
	 * 		<p>
	 * 		    查询精简站点数据
	 * 		</p>
	 * @param siteQueryCondition
	 * @param limit
	 * @return
	 */
	List<StreamlinedBasicSite> querySiteByConditionFromStreamlinedSite(StreamlinedSiteQueryCondition siteQueryCondition, Integer limit);

	/**
	 * 校验冷链配置路由
	 * 		<p>
	 * 		    查询精简站点数据
	 * 		</p>
	 * @param createSiteCode
	 * @param endSiteCode
	 * @return
	 */
	boolean validateDirectlySentLine(Integer createSiteCode,Integer endSiteCode);
}
