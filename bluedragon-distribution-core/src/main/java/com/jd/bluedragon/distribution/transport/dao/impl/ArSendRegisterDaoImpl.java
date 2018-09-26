package com.jd.bluedragon.distribution.transport.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.dao.ArSendRegisterDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuyoude
 * @ClassName: ArSendRegisterDaoImpl
 * @Description: 发货登记表--Dao接口实现
 * @date 2017年12月28日 09:46:12
 */
@Repository("arSendRegisterDao")
public class ArSendRegisterDaoImpl extends BaseDao<ArSendRegister> implements ArSendRegisterDao {

    @Override
    public List<ArSendRegister> getList(List<Long> ids) {
        return sqlSession.selectList(this.nameSpace + ".getList", ids);
    }

    @Override
    public int deleteByIds(List<Long> ids, String userCode) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("ids", ids);
        parameter.put("updateUser", userCode);
        return sqlSession.update(this.nameSpace + ".deleteByIdsUser", parameter);
    }

    @Override
    public List<ArSendRegister> queryStartCityInfo() {
        return sqlSession.selectList(this.nameSpace + ".queryStartCityInfo");
    }

    @Override
    public List<ArSendRegister> queryEndCityInfo() {
        return sqlSession.selectList(this.nameSpace + ".queryEndCityInfo");
    }

    @Override
    public List<ArSendRegister> queryWaitReceive(ArSendRegister arSendRegister) {
        return sqlSession.selectList(this.nameSpace + ".queryWaitReceive", arSendRegister);
    }

    @Override
    public List<ArSendRegister> getListByTransInfo(Map<String, Object> parameter) {
        return sqlSession.selectList(this.nameSpace + ".getListByTransInfo", parameter);
    }
}
