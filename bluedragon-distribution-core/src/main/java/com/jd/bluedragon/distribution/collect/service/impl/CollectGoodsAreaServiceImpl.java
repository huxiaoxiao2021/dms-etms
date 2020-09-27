package com.jd.bluedragon.distribution.collect.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsAreaDao;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsArea;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsAreaService;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: CollectGoodsAreaServiceImpl
 * @Description: 集货区表--Service接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Service("collectGoodsAreaService")
public class CollectGoodsAreaServiceImpl extends BaseService<CollectGoodsArea> implements CollectGoodsAreaService {

    // 集货区可删除站点，-1代表全国
    private static final String COLLECT_GOODS_SITES = "-1";

	@Autowired
	@Qualifier("collectGoodsAreaDao")
	private CollectGoodsAreaDao collectGoodsAreaDao;

	@Autowired
	@Qualifier("collectGoodsPlaceService")
	private CollectGoodsPlaceService collectGoodsPlaceService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private LogEngine logEngine;

	@Override
	public Dao<CollectGoodsArea> getDao() {
		return this.collectGoodsAreaDao;
	}


	@Override
	public boolean findExistByAreaCode(CollectGoodsArea e) {
		return collectGoodsAreaDao.findExistByAreaCode(e)>0?true:false;
	}

	@Override
	public List<CollectGoodsArea> findBySiteCode(CollectGoodsArea e) {
		return collectGoodsAreaDao.findBySiteCode(e);
	}

	@Override
	public boolean deleteByCode(List<String> codes) {

		return collectGoodsAreaDao.deleteByCode(codes)>0 && collectGoodsPlaceService.deleteByAreaCode(codes);
	}

    /**
     * 校验场地权限
     * @param createSiteCode
     */
    @Override
    public boolean checkAuthority(Integer createSiteCode) {
        String collectGoodsDeleteSites = uccPropertyConfiguration.getCollectGoodsDeleteSites();
        if(StringUtils.isEmpty(collectGoodsDeleteSites)){
            return true;
        }
        List<String> siteCodes = Arrays.asList(collectGoodsDeleteSites.split(Constants.SEPARATOR_COMMA));
        // 全国未开启 且 场地不包含则直接返回
        if(!COLLECT_GOODS_SITES.equals(collectGoodsDeleteSites)
                && !siteCodes.contains(String.valueOf(createSiteCode))){
            return true;
        }
        return false;
    }

    /**
     * 记录businessLog日志
     * @param userErp
     * @param createSiteCode
     * @param codes
     */
    @Override
    public void writeLog(String userErp, Integer createSiteCode, List<String> codes) {
        try {
            long startTime = System.currentTimeMillis();
            JSONObject request = new JSONObject();
            request.put("operatorCode",userErp);
            request.put("siteCode", createSiteCode);
            request.put("operateTime", new Date());

            JSONObject response = new JSONObject();
            response.put("code", "200");
            response.put("content", String.format("站点【%s】操作人【%s】删除了%s的集货区",createSiteCode,userErp, JsonHelper.toJson(codes)));

            long endTime = System.currentTimeMillis();

            BusinessLogProfiler logProfiler = new BusinessLogProfilerBuilder()
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.COLLECT_GOODS_DELETE)
                    .processTime(endTime, startTime)
                    .operateRequest(request)
                    .operateResponse(response)
                    .methodName("CollectGoodsAreaServiceImpl#checkAuthority")
                    .build();
            logEngine.addLog(logProfiler);
        }catch (Exception e){
            log.error("集货区删除记录businessLog异常!",e);
        }
    }
}
