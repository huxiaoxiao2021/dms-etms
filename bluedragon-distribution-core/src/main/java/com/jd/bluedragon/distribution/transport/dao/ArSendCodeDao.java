package com.jd.bluedragon.distribution.transport.dao;

import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 * @author wuyoude
 * @ClassName: ArSendCodeDao
 * @Description: 发货批次表--Dao接口
 * @date 2017年12月28日 09:46:12
 */
public interface ArSendCodeDao extends Dao<ArSendCode> {

    /**
     * 根据单号获取批次号
     *
     * @param sendRegisterId
     * @return
     */
    List<ArSendCode> getBySendRegisterId(Long sendRegisterId);

    /**
     * 根据单号获取批次号
     *
     * @param sendRegisterIds
     * @return
     */
    List<ArSendCode> getBySendRegisterIds(List<Long> sendRegisterIds);

    /**
     * 根据单号删除批次号
     *
     * @param sendRegisterId
     * @param userCode
     * @return
     */
    int deleteBySendRegisterId(Long sendRegisterId, String userCode);

}
