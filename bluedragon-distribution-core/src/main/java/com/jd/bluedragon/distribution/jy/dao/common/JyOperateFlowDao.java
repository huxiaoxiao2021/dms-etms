package com.jd.bluedragon.distribution.jy.dao.common;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 操作流水表-dto
 * @author wuyoude
 *
 */
public class JyOperateFlowDao extends BaseDao<JyOperateFlowDto> {

    private final Logger logger = LoggerFactory.getLogger(JyOperateFlowDao.class);

    private final static String NAMESPACE = JyOperateFlowDao.class.getName();

    private static final String DB_TABLE_NAME = "jy_operate_flow";

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    public int insert(JyOperateFlowDto entity){
        if (dmsConfigManager.getPropertyConfig().isOperateFlowNewSwitch()) {
            if (entity.getId() == null) {
                if (logger.isInfoEnabled()) {
                    logger.info("JyOperateFlowDao-insert|开关打开id为空需要重新生成:entity={}", JsonHelper.toJson(entity));
                }
                entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("JyOperateFlowDao-insert|开关关闭id为空需要重新生成:entity={}", JsonHelper.toJson(entity));
            }
            entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        }
        if (logger.isInfoEnabled()) {
            logger.info("JyOperateFlowDao-insert|最终insert:entity={}", JsonHelper.toJson(entity));
        }
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

}
