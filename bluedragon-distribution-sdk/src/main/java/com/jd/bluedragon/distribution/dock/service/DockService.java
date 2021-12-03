package com.jd.bluedragon.distribution.dock.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.dock.entity.AllowedVehicleEntity;
import com.jd.bluedragon.distribution.dock.entity.DockInfoEntity;
import com.jd.bluedragon.distribution.dock.entity.DockPageQueryCondition;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.dock.service
 * @ClassName: DockService
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/11/29 10:47
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public interface DockService {

    /**
     * 获取基础资料可操作车型
     * @return
     */
    Response<List<AllowedVehicleEntity>> getAllowedVehicleTypeEnums();

    /**
     * 添加月台基础资料信息
     * @return
     */
    Response<Boolean> saveDockInfo(DockInfoEntity dockInfoEntity);

    /**
     * 分页查询月台基础资料信息
     * 根据机构ID和拣运中心
     * @return
     */
    PageDto<DockInfoEntity> queryDockInfoByPage(DockPageQueryCondition condition);

    /**
     * 根据ID进行删除
     * @param dockInfoEntity
     * @return
     */
    Response<Boolean> deleteDockInfoById(DockInfoEntity dockInfoEntity);

    /**
     * 根据ID进行更新
     * @param dockInfoEntity
     * @return
     */
    Response<Boolean> updateDockInfoById(DockInfoEntity dockInfoEntity);

}
