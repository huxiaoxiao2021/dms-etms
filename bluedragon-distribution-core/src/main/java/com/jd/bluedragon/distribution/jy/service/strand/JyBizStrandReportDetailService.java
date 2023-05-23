package com.jd.bluedragon.distribution.jy.service.strand;

import com.jd.bluedragon.distribution.jy.strand.JyBizStrandReportDetailEntity;
import com.jd.bluedragon.distribution.jy.strand.StrandDetailSumEntity;

import java.util.List;
import java.util.Map;

/**
 * 拣运-滞留任务明细接口
 *
 * @author hujiping
 * @date 2023/4/19 8:30 PM
 */
public interface JyBizStrandReportDetailService {

    /**
     * 根据bizId查询已扫数量
     * 
     * @param bizId
     * @return
     */
    Integer queryTotalScanNum(String bizId);

    /**
     * 根据查询条件分页查询数据
     * 
     * @param paramsMap
     * @return
     */
    List<JyBizStrandReportDetailEntity> queryPageListByCondition(Map<String, Object> paramsMap);

    /**
     * 根据bizId查询容器内数量
     * 
     * @param bizId
     * @return
     */
    Integer queryTotalInnerScanNum(String bizId);

    /**
     * 根据bizId批量查询数量
     * 
     * @param bizIds
     * @return
     */
    List<StrandDetailSumEntity> queryTotalInnerScanNumByBizIds(List<String> bizIds);

    /**
     * 根据查询条件查询一条记录
     * 
     * @param condition
     * @return
     */
    JyBizStrandReportDetailEntity queryOneByCondition(JyBizStrandReportDetailEntity condition);

    /**
     * 新增一条记录
     * 
     * @param entity
     */
    Integer insert(JyBizStrandReportDetailEntity entity);

    /**
     * 根据条件取消
     * 
     * @param cancelEntity
     */
    Integer cancel(JyBizStrandReportDetailEntity cancelEntity);

    /**
     * 根据bizId查询容器
     *
     * @param bizId
     * @return
     */
    List<String> queryContainerByBizId(String bizId);
}
