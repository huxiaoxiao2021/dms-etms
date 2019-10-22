package com.jd.bluedragon.distribution.middleend;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.middleend.sorting.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class SortingServiceFactory {
    protected Logger logger = LoggerFactory.getLogger(SortingServiceFactory.class);

    @Autowired
    private SiteService siteService;

    @Autowired
    private DmsSortingServiceImpl dmsSortingService;

    @Autowired
    private MiddleEndSortingServiceImpl middleEndSortingService;

    @Autowired
    private FailOverSortingServiceImpl failOverSortingService;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    private static final String SYSTEM_CONFIG_MIDDLE_END_SORTING_OPEN = "failover.sorting.service.site.open";
    private static final String SORTING_SERVICE_MODE_DMS ="DMS";
    private static final String SORTING_SERVICE_MODE_MIDDLEEND="MIDDLEEND";
    private static final String SORTING_SERVICE_MODE_FAILOVER="FAILOVER";

    public ISortingService getSortingService(Integer createSiteCode) {
        String serviceMode = SORTING_SERVICE_MODE_DMS;
        try {
            serviceMode = uccPropertyConfiguration.getSortingServiceMode();
        } catch (Exception e) {
            logger.error("ucc获取分拣serviceType异常.", e);
        }

        if(serviceMode.equals(SORTING_SERVICE_MODE_FAILOVER)){
            Set<Integer> siteSet = siteService.getSiteCodesFromSysConfig(SYSTEM_CONFIG_MIDDLE_END_SORTING_OPEN);
            //站点set为空即代表全部场地开启
            if (!siteSet.isEmpty() && (createSiteCode == null || !siteSet.contains(createSiteCode))) {
                serviceMode = SORTING_SERVICE_MODE_DMS;
            }
        }
        if (serviceMode.equals(SORTING_SERVICE_MODE_DMS)) {
            logger.info("站点[" + createSiteCode + "]使用DMS模式执行分拣流程");
            return dmsSortingService;
        } else if (serviceMode.equals(SORTING_SERVICE_MODE_MIDDLEEND)) {
            logger.info("站点[" + createSiteCode + "]使用MIDDLEEND模式执行分拣流程");
            return middleEndSortingService;
        } else if (serviceMode.equals(SORTING_SERVICE_MODE_FAILOVER)) {
            logger.info("站点[" + createSiteCode + "]使用FAILOVER模式执行分拣流程");
            return failOverSortingService;
        }

        return dmsSortingService;
    }
}