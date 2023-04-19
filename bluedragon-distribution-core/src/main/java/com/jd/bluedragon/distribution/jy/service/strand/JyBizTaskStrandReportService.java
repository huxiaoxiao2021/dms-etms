package com.jd.bluedragon.distribution.jy.service.strand;

import com.jd.bluedragon.distribution.jy.dto.strand.JyStrandTaskPageCondition;
import com.jd.bluedragon.distribution.jy.strand.JyBizTaskStrandReportEntity;

import java.util.List;

/**
 * 拣运-滞留任务接口
 *
 * @author hujiping
 * @date 2023/4/19 8:28 PM
 */
public interface JyBizTaskStrandReportService {

    /**
     * 新增一条记录
     * 
     * @param entity
     * @return
     */
    Integer insert(JyBizTaskStrandReportEntity entity);

    /**
     * 根据条件更新状态
     * 
     * @param updateEntity
     */
    Integer updateStatus(JyBizTaskStrandReportEntity updateEntity);

    /**
     * 根据bizId查询一条记录
     * 
     * @param bizId
     * @return
     */
    JyBizTaskStrandReportEntity queryOneByBiz(String bizId);

    /**
     * 根据条件查询数量
     * 
     * @param pageCondition
     * @return
     */
    Integer queryTotalCondition(JyStrandTaskPageCondition pageCondition);

    /**
     * 根据查询条件分页查询数据
     * 
     * @param pageCondition
     * @return
     */
    List<JyBizTaskStrandReportEntity> queryPageListByCondition(JyStrandTaskPageCondition pageCondition);

    /**
     * 根据transPlanCode查询一条记录
     * 
     * @param transPlanCode
     * @return
     */
    JyBizTaskStrandReportEntity queryOneByTransportRejectBiz(String transPlanCode);
}
