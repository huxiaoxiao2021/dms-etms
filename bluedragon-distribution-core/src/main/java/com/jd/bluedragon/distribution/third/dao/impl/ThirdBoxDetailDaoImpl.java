package com.jd.bluedragon.distribution.third.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.bluedragon.distribution.third.dao.ThirdBoxDetailDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: ThirdBoxDetailDaoImpl
 * @Description: 三方装箱明细表--Dao接口实现
 * @author wuyoude
 * @date 2020年01月07日 16:34:04
 *
 */
@Repository("thirdBoxDetailDao")
public class ThirdBoxDetailDaoImpl extends BaseDao<ThirdBoxDetail> implements ThirdBoxDetailDao {


    /**
     * 取消某一包裹的绑定关系
     *
     * @param tenantCode 租户编码
     * @param startSiteId 操作场地
     * @param boxCode     箱号
     * @param packageCode 包裹号
     * @return 结果
     */
    @Override
    public boolean cancel(String tenantCode, Integer startSiteId, String boxCode, String packageCode) {
        ThirdBoxDetail param = new ThirdBoxDetail();
        param.setStartSiteId(startSiteId);
        param.setBoxCode(boxCode);
        param.setPackageCode(packageCode);
        param.setTenantCode(tenantCode);
        return sqlSession.update(this.nameSpace+".cancel", param) == 1;
    }

    /**
     * 查询箱子明细
     *
     * @param tenantCode 租户编码
     * @param startSiteId 始发站点
     * @param boxCode     箱号
     * @return 结果集
     */
    @Override
    public List<ThirdBoxDetail> queryByBoxCode(String tenantCode, Integer startSiteId, String boxCode) {
        ThirdBoxDetail param = new ThirdBoxDetail();
        param.setStartSiteId(startSiteId);
        param.setBoxCode(boxCode);
        param.setTenantCode(tenantCode);
        return sqlSession.selectList(this.nameSpace+".queryByBoxCode", param);
    }
}
