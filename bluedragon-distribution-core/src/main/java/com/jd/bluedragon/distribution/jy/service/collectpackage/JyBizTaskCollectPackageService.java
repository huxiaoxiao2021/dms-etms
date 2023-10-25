package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackStatusCount;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageQuery;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.CancelCollectPackageDto;

import java.util.List;

public interface JyBizTaskCollectPackageService {
    /**
     * 根据任务bizId查询 集包任务详情
     * @param bizId
     * @return
     */
    JyBizTaskCollectPackageEntity findByBizId(String bizId);

    /**
     * 根据任务箱号查询 集包任务详情
     * @param boxCode
     * @return
     */
    JyBizTaskCollectPackageEntity findByBoxCode(String boxCode);

    /**
     * 保存任务
     * @param record
     * @return
     */
    Boolean save(JyBizTaskCollectPackageEntity record);


    /**
     * 根据条件分页查询
     * @param query
     * @return
     */
    List<JyBizTaskCollectPackageEntity> pageQueryTask(JyBizTaskCollectPackageQuery query);


    /**
     * 根据站点和状态查询任务总数
     * @return
     */
    List<CollectPackStatusCount> queryTaskStatusCount(JyBizTaskCollectPackageQuery query);

    /**
     * 根据ID更新数据
     * @param entity
     * @return
     */
    Boolean updateById(JyBizTaskCollectPackageEntity entity);


    /**
     * 根据Id批量更新状态
     * @param query
     * @return
     */
    Boolean updateStatusByIds(JyBizTaskCollectPackageQuery query);

    /**
     * 根据BizId批量查询
     * @param bizIds
     * @return
     */
    List<JyBizTaskCollectPackageEntity> findByBizIds(List<String> bizIds);

    /**
     * 拣运取消单个集包能力
     * @param cancelCollectPackageDto
     * @return
     */

    boolean cancelJyCollectPackage(CancelCollectPackageDto cancelCollectPackageDto);

    /**
     * 创建集包任务并保存集包流向快照
     *
     * @param newTask
     * @param oldTask
     * @return
     */
    boolean createTaskAndFlowInfo(JyBizTaskCollectPackageEntity newTask, JyBizTaskCollectPackageEntity oldTask);
}
