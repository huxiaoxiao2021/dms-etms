package com.jd.bluedragon.distribution.newseal.dao.impl;

import com.jd.bluedragon.distribution.newseal.domain.SealVehicleEnum;
import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.dao.PreSealVehicleDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PreSealVehicleDaoImpl
 * @Description: 预封车数据表--Dao接口实现
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
@Repository("preSealVehicleDao")
public class PreSealVehicleDaoImpl extends BaseDao<PreSealVehicle> implements PreSealVehicleDao {

    @Override
    public int updateStatusByIds(List<Long> ids, String updateUserErp, String updateUserName, Integer status) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("list", ids);
        parameter.put("updateUserErp", updateUserErp);
        parameter.put("updateUserName", updateUserName);
        parameter.put("status", status);
        parameter.put("updateTime", new Date());
        return sqlSession.update(this.nameSpace+".updateStatusByIds", parameter);
    }

    @Override
    public List<PreSealVehicle> findByCreateAndReceive(Integer createSiteCode, Integer receiveSiteCode) {
        PreSealVehicle query = new PreSealVehicle();
        query.setCreateSiteCode(createSiteCode);
        query.setReceiveSiteCode(receiveSiteCode);
        query.setStatus(SealVehicleEnum.PRE_SEAL.getCode());
        return sqlSession.selectList(this.nameSpace+".findByCreateAndReceive", query);
    }
}
