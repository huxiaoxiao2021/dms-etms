package com.jd.bluedragon.distribution.jy.service.calibrate;

import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateEntity;

import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/12/7 6:37 PM
 */
public interface JyBizTaskMachineCalibrateService {

    /**
     * 新增
     * @param entity
     * @return
     */
    int insert(JyBizTaskMachineCalibrateEntity entity);

    /**
     * 修改
     * @param entity
     * @return
     */
    int update(JyBizTaskMachineCalibrateEntity entity);

    /**
     * 根据条件查询最近一条任务
     * 
     * @param entity
     * @return
     */
    JyBizTaskMachineCalibrateEntity queryOneByCondition(JyBizTaskMachineCalibrateEntity entity);

    /**
     * 根据条件查询
     *
     * @param condition
     * @return
     */
    List<JyBizTaskMachineCalibrateEntity> queryListByCondition(JyBizTaskMachineCalibrateEntity condition);

    /**
     * 根据条件关闭任务
     * @param entity
     * @return
     */
    int closeMachineCalibrateTask(JyBizTaskMachineCalibrateEntity entity);

    /**
     * 根据id批量关闭设备任务
     *
     * @param ids
     * @return
     */
    int batchCloseByIds(List<Long> ids);
}
