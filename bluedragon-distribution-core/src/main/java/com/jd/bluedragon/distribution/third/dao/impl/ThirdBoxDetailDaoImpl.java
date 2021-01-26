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
     * @param detail 明细
     * @return 结果
     */
    @Override
    public boolean cancel(ThirdBoxDetail detail) {
        return sqlSession.update(this.nameSpace+".cancel", detail) > 0;
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

    /**
     * 获取运单或包裹装箱数据
     * 为了走索引查询包裹时也需要传入运单号
     *
     * @param tenantCode
     * @param waybillCode
     * @param packageCode
     * @return
     */
    @Override
    public List<ThirdBoxDetail> queryByWaybillOrPackage(String tenantCode, String waybillCode, String packageCode) {
        ThirdBoxDetail param = new ThirdBoxDetail();
        param.setWaybillCode(waybillCode);
        param.setPackageCode(packageCode);
        param.setTenantCode(tenantCode);
        return sqlSession.selectList(this.nameSpace+".queryByWaybillOrPackage", param);
    }

    /**
     * 获取存在数据
     *
     * @param tenantCode
     * @param startSiteId
     * @param boxCode
     * @return
     */
    @Override
    public int queryCountByBoxCode(String tenantCode, Integer startSiteId, String boxCode) {
        ThirdBoxDetail param = new ThirdBoxDetail();
        param.setStartSiteId(startSiteId);
        param.setBoxCode(boxCode);
        param.setTenantCode(tenantCode);
        return sqlSession.selectOne(this.nameSpace+".queryCountByBoxCode", param);
    }
}
