package com.jd.bluedragon.distribution.newseal.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.jsf.tms.TmsServiceManager;
import com.jd.bluedragon.core.jsf.tms.TransportResource;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.newseal.dao.DmsSendRelationDao;
import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelation;
import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelationCondition;
import com.jd.bluedragon.distribution.newseal.service.DmsSendRelationService;
import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealQueryRequest;
import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealRecord;
import com.jd.bluedragon.utils.SqlUtils;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheKeyGenerator;
import com.jd.ql.dms.common.cache.CacheService;

/**
 * @ClassName: DmsSendRelationServiceImpl
 * @Description: 分拣发货关系表--Service接口实现
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
@Service("dmsSendRelationService")
public class DmsSendRelationServiceImpl implements DmsSendRelationService {

	private static final Logger logger = LoggerFactory.getLogger(DmsSendRelationServiceImpl.class);

	@Autowired
	@Qualifier("dmsSendRelationDao")
	private DmsSendRelationDao dmsSendRelationDao;
	
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;
    
    @Autowired
    @Qualifier("cacheKeyGenerator")
    private CacheKeyGenerator cacheKeyGenerator;
    
    @Autowired
    private BaseMinorManager baseMinorManager;
    
    @Autowired
    private TmsServiceManager tmsServiceManager;
    
	@Autowired
	private BaseMajorManager baseMajorManager;
    /**
     * 数据更新频率，默认1800（单位：秒）
     */
    @Value("${beans.DmsSendRelationServiceImpl.saveFrequency:1800}")
	private int saveFrequency;
    /**
     * 页面字段对应sql字段关系
     */
    private static Map<String,String> COLUM_MAPPING_QUERYPASSPRESEALDATA = new HashMap<String,String>();
    static{
    	COLUM_MAPPING_QUERYPASSPRESEALDATA.put("jobCreateTime", "s.depart_time");
    	COLUM_MAPPING_QUERYPASSPRESEALDATA.put("departTime", "s.job_create_time");
    	COLUM_MAPPING_QUERYPASSPRESEALDATA.put("preSealStatus", "pre_seal_status");
    }
	@Override
	public boolean saveOrUpdate(DmsSendRelation dmsSendRelation) {
		DmsSendRelation oldData = dmsSendRelationDao.queryByBusinessKey(dmsSendRelation);
		if(oldData == null){
			if(dmsSendRelation.getCreateTime() == null) {
				dmsSendRelation.setCreateTime(new Date());
			}
			return dmsSendRelationDao.insert(dmsSendRelation) == 1;
		}else{
			dmsSendRelation.setId(oldData.getId());
			dmsSendRelation.setTs(new Date());
			return dmsSendRelationDao.update(dmsSendRelation) == 1;
		}
	}
	
	@Override
	public boolean saveWithFrequency(DmsSendRelation dmsSendRelation) {
		if(dmsSendRelation.getOriginalSiteCode()== null 
				|| dmsSendRelation.getDestinationSiteCode() == null) {
			logger.warn("saveWithFrequency-fail:始发和目的不能为空！{0}", JsonHelper.toJson(dmsSendRelation));
			return false;
		}
		String cachekey = cacheKeyGenerator.getCacheKey(CacheKeyConstants.CACHE_KEY_FORMAT_SAVE_SEND_RELATION,
				dmsSendRelation.getOriginalSiteCode().toString(), 
				dmsSendRelation.getDestinationSiteCode().toString());
		//默认半小时更新一次数据
		if(cacheService.setNx(cachekey, Constants.FLAG_OPRATE_ON,saveFrequency)) {
			//加载运力及滑道号信息
			loadLineAndCrossInfo(dmsSendRelation);
            if (this.saveOrUpdate(dmsSendRelation)) {
                return true;
            } else {
                cacheService.del(cachekey);
                return false;
            }
		}
		return true;
	}
	/**
	 * 加载线路及滑道号相关信息
	 * @param dmsSendRelation
	 */
	private void loadLineAndCrossInfo(DmsSendRelation dmsSendRelation) {
		String startNodeCode = null;
		String endNodeCode = null;
		//加载站点名称
		if(dmsSendRelation.getOriginalSiteCode() != null ) {
			BaseStaffSiteOrgDto startSiteInfo= baseMajorManager.getBaseSiteBySiteId(dmsSendRelation.getOriginalSiteCode());
			if(startSiteInfo != null) {
				dmsSendRelation.setOriginalSiteName(startSiteInfo.getSiteName());
				startNodeCode = startSiteInfo.getDmsSiteCode();
			}else {
				logger.warn("加载始发信息为空！{}", dmsSendRelation.getOriginalSiteCode());
			}
		}
		if(dmsSendRelation.getDestinationSiteCode() != null ) {
			BaseStaffSiteOrgDto endSiteInfo= baseMajorManager.getBaseSiteBySiteId(dmsSendRelation.getDestinationSiteCode());
			if(endSiteInfo != null) {
				dmsSendRelation.setDestinationSiteName(endSiteInfo.getSiteName());
				endNodeCode = endSiteInfo.getDmsSiteCode();
			}else {
				logger.warn("加载目的信息为空！{}", dmsSendRelation.getDestinationSiteCode());
			}
		}
		//加载线路信息
		JdResult<List<TransportResource>> transportResult = tmsServiceManager.loadTransportResources(startNodeCode, endNodeCode, Constants.CUAN_BAI_LINE_TYPES);
		if(transportResult != null && CollectionUtils.isNotEmpty(transportResult.getData())) {
			TransportResource transportResource = transportResult.getData().get(0);
			dmsSendRelation.setLineType(transportResource.getTransType());
		}else {
			dmsSendRelation.setLineType(Constants.LINE_TYPE_DEFAULT);
			logger.warn("加载线路信息为空！{}->{}", dmsSendRelation.getOriginalSiteCode(),dmsSendRelation.getDestinationSiteCode());
		}
		//加载大全表信息
		JdResult<CrossPackageTagNew> crossResult =  baseMinorManager.queryCrossPackageTag(null, dmsSendRelation.getDestinationSiteCode(), dmsSendRelation.getOriginalSiteCode(), Constants.ORIGINAL_CROSS_TYPE_GENERAL);
		if(crossResult != null 
				&& crossResult.isSucceed() 
				&& crossResult.getData() != null) {
			dmsSendRelation.setOriginalCrossCode(crossResult.getData().getOriginalCrossCode());
			dmsSendRelation.setDestinationCrossCode(crossResult.getData().getDestinationCrossCode());
		}else {
			logger.warn("加载大全表信息为空！{}->{}", dmsSendRelation.getOriginalSiteCode(),dmsSendRelation.getDestinationSiteCode());
		}
	}

	@Override
	public List<PassPreSealRecord> queryPassPreSealData(PassPreSealQueryRequest queryCondition) {
		//处理排序信息
		if(queryCondition.getOrders() != null) {
			queryCondition.setOrderBy(SqlUtils.genOrderBySql(queryCondition.getOrders(), COLUM_MAPPING_QUERYPASSPRESEALDATA));
		}
		return dmsSendRelationDao.queryPassPreSealData(queryCondition);
	}

	@Override
	public Integer countPassPreSealData(PassPreSealQueryRequest queryCondition) {
		return dmsSendRelationDao.countPassPreSealData(queryCondition);
	}

}
