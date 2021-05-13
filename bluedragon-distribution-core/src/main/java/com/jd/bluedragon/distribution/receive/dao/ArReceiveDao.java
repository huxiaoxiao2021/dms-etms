package com.jd.bluedragon.distribution.receive.dao;

import com.jd.bluedragon.distribution.receive.domain.ArReceive;
import com.jd.bluedragon.distribution.transport.domain.ArReceiveCondition;
import com.jd.bluedragon.distribution.transport.domain.ArReceiveVo;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: ArReceiveDao
 * @Description: 空铁提货表--Dao接口
 * @author wuyoude
 * @date 2018年01月15日 22:51:31
 *
 */
public interface ArReceiveDao extends Dao<ArReceive> {

    Integer queryArReceiveCountForWorking(ArReceiveCondition request);

    List<ArReceiveVo> queryArReceiveDetailForWorking(ArReceiveCondition request);
}
