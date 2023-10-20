package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageQuery;

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
    Long queryTaskCount(JyBizTaskCollectPackageQuery query);

    /**
     * 根据ID更新数据
     * @param entity
     * @return
     */
    Boolean updateById(JyBizTaskCollectPackageEntity entity);
    
}
