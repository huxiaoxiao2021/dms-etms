package com.jd.bluedragon.distribution.loadAndUnload.service;


import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTask;
import com.jd.bluedragon.distribution.unloadCar.domain.UnloadCarCondition;

import java.util.List;
import java.util.Map;

/**
 * 卸车通用实现
 *
 * @author: hujiping
 * @date: 2020/6/23 20:05
 */
public interface UnloadCarCommonService {

    /**
     * 新增
     * @param detail
     * @return*/
    public int add(UnloadCar detail);

    public UnloadCar selectBySealCarCode(String sealCarCode);

    public UnloadCar selectBySealCarCodeWithStatus(String sealCarCode);

    public List<UnloadCarTask> queryByCondition(UnloadCarCondition condition);

    public int queryCountByCondition(UnloadCarCondition condition);

    public Integer distributeTaskByParams(Map<String, Object> params);

    public List<UnloadCar> getUnloadCarTaskByParams(UnloadCar params);

    public int updateUnloadCarTaskStatus(UnloadCar params);

    public List<UnloadCar> getUnloadCarTaskScan(List<String> sealCarCodes);

    public List<UnloadCar> selectByUnloadCar(UnloadCar unloadCar);

    public List<UnloadCar> selectTaskByLicenseNumberAndSiteCode(UnloadCar params);

    public int updateStartTime(UnloadCar params);

    public List<UnloadCar> selectByCondition(UnloadCar unloadCar);

    List<UnloadCar> getTaskInfoBySealCarCodes(List<String> sealCarCodes);

}
