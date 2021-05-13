package com.jd.bluedragon.distribution.receive.dao;

import com.jd.bluedragon.distribution.receive.domain.ArReceive;
import com.jd.bluedragon.distribution.transport.domain.ArReceiveCondition;
import com.jd.bluedragon.distribution.transport.domain.ArReceiveVo;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @ClassName: ArReceiveDaoImpl
 * @Description: 空铁提货表--Dao接口实现
 * @author wuyoude
 * @date 2018年01月15日 22:51:31
 *
 */
@Repository("arReceiveDao")
public class ArReceiveDaoImpl extends BaseDao<ArReceive> implements ArReceiveDao {

    @Override
    public Integer queryArReceiveCountForWorking(ArReceiveCondition request) {
        return sqlSession.insert(this.nameSpace + ".queryArReceiveCountForWorking", request);
    }

    @Override
    public List<ArReceiveVo> queryArReceiveDetailForWorking(ArReceiveCondition request) {
        return sqlSession.selectList(this.nameSpace + ".queryArReceiveDetailForWorking", request);
    }
}
