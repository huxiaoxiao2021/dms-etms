package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTask;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.tys.UnloadCarForTysDao;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarCommonService;
import com.jd.bluedragon.distribution.unloadCar.domain.UnloadCarCondition;
import com.jd.jsf.gd.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
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
        if (uccPropertyConfiguration.isStopWriteUnloadFromDms()) {
            if (uccPropertyConfiguration.isWriteUnloadFromTys()) {
                return unloadCarForTysDao.add(detail);
            }
            throw new LoadIllegalException(Constants.UNLOAD_TRANSFER_WARN_MESSAGE);
        }
        return unloadCarDao.add(detail);
    }

    @Override
    public UnloadCar selectBySealCarCode(String sealCarCode) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarForTysDao.selectBySealCarCode(sealCarCode);
        }
        return unloadCarDao.selectBySealCarCode(sealCarCode);
    }

    @Override
    public UnloadCar selectBySealCarCodeWithStatus(String sealCarCode) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarForTysDao.selectBySealCarCodeWithStatus(sealCarCode);
        }
        return unloadCarDao.selectBySealCarCodeWithStatus(sealCarCode);
    }

    @Override
    public List<UnloadCarTask> queryByCondition(UnloadCarCondition condition) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarForTysDao.queryByCondition(condition);
        }
        return unloadCarDao.queryByCondition(condition);
    }

    @Override
    public int queryCountByCondition(UnloadCarCondition condition) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarForTysDao.queryCountByCondition(condition);
        }
        return unloadCarDao.queryCountByCondition(condition);
    }

    @Override
    public Integer distributeTaskByParams(Map<String, Object> params) {
        if (uccPropertyConfiguration.isStopWriteUnloadFromDms()) {
            if (uccPropertyConfiguration.isWriteUnloadFromTys()) {
                log.info("转运DB卸车任务分配负责人{}", JsonUtils.toJSONString(params));
                return unloadCarForTysDao.distributeTaskByParams(params);
            }
            throw new LoadIllegalException(Constants.UNLOAD_TRANSFER_WARN_MESSAGE);
        }
        log.info("转运DB卸车任务分配负责人{}", JsonUtils.toJSONString(params));
        return unloadCarDao.distributeTaskByParams(params);
    }

    @Override
    public List<UnloadCar> getUnloadCarTaskByParams(UnloadCar params) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarForTysDao.getUnloadCarTaskByParams(params);
        }
        return unloadCarDao.getUnloadCarTaskByParams(params);
    }

    @Override
    public int updateUnloadCarTaskStatus(UnloadCar params) {
        if (uccPropertyConfiguration.isStopWriteUnloadFromDms()) {
            if (uccPropertyConfiguration.isWriteUnloadFromTys()) {
                return unloadCarForTysDao.updateUnloadCarTaskStatus(params);
            }
            throw new LoadIllegalException(Constants.UNLOAD_TRANSFER_WARN_MESSAGE);
        }
        return unloadCarDao.updateUnloadCarTaskStatus(params);
    }

    @Override
    public List<UnloadCar> getUnloadCarTaskScan(List<String> sealCarCodes) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarForTysDao.getUnloadCarTaskScan(sealCarCodes);
        }
        return unloadCarDao.getUnloadCarTaskScan(sealCarCodes);
    }

    @Override
    public List<UnloadCar> selectByUnloadCar(UnloadCar unloadCar) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarForTysDao.selectByUnloadCar(unloadCar);
        }
        return unloadCarDao.selectByUnloadCar(unloadCar);
    }

    @Override
    public List<UnloadCar> selectTaskByLicenseNumberAndSiteCode(UnloadCar params) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarForTysDao.selectTaskByLicenseNumberAndSiteCode(params);
        }
        return unloadCarDao.selectTaskByLicenseNumberAndSiteCode(params);
    }

    @Override
    public int updateStartTime(UnloadCar params) {
        if (uccPropertyConfiguration.isStopWriteUnloadFromDms()) {
            if (uccPropertyConfiguration.isWriteUnloadFromTys()) {
                return unloadCarForTysDao.updateStartTime(params);
            }
            throw new LoadIllegalException(Constants.UNLOAD_TRANSFER_WARN_MESSAGE);
        }
        return unloadCarDao.updateStartTime(params);
    }

    @Override
    public List<UnloadCar> selectByCondition(UnloadCar unloadCar) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarForTysDao.selectByCondition(unloadCar);
        }
        return unloadCarDao.selectByCondition(unloadCar);
    }

    //
    @Override
    public List<UnloadCar> getTaskInfoBySealCarCodes(List<String> sealCarCodes) {
        if (uccPropertyConfiguration.isReadUnloadFromTys()) {
            return unloadCarForTysDao.getTaskInfoBySealCarCodes(sealCarCodes);
        }
        return null;
    }


}
