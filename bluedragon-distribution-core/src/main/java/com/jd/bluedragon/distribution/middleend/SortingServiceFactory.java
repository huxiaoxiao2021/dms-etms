package com.jd.bluedragon.distribution.middleend;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.middleend.sorting.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
            if (createSiteCode == null || !siteService.getSiteCodesFromSysConfig(SYSTEM_CONFIG_MIDDLE_END_SORTING_OPEN).contains(createSiteCode)) {
                serviceMode = SORTING_SERVICE_MODE_DMS;
            }
        }
        if (serviceMode.equals(SORTING_SERVICE_MODE_DMS)) {
            return dmsSortingService;
        } else if (serviceMode.equals(SORTING_SERVICE_MODE_MIDDLEEND)) {
            return middleEndSortingService;
        } else if (serviceMode.equals(SORTING_SERVICE_MODE_FAILOVER)) {
            return failOverSortingService;
        }

        return dmsSortingService;
    }
}