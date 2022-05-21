package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.send.BatchTransferDto;
import com.jd.bluedragon.distribution.jy.dto.send.TransferDto;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发货批次关系表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JySendCodeDao extends BaseDao<JySendCodeEntity> {

    private final static String NAMESPACE = JySendCodeDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JySendCodeEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    /**
     * 更新
     *
     * @param
     * @return
     */
    public int updateBySendCode(TransferDto dto) {
        return this.getSqlSession().update(NAMESPACE + ".updateBySendCode", dto);
    }

    /**
     * 批量更新
     *
     * @param
     * @return
     */
    public int batchUpdateBySendCode(BatchTransferDto dto) {
        return this.getSqlSession().update(NAMESPACE + ".batchUpdateBySendCode", dto);
    }
}
