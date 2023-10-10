package com.jd.bluedragon.distribution.jy.dao.common;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;

/**
 * 操作流水表-dto
 * @author wuyoude
 *
 */
public class JyOperateFlowDao extends BaseDao<JyOperateFlowDto> {

    private final static String NAMESPACE = JyOperateFlowDao.class.getName();

    private static final String DB_TABLE_NAME = "jy_operate_flow";

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    public int insert(JyOperateFlowDto entity){
        entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

}
