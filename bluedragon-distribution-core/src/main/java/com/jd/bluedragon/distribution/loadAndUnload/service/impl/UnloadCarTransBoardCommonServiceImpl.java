package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTransBoard;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarTransBoardDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.tys.UnloadCarTransBoardForTysDao;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarTransBoardCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("unloadCarTransBoardCommonService")
public class UnloadCarTransBoardCommonServiceImpl implements UnloadCarTransBoardCommonService {

    @Autowired
    private UnloadCarTransBoardDao unloadCarTransBoardDao;

    @Autowired
    private UnloadCarTransBoardForTysDao unloadCarTransBoardForTysDao;

    @Autowired
    private DmsConfigManager dmsConfigManager;


    @Override
    public int add(UnloadCarTransBoard detail) {
        if (dmsConfigManager.getPropertyConfig().isStopWriteUnloadFromDms()) {
            if (dmsConfigManager.getPropertyConfig().isWriteUnloadFromTys()) {
                return unloadCarTransBoardForTysDao.add(detail);
            }
            throw new LoadIllegalException(Constants.UNLOAD_TRANSFER_WARN_MESSAGE);
        }
        return unloadCarTransBoardDao.add(detail);
    }

    @Override
    public int updateCount(UnloadCarTransBoard detail) {
        if (dmsConfigManager.getPropertyConfig().isStopWriteUnloadFromDms()) {
            if (dmsConfigManager.getPropertyConfig().isWriteUnloadFromTys()) {
                return unloadCarTransBoardForTysDao.updateCount(detail);
            }
            throw new LoadIllegalException(Constants.UNLOAD_TRANSFER_WARN_MESSAGE);
        }
        return unloadCarTransBoardDao.updateCount(detail);
    }

    @Override
    public List<String> searchBoardsBySealCode(String sealCarCode) {
        if (dmsConfigManager.getPropertyConfig().isReadUnloadFromTys()) {
            return unloadCarTransBoardForTysDao.searchBoardsBySealCode(sealCarCode);
        }
        return unloadCarTransBoardDao.searchBoardsBySealCode(sealCarCode);
    }

    @Override
    public UnloadCarTransBoard searchBySealCode(String sealCarCode) {
        if (dmsConfigManager.getPropertyConfig().isReadUnloadFromTys()) {
            return unloadCarTransBoardForTysDao.searchBySealCode(sealCarCode);
        }
        return unloadCarTransBoardDao.searchBySealCode(sealCarCode);
    }

    @Override
    public UnloadCarTransBoard searchByBoardCode(String boardCode) {
        if (dmsConfigManager.getPropertyConfig().isReadUnloadFromTys()) {
            return unloadCarTransBoardForTysDao.searchByBoardCode(boardCode);
        }
        return unloadCarTransBoardDao.searchByBoardCode(boardCode);
    }

    @Override
    public UnloadCarTransBoard searchBySealCodeAndBoardCode(String sealCarCode, String boardCode) {
        if (dmsConfigManager.getPropertyConfig().isReadUnloadFromTys()) {
            return unloadCarTransBoardForTysDao.searchBySealCodeAndBoardCode(sealCarCode, boardCode);
        }
        return unloadCarTransBoardDao.searchBySealCodeAndBoardCode(sealCarCode, boardCode);
    }
}
