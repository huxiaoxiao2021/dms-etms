package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarDistribution;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarDistributionDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.tys.UnloadCarDistributionForTysDao;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarDistributeCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("unloadCarDistributeCommonService")
public class UnloadCarDistributeCommonServiceImpl implements UnloadCarDistributeCommonService {

    @Autowired
    private UnloadCarDistributionDao unloadCarDistributionDao;

    @Autowired
    private UnloadCarDistributionForTysDao unloadCarDistributionForTysDao;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Override
    public int add(UnloadCarDistribution detail) {
        return unloadCarDistributionForTysDao.add(detail);
    }

    @Override
    public int updateUnloadUser(UnloadCarDistribution detail) {
        return unloadCarDistributionForTysDao.updateUnloadUser(detail);
    }

    @Override
    public List<String> selectHelperBySealCarCode(String sealCarCode) {
        return unloadCarDistributionForTysDao.selectHelperBySealCarCode(sealCarCode);
    }

    @Override
    public List<String> selectUnloadUserBySealCarCode(String sealCarCode) {
        return unloadCarDistributionForTysDao.selectUnloadUserBySealCarCode(sealCarCode);
    }

    @Override
    public List<UnloadCarDistribution> selectUnloadCarTaskHelpers(String sealCarCode) {
        return unloadCarDistributionForTysDao.selectUnloadCarTaskHelpers(sealCarCode);
    }

    @Override
    public boolean deleteUnloadCarTaskHelpers(UnloadCarDistribution params) {
        return unloadCarDistributionForTysDao.deleteUnloadCarTaskHelpers(params);
    }

    @Override
    public boolean deleteUnloadHelper(UnloadCarDistribution params) {
         return unloadCarDistributionForTysDao.deleteUnloadHelper(params);
    }

    @Override
    public boolean deleteUnloadMaster(UnloadCarDistribution params) {
        return unloadCarDistributionForTysDao.deleteUnloadMaster(params);
    }

    @Override
    public List<String> selectTasksByUser(String unloadUserErp) {
        return unloadCarDistributionForTysDao.selectTasksByUser(unloadUserErp);
    }
}
