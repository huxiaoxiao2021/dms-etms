package com.jd.bluedragon.distribution.ver.filter.filters;

import com.google.gson.Gson;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.boxlimit.service.BoxLimitService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.ver.config.NumberLimitConfig;
import com.jd.bluedragon.distribution.ver.config.SortingNumberLimitConfig;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fce.zookeeper.common.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SortingNumberLimitFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 包裹数验证配置开关
     */
    private static final String CONFIG_SEND_PACKAGE_NUM_CHECK = "sys.config.sorting.sortingPackageNumCheck";
    /**
     * 站点包裹数验证配置开关
     */
    private static final String CONFIG_SITE_PACKAGE_NUM_CHECK = "sys.config.sorting.siteSortingPackageNumCheck";

    @Autowired
    private SortingService sortingService;

    @Autowired
    private SortingNumberLimitConfig sortingNumberLimitConfig;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private BoxLimitService boxLimitService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        if (request.getBox() != null) {
        	//存放限制的数量列表
        	List<Integer> limitNums = new ArrayList<>();
        	Integer siteType = null;
        	if (request.getReceiveSite() != null) {
        		siteType = request.getReceiveSite().getType();
                logger.info("分拣数量限制拦截 siteType: {}, subType：{}", siteType, request.getReceiveSite().getSubType());
        		Integer subSiteType = request.getReceiveSite().getType();
                Map<Integer, Set<Integer>> siteTypes = sortingNumberLimitConfig.getSiteTypes();
                logger.info("分拣数量限制拦截,siteTypes:{}", siteTypes);
                if (siteType != null && subSiteType != null && siteTypes != null
                        && siteTypes.containsKey(siteType) && siteTypes.get(siteType).contains(subSiteType)) {
                    //校验开关是否开启
                    NumberLimitConfig siteCheckConfig = this.getSwitchStatus(CONFIG_SITE_PACKAGE_NUM_CHECK);
                    logger.info("分拣数量限制拦截,siteCheckConfig:{}", new Gson().toJson(siteCheckConfig));
                    if (siteCheckConfig != null && Boolean.TRUE.equals(siteCheckConfig.getIsOpen())) {
                        Integer limitNum = boxLimitService.queryLimitNumBySiteId(request.getCreateSiteCode());
                        logger.info("分拣数量限制拦截,createSiteCode:{},queryLimitNumBySiteId:{}", request.getCreateSiteCode(), new Gson().toJson(siteCheckConfig));
                        if (limitNum != null) {
                            limitNums.add(limitNum);
                        } else {
                            limitNums.add(siteCheckConfig.getMaxNum());
                        }

                    }
                }
        	}
            //校验开关是否开启
            NumberLimitConfig config =this.getSwitchStatus(CONFIG_SEND_PACKAGE_NUM_CHECK);
            if(config != null && Boolean.TRUE.equals(config.getIsOpen())) {
            	limitNums.add(config.getMaxNum());
            }
            if(limitNums.size() > 0){
                int currentNum = 0;
            	//箱号已分拣的数量
            	int hasSorting = sortingService.findBoxPack(request.getBox().getCreateSiteCode(), request.getBoxCode());

                if (request.getPackageNum() != null) {
                	currentNum = request.getPackageNum();
                }
                //多个限制，按限制数量排序
                if (limitNums.size() > 1) {
                	CollectionUtils.sort(limitNums);
                }
                for (Integer limitNum : limitNums){
                	limitNumCheck(hasSorting, currentNum, limitNum);
                }
            }
        }
        chain.doFilter(request, chain);
    }
    /**
     * 校验分拣数量
     * @param hasSorting
     * @param currentSorting
     * @param limitNum
     * @throws SortingCheckException
     */
    private void limitNumCheck(int hasSorting,int currentSorting,int limitNum) throws SortingCheckException {
        logger.info("分拣数量限制拦截,hasSorting:{},currentSorting:{},limitNum:{}", hasSorting, currentSorting, limitNum);
    	if(currentSorting > limitNum) {
            //当前分拣数量大于限制数量，提示按包裹分拣
            throw new SortingCheckException(SortingResponse.CODE_29417, MessageFormat.format(SortingResponse.MESSAGE_29417_WAYBILL,limitNum));
        } else if (hasSorting + currentSorting > limitNum) {
            //提示换箱号
            throw new SortingCheckException(SortingResponse.CODE_29417, MessageFormat.format(SortingResponse.MESSAGE_29417,limitNum));
        }
    }

    private NumberLimitConfig getSwitchStatus(String conName){

        List<SysConfig> sysConfigs = null;
        try{
            sysConfigs = sysConfigService.getCachedList(conName);
        }catch(Exception ex){
            logger.error("获取开关信息失败:conName={}",conName, ex);
        }
        if(null == sysConfigs || sysConfigs.size() <= 0){
            return null;
        }

        return JsonHelper.fromJson(sysConfigs.get(0).getConfigContent(), NumberLimitConfig.class);
    }
}
