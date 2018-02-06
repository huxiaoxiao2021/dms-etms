package com.jd.bluedragon.distribution.receive.dao;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.receive.domain.ArReceive;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

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


}
