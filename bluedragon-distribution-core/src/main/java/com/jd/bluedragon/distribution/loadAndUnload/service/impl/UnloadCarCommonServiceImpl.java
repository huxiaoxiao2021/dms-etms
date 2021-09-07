package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTask;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.tys.UnloadCarForTysDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarCommonService;
import com.jd.bluedragon.distribution.unloadCar.domain.UnloadCarCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service("unloadCarCommonService")
public class UnloadCarCommonServiceImpl implements UnloadCarCommonService {

    @Autowired
    private UnloadCarDao unloadCarDao;

    @Autowired
    private UnloadCarForTysDao unloadCarForTysDao;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;


    @Override
    public int add(UnloadCar detail) {
        return 0;
    }

    @Override
    public UnloadCar selectBySealCarCode(String sealCarCode) {

        return null;
    }

    @Override
    public UnloadCar selectBySealCarCodeWithStatus(String sealCarCode) {
        return null;
    }

    @Override
    public List<UnloadCarTask> queryByCondition(UnloadCarCondition condition) {
        return null;
    }

    @Override
    public int queryCountByCondition(UnloadCarCondition condition) {
        return 0;
    }

    @Override
    public Integer distributeTaskByParams(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<UnloadCar> getUnloadCarTaskByParams(UnloadCar params) {
        return null;
    }

    @Override
    public int updateUnloadCarTaskStatus(UnloadCar params) {
        return 0;
    }

    @Override
    public List<UnloadCar> getUnloadCarTaskScan(List<String> sealCarCodes) {
        return null;
    }

    @Override
    public List<UnloadCar> selectByUnloadCar(UnloadCar unloadCar) {
        return null;
    }

    @Override
    public List<UnloadCar> selectTaskByLicenseNumberAndSiteCode(UnloadCar params) {
        return null;
    }

    @Override
    public int updateStartTime(UnloadCar params) {
        return 0;
    }

    @Override
    public List<UnloadCar> selectByCondition(UnloadCar unloadCar) {
        return null;
    }

    private boolean isUseNewUnloadCar(String createSiteCode) {
        String siteCodes = uccPropertyConfiguration.getUseNewInventorySiteCodes();
        if (StringUtils.isBlank(siteCodes)) {
            return false;
        }
        List<String> siteList = Arrays.asList(siteCodes.split(Constants.SEPARATOR_COMMA));
        return siteList.contains(createSiteCode) || siteList.contains("true");
    }

}
