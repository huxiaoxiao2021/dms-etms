package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.boxlimit.service.BoxLimitService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.ver.config.NumberLimitConfig;
import com.jd.bluedragon.distribution.ver.config.SortingNumberLimitConfig;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fce.zookeeper.common.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.*;

public class SortingNumberLimitFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 需拦截的目的地网点类型: 二级分类(编号)-[三级分类(编号)]
     * 营业部(4)-[营业部(4)]
     * 分拣中心(64)-[中转站(256),一级分拣中心(64),航空分拣中心(6408),二级分拨中心(6409),三级中转场(6410)]
     */
    private final Map<Integer, Set<Integer>> receiveSiteTypMap = Collections.unmodifiableMap(new HashMap<Integer, Set<Integer>>(){{
        put(4, new HashSet<>(Collections.singletonList(4)));
        put(64, new HashSet<>(Arrays.asList(256, 64, 6408, 6409, 6410)));
    }});
    /**
     * 默认建箱包裹数量
     */
    private static final Integer DEFAULT_LIMIT_NUM = 100;
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
        		if(siteType != null && sortingNumberLimitConfig.getSiteTypes().contains(siteType)){
                    //校验开关是否开启
                    NumberLimitConfig siteCheckConfig = this.getSwitchStatus(CONFIG_SITE_PACKAGE_NUM_CHECK);
                    if (siteCheckConfig != null && Boolean.TRUE.equals(siteCheckConfig.getIsOpen())) {
                    	limitNums.add(siteCheckConfig.getMaxNum());
                    }
        		}
        	}
            //校验开关是否开启
            NumberLimitConfig config =this.getSwitchStatus(CONFIG_SEND_PACKAGE_NUM_CHECK);
            if(config != null && Boolean.TRUE.equals(config.getIsOpen())) {
            	limitNums.add(config.getMaxNum());
            }
            if(limitNums.size() > 0 || needBoxLimitCheck(request.getReceiveSite())){
                int currentNum = 0;
            	//箱号已分拣的数量
            	int hasSorting = sortingService.findBoxPack(request.getBox().getCreateSiteCode(), request.getBoxCode());

                if (request.getPackageNum() != null) {
                	currentNum = request.getPackageNum();
                }
                if (limitNums.size() > 0) {
                    //多个限制，按限制数量排序
                    if (limitNums.size() > 1) {
                        CollectionUtils.sort(limitNums);
                    }
                    for (Integer limitNum : limitNums){
                        limitNumCheck(hasSorting, currentNum, limitNum);
                    }
                }

                if (needBoxLimitCheck(request.getReceiveSite())) {
                    boxLimitCheck(hasSorting, currentNum, request.getCreateSiteCode());
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

    /**
     * PDA建箱包裹数上限限制二期
     *
     * @param hasSorting     已分拣数量
     * @param currentSorting 当前分拣数量
     * @param createSiteCode 当前建箱站点Id
     */
    private void boxLimitCheck(int hasSorting, int currentSorting,Integer createSiteCode) throws SortingCheckException {
        Integer limitNum = boxLimitService.queryLimitNumBySiteId(createSiteCode);
        if (limitNum == null) {
            limitNum = DEFAULT_LIMIT_NUM;
        }
        if (hasSorting + currentSorting > limitNum) {
            //提示换箱号
            throw new SortingCheckException(SortingResponse.CODE_29417, MessageFormat.format(SortingResponse.MESSAGE_29417_BOXLIMIT,limitNum));
        }
    }

    /**
     * 根据目的地站点类型判断是否需要 建箱包裹数 校验
     * @param receiveSite 目的地站点类型
     * @return  false-不需要校验, true-需要校验
     */
    private boolean needBoxLimitCheck(Site receiveSite) {
        if (receiveSite == null) {
            return false;
        }
        // 二级分类
        Integer siteType = receiveSite.getType();
        // 三级分类
        Integer siteSubType = receiveSite.getType();
        return  (receiveSiteTypMap.containsKey(siteType) && receiveSiteTypMap.get(siteType).contains(siteSubType));
    }
}
