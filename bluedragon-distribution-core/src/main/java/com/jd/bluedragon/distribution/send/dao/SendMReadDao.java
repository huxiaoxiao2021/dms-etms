package com.jd.bluedragon.distribution.send.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.send.domain.SendM;

import java.util.List;

/**
 * @author dudong
 * @date 2015/5/25
 */
public class SendMReadDao extends BaseDao<SendM>{
    private static final String namespace = SendMReadDao.class.getName();

    @SuppressWarnings("unchecked")
    public List<SendM> findSendMByBoxCode(SendM sendM) {
        return (List<SendM>) getSqlSession().selectList(SendMReadDao.namespace + ".findSendMByBoxCode", sendM);
    }
}
