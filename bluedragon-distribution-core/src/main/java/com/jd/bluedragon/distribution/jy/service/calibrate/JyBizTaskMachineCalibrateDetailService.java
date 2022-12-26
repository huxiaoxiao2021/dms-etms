package com.jd.bluedragon.distribution.jy.service.calibrate;

import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateCondition;
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
     * 根据条件查询最近一条记录
     *
     * @param condition
     * @return
     */
    JyBizTaskMachineCalibrateDetailEntity selectLatelyOneByCondition(JyBizTaskMachineCalibrateCondition condition);

    /**
     * 根据条件查询任务
     *
     * @param condition
     * @return
     */
    List<JyBizTaskMachineCalibrateDetailEntity> selectByCondition(JyBizTaskMachineCalibrateCondition condition);

    /**
     * 根据条件查询数量
     *  仅为超时任务使用
     * @param condition
     * @return
     */
    Integer selectCountForTask(JyBizTaskMachineCalibrateCondition condition);

    /**
     * 根据条件查询任务
     *  仅为超时任务使用
     * @param condition
     * @return
     */
    List<JyBizTaskMachineCalibrateDetailEntity> selectByConditionForTask(JyBizTaskMachineCalibrateCondition condition);


    /**
     * 查询当前时间段内的待处理任务
     * @param condition
     * @return
     */
    List<JyBizTaskMachineCalibrateDetailEntity> queryCurrentTaskDetail(JyBizTaskMachineCalibrateCondition condition);

    /**
     * 设备关闭后废弃当前待处理任务
     * @param entity
     * @return
     */
    int closeCalibrateDetailById(JyBizTaskMachineCalibrateDetailEntity entity);

    /**
     * 根据id批量更新状态
     *
     * @param ids
     * @param status
     */
    void batchUpdateStatus(List<Long> ids, Integer status);

    JyBizTaskMachineCalibrateDetailEntity selectById(Long taskId);
}
