package com.jd.bluedragon.distribution.boxlimit.service.impl;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.boxlimit.service.BoxLimitService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.*;

public class SortingNumberLimitFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<Integer, Set<Integer>> receiveSiteTypMap = Collections.unmodifiableMap(new HashMap<Integer, Set<Integer>>(){{
        put(4, new HashSet<>(Arrays.asList(4)));
        put(64, new HashSet<>(Arrays.asList(256, 64, 6408, 6409, 6410)));
    }});
    private static final Integer DEFAULT_LIMIT_NUM = 100;

    @Autowired
    private SortingService sortingService;
    @Autowired
    private BoxLimitService boxLimitService;

    public void doFilter(Object request, Object chain) throws Exception {
        // TODO 从 request 中 获取参数
        // 箱号
        String boxCode = "";
        // 创建站点编号
        Integer createSiteCode = 1;
        // 站点类型
        Integer siteType = 1;
        // 站点子类型
        Integer siteSubType = 1;
        // 存放当前运单的包裹数，传入包裹号时为1
        Integer packageNum = 1;

        // 获取 包裹限制数量
        if (siteType != null && receiveSiteTypMap.containsKey(siteType) && receiveSiteTypMap.get(siteType).contains(siteSubType)) {
            Integer limitNum = boxLimitService.queryLimitNumBySiteId(createSiteCode);
            if (limitNum == null) {
                limitNum = DEFAULT_LIMIT_NUM;
            }

            //箱号已分拣的数量
            int hasSorting = sortingService.findBoxPack(createSiteCode, boxCode);
            limitNumCheck(hasSorting, packageNum, limitNum);
        }
//        chain.doFilter(request, chain);
    }
    /**
     * 校验分拣数量
     * @param hasSorting 已分拣数量
     * @param currentSorting 当前分拣数量
     * @param limitNum 包裹数限制
     */
    private void limitNumCheck(int hasSorting,int currentSorting,int limitNum) throws Exception {
        if (hasSorting + currentSorting > limitNum) {
            // TODO 抛出 SortingCheckException
            //提示换箱号
            throw new RuntimeException(MessageFormat.format(SortingResponse.MESSAGE_29300,limitNum));
//            throw new SortingCheckException(SortingResponse.CODE_29417, MessageFormat.format(SortingResponse.MESSAGE_29417_WAYBILL,limitNum));
        }
    }


}
