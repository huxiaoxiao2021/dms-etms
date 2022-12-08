package com.jd.bluedragon.distribution.jy.service.calibrate;

import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateDetailEntity;

import java.util.List;

/**
 * 拣运设备校准任务详情SERVICE
 *
 * @author hujiping
 * @date 2022/12/7 6:39 PM
 */
public interface JyBizTaskMachineCalibrateDetailService {

    /**
     * 新增
     * @param entity
     * @return
     */
    int insert(JyBizTaskMachineCalibrateDetailEntity entity);

    /**
     * 修改
     * @param entity
     * @return
     */
    int update(JyBizTaskMachineCalibrateDetailEntity entity);

    /**
     * 根据条件修改设备状态
     *
     * @param entity
     * @return
     */
    int updateMachineStatus(JyBizTaskMachineCalibrateDetailEntity entity);

    /**
     * 根据条件修改任务状态
     *
     * @param entity
     * @return
     */
    int updateTaskStatus(JyBizTaskMachineCalibrateDetailEntity entity);

    /**
     * 根据条件查询任务
     *
     * @param condition
     * @return
     */
    List<JyBizTaskMachineCalibrateDetailEntity> selectByCondition(JyBizTaskMachineCalibrateDetailEntity condition);

}
