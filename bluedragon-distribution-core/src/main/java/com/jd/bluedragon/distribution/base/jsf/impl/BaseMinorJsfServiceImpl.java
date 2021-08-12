package com.jd.bluedragon.distribution.base.jsf.impl;

import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.BaseDmsStore;
import com.jd.bluedragon.distribution.base.domain.CrossPackageTagNew;
import com.jd.bluedragon.distribution.base.service.IBaseMinorJsfService;
import com.jd.bluedragon.distribution.command.JdResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-05-10 18:51:55 周一
 */
@Service("baseMinorJsfService")
public class BaseMinorJsfServiceImpl implements IBaseMinorJsfService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseMinorManager baseMinorManager;

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
    @Override
    public JdResult<CrossPackageTagNew> queryCrossPackageTagForPrint(BaseDmsStore baseDmsStore, Integer targetSiteId, Integer originalDmsId, Integer originalCrossType) {
        JdResult<CrossPackageTagNew> result = new JdResult<>();
        result.toSuccess();
        try {
            com.jd.ql.basic.domain.BaseDmsStore baseDmsStoreReal = new com.jd.ql.basic.domain.BaseDmsStore();
            BeanUtils.copyProperties(baseDmsStore, baseDmsStoreReal);
            JdResult<com.jd.ql.basic.domain.CrossPackageTagNew> crossPackageTagNewResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStoreReal, targetSiteId, originalDmsId, originalCrossType);
            if(crossPackageTagNewResult == null){
                result.toFail("调用结果为空");
                return result;
            }
            if(!crossPackageTagNewResult.isSucceed()){
                log.error("BaseMinorJsfServiceImpl.queryCrossPackageTagForPrint fail {}", JsonHelper.toJson(crossPackageTagNewResult));
                result.toFail("调用失败");
                return result;
            }
            com.jd.ql.basic.domain.CrossPackageTagNew crossPackageTagNew = crossPackageTagNewResult.getData();
            CrossPackageTagNew crossPackageTagNewData = new CrossPackageTagNew();
            BeanUtils.copyProperties(crossPackageTagNew, crossPackageTagNewData);
            result.setData(crossPackageTagNewData);
        } catch (BeansException e) {
            log.error("BaseMinorJsfServiceImpl.queryCrossPackageTagForPrint exception ", e);
            result.toFail("查询异常");
        }
        return result;
    }
}
