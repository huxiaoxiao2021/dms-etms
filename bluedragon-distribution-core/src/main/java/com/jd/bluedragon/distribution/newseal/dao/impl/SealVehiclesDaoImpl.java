package com.jd.bluedragon.distribution.newseal.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.bluedragon.distribution.newseal.dao.SealVehiclesDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: SealVehiclesDaoImpl
 * @Description: 封车数据表--Dao接口实现
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
@Repository("sealVehiclesDao")
public class SealVehiclesDaoImpl extends BaseDao<SealVehicles> implements SealVehiclesDao {

    @Override
    public boolean updateBySealDataCode(SealVehicles sealVehicles) {
        return sqlSession.update(this.nameSpace+".updateBySealDataCode", sealVehicles) == 1;
    }

    @Override
    public List<String> findUsedTransports(Integer createSiteCode, Date startDate) {
        SealVehicles param = new SealVehicles();
        param.setCreateSiteCode(createSiteCode);
        param.setCreateTime(startDate);

        return sqlSession.selectList(this.nameSpace+".findUsedTransports", param);
    }

    @Override
    public List<String> findSealDataBySealDataCodes(List<String> sealDataCodes) {
        return sqlSession.selectList(this.nameSpace+".findSealDataBySealDataCodes", sealDataCodes);
    }
}
