package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.distribution.base.domain.BaseDmsStore;
import com.jd.bluedragon.distribution.base.domain.CrossPackageTagNew;
import com.jd.bluedragon.distribution.command.JdResult;

/**
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-05-09 16:30:35 周天
 */
public interface IBaseMinorJsfService {

    /**
     * 打印业务-根据库房、目的站点ID、始发分拣中心ID、始发道口类型，获取取包裹标签打印信息
     * 先调用正向业务，获取不到数据会调用逆向接口
     *
     * @param baseDmsStore      库房
     * @param targetSiteId      目的站点ID -- 必填
     * @param originalDmsId     始发分拣中心ID
     * @param originalCrossType 始发道口类型  1 -- 普通 2 -- 航空 3 -- 填仓
     * @return
     */
    JdResult<CrossPackageTagNew> queryCrossPackageTagForPrint(BaseDmsStore baseDmsStore, Integer targetSiteId, Integer originalDmsId, Integer originalCrossType);

}
