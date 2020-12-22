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
    protected Logger log = LoggerFactory.getLogger(SortingServiceFactory.class);

    @Autowired
    private SiteService siteService;

    @Autowired
    private DmsSortingServiceImpl dmsSortingService;


    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    private static final String SYSTEM_CONFIG_MIDDLE_END_SORTING_OPEN = "failover.sorting.service.site.open";
    private static final String SYSTEM_CONFIG_MIDDLE_END_SORTING_CLOSE = "failover.sorting.service.site.close";

    private static final String SORTING_SERVICE_MODE_DMS ="DMS";
    private static final String SORTING_SERVICE_MODE_MIDDLEEND="MIDDLEEND";
    private static final String SORTING_SERVICE_MODE_FAILOVER="FAILOVER";

    public ISortingService getSortingService(Integer createSiteCode) {
        return dmsSortingService;
    }
}