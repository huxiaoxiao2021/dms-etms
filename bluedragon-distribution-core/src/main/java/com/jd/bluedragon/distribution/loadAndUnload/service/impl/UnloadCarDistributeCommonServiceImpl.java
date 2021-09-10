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
        if (uccPropertyConfiguration.isStopWriteUnloadFromDms()) {
            if (uccPropertyConfiguration.isWriteUnloadFromTys()) {
                return unloadCarDistributionForTysDao.add(detail);
            }
            throw new LoadIllegalException(Constants.UNLOAD_TRANSFER_WARN_MESSAGE);
        }
        return unloadCarDistributionDao.add(detail);
    }

    @Override
    public int updateUnloadUser(UnloadCarDistribution detail) {
        if (uccPropertyConfiguration.isStopWriteUnloadFromDms()) {
            if (uccPropertyConfiguration.isWriteUnloadFromTys()) {
                return unloadCarDistributionForTysDao.updateUnloadUser(detail);
            }
            throw new LoadIllegalException(Constants.UNLOAD_TRANSFER_WARN_MESSAGE);
        }
        return unloadCarDistributionDao.updateUnloadUser(detail);
    }

    @Override
    public List<String> selectHelperBySealCarCode(String sealCarCode) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarDistributionForTysDao.selectHelperBySealCarCode(sealCarCode);
        }
        return unloadCarDistributionDao.selectHelperBySealCarCode(sealCarCode);
    }

    @Override
    public List<String> selectUnloadUserBySealCarCode(String sealCarCode) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarDistributionForTysDao.selectUnloadUserBySealCarCode(sealCarCode);
        }
        return unloadCarDistributionDao.selectUnloadUserBySealCarCode(sealCarCode);
    }

    @Override
    public List<UnloadCarDistribution> selectUnloadCarTaskHelpers(String sealCarCode) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarDistributionForTysDao.selectUnloadCarTaskHelpers(sealCarCode);
        }
        return unloadCarDistributionDao.selectUnloadCarTaskHelpers(sealCarCode);
    }

    @Override
    public boolean deleteUnloadCarTaskHelpers(UnloadCarDistribution params) {
        if (uccPropertyConfiguration.isStopWriteUnloadFromDms()) {
            if (uccPropertyConfiguration.isWriteUnloadFromTys()) {
                return unloadCarDistributionForTysDao.deleteUnloadCarTaskHelpers(params);
            }
            throw new LoadIllegalException(Constants.UNLOAD_TRANSFER_WARN_MESSAGE);
        }
        return unloadCarDistributionDao.deleteUnloadCarTaskHelpers(params);
    }

    @Override
    public List<String> selectTasksByUser(String unloadUserErp) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarDistributionForTysDao.selectTasksByUser(unloadUserErp);
        }
        return unloadCarDistributionDao.selectTasksByUser(unloadUserErp);
    }
}
