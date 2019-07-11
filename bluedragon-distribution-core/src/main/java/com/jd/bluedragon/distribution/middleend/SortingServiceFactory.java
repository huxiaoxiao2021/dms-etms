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

    private static final String SYSTEM_CONFIG_MIDDLE_END_SORTING_OPEN = "middle.end.sorting.open";

    public ISortingService getSortingService(Integer createSiteCode) {
        String serviceType = "FAILOVER";
        try {
            serviceType = uccPropertyConfiguration.getSortingServiceType();
        } catch (Exception e) {
            logger.error("ucc获取分拣serviceType异常.", e);
        }

        if(serviceType.equals("FAILOVER")){
            if (createSiteCode != null && siteService.getSiteCodesFromSysConfig(SYSTEM_CONFIG_MIDDLE_END_SORTING_OPEN).contains(createSiteCode)) {
                serviceType = "DMS";
            }
        }
        if (serviceType.equals("DMS")) {
            return dmsSortingService;
        } else if (serviceType.equals("MIDDLEEND")) {
            return middleEndSortingService;
        } else if (serviceType.equals("FAILOVER")) {
            return failOverSortingService;
        }

        return new DmsSortingServiceImpl();
    }
}
