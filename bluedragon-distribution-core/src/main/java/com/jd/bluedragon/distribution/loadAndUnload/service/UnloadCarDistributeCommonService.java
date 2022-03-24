package com.jd.bluedragon.distribution.loadAndUnload.service;


import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarDistribution;

import java.util.List;

/**
 * 卸车协助人通用实现
 *
 * @author: hujiping
 * @date: 2020/6/23 20:05
 */
public interface UnloadCarDistributeCommonService {

    /**
     * 新增
     * @param detail
     * @return*/
    public int add(UnloadCarDistribution detail);

    public int updateUnloadUser(UnloadCarDistribution detail);

    public List<String> selectHelperBySealCarCode(String sealCarCode);

    public List<String> selectUnloadUserBySealCarCode(String sealCarCode);

    public List<UnloadCarDistribution> selectUnloadCarTaskHelpers(String sealCarCode);

    public boolean deleteUnloadCarTaskHelpers(UnloadCarDistribution params);

    public boolean deleteUnloadHelper(UnloadCarDistribution params);

    public boolean deleteUnloadMaster(UnloadCarDistribution params);

    public List<String> selectTasksByUser(String unloadUserErp);

}
