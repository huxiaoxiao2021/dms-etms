package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.domain.PsStoreInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.dto.SimpleBaseSite;


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
	
	List<BaseStaffSiteOrgDto> getBaseSiteByOrgIdSubType(Integer orgId,
			Integer targetType);

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

	public List<BaseStaffSiteOrgDto> getBaseStaffListByOrgId(Integer orgid,int num);

    public BaseStaffSiteOrgDto getThirdStaffByJdAccount(final String jdAccount);
}
