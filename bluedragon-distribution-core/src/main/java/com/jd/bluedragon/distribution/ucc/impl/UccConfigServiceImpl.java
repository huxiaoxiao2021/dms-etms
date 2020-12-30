package com.jd.bluedragon.distribution.ucc.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.ucc.UccConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName UccConfigServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/12/28 17:19
 **/
@Service
public class UccConfigServiceImpl implements UccConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UccConfigServiceImpl.class);

    @Autowired
    private UccPropertyConfiguration uccConfiguration;

    @Override
    public boolean siteEnableFilePackageCheck(Integer siteCode) {
        if (null == siteCode) {
            return false;
        }
        String configSite = uccConfiguration.getSiteEnableFilePackageCheck();
        if (StringUtils.isBlank(configSite)) {
            return false;
        }

        if (Constants.STR_ALL.equalsIgnoreCase(configSite)) {
            return true;
        }

        List<String> sites = null;
        try {
            sites = Arrays.asList(StringUtils.split(configSite, Constants.SEPARATOR_COMMA));
        }
        catch (Exception ex) {
            LOGGER.error("transfer inspection split waybill site error.", ex);
        }

        if (CollectionUtils.isEmpty(sites)) {
            return false;
        }

        return sites.contains(siteCode.toString());
    }
}
